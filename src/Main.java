//Program made by: Jake Garza
//Program purpose: To generate relaxing animations
//School: Waterford Mott High School
//Date started: 10/30/16 (Non particles game started on this date)

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class Main {
    public static int mouseX = 0;
    public static int mouseY = 0;
    public static ArrayList<Particle> particles;
    
    //Window variables
    public static JFrame frame;
    public static BufferedImage background = null;
    public static int windowWidth = 0;
    public static int windowHeight = 0;

    public static boolean fullScreen = false;
    
    //FPS and rendering variables
    public static long targetFPS = 60;
    public static BufferStrategy bs;
    public static int fps = 0;
    public static int frames = 0;
    public static long currentTime = System.currentTimeMillis();
    public static long lastTime = currentTime;
    public static long totalTime = 0;
    public static long totalFrames = 0;
    
    public static String playerName = "";

    public static boolean isMultiplayer = false;
    public static boolean isHosting = false;
    public static Stack<String> serverInfoStack = new Stack<String>();
    public static String IP;
    public static int TCPPort = 4447;
    public static int UDPPort = 4447;
    public static int UDPMulitcastPort = 4446;
    
    public static CopyOnWriteArrayList<TCPClientHandler> clients = new CopyOnWriteArrayList<TCPClientHandler>();
    
    public static int genRand(int max, int min) {
        int rang = (max - min) + 1;
        return (int)(Math.random() * rang) + min;
    }
    
    //Random x and y values to particles
    public static void resetParticles() {
        for (int i = 0; i < particles.size(); i++) {
            int x = (int)(Math.random() * frame.getSize().getWidth());
            int y = (int)(Math.random() * frame.getSize().getHeight());
            Particle particle = new Particle(x, y, 0, 0, i);
            particles.set(i, particle);
        }
    }
    
    public static void hideCursor() {
        //Create a blank cursor and set it to the frame cursor
        BufferedImage img = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
        Cursor invisibleCursor = Toolkit.getDefaultToolkit().createCustomCursor(img, new Point(0, 0), "blank cursor");
        frame.getContentPane().setCursor(invisibleCursor);
    }
    
    public static void showCursor() {
        frame.getContentPane().setCursor(Cursor.getDefaultCursor());
    }
    
    public static BufferedImage screenShot() throws Exception {
        //Create robot object for taking screenshots
        Robot robot = new Robot();

        //Rectangle to hold dimensions of screen
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

        //Store screenshot in buffered image
        BufferedImage screenImage = robot.createScreenCapture(screenRect);

        //Return screenshot
        return screenImage;
    }
    
    
    public static void setParticles(int p) {
        particles.clear(); //Clear the array first
        
        for (int i = 0; i < p; i++) {
            particles.add(new Particle(genRand(frame.getWidth(), 0), genRand(frame.getHeight(), 0), 0, 0, i));
        }
    }
    
    
    
    public static void startParticles(int particleNumber, int particleSize, long targetFPS, boolean screenBackground) {
    	if (ParticleSettings.isDemoMode()) {
    		particleNumber = 3;
    		particleSize = 1;
    		ParticleSettings.setConnectParticles(true);
    		ParticleSettings.setTripMode(true);
    		ParticleSettings.setDistanceModifier(-100);
    	}
    	try {
            //Dress her up all nice and pertty ;)
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            //Get user settings
            ParticleSettings.setNumP(particleNumber);
            Particle.setSize(particleSize);
            Main.targetFPS = targetFPS;
            
            //If they want a screen shot background, take one and store it for later
            if (screenBackground) {
                Thread.sleep(200); //Sleep so the dialog box isn't in the shot
                background = screenShot();
            }
            

            if (ParticleSettings.getNumP() <= 0) ParticleSettings.setNumP(1);
            if (Particle.getSize() <= 0) Particle.setSize(1);
            
            //Create predefined array of particles with numP capacity
            particles = new ArrayList<Particle>(ParticleSettings.getNumP());
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, e + "\nThe Program will now exit.");
            System.exit(-1);
        }
        
        
        frame = new JFrame();
        frame.getContentPane().setBackground(Color.BLACK);
        if (fullScreen) {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setUndecorated(true);
        }
        else {
            frame.setSize(400, 400);
        }



        frame.setTitle("Particles");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        
        Thread colorThread = new Thread(new Runnable() {
            public void run() {
                for (;;) { //Forever scroll through colors
                    for (float i = 0; i < 1; i+=0.001) {
                    	try {Thread.sleep(ParticleSettings.getColorScrollDelay());} catch (Exception e) {}

                    	ParticleSettings.setColor(Color.getHSBColor(i, 1, 1));
                    }
                }
            }
        });
        colorThread.start();
        
        //If we are in multiplayer and are not hosting, prefetch data from server
        if (isMultiplayer && !isHosting) {
        	Thread serverInfoGetter = new Thread(new Runnable() {
        		public void run() {
        			while (true) {
        				try {
        					//The dummy bytes are because the last chars of the string sometimes corrupt
        					byte dummy[] = new byte[64];
        					
        					Color currentColor = ParticleSettings.getColor();
        					int r = currentColor.getRed();
        					int g = currentColor.getGreen();
        					int b = currentColor.getBlue();

        					int distMod = ParticleSettings.getDistanceModifier();

                            //Send server our information and get a response on what the next frame should look like
        					String info = UDPHelper.getGameInfoFromServer(mouseX + " " + mouseY + " " + playerName + " " + r + " " + g + " " + b + " " + distMod + new String(dummy));
        					serverInfoStack.add(info);
        					//System.out.println(info);

                            //Run twice as fast as our FPS
        					Thread.sleep((1000/Main.targetFPS) / 2);
        				} catch (Exception e) {
        					e.printStackTrace();
        				}
        			}
        		}
        	});
        	serverInfoGetter.start();
        }
        
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
        Canvas canvas = new Canvas();
        canvas.setFocusable(false);
        frame.add(canvas);
        canvas.createBufferStrategy(2);
        bs = canvas.getBufferStrategy();
        
        //Set global window width and height variables
        windowWidth = frame.getWidth();
        windowHeight = frame.getHeight();
        
        
        setParticles(ParticleSettings.getNumP());
        
        java.util.Timer timer = new java.util.Timer();
        timer.scheduleAtFixedRate(new java.util.TimerTask() {
        	public void run() {
        		lastTime = currentTime;
        		currentTime = System.currentTimeMillis();

        		totalTime += currentTime - lastTime;

        		//When a second passes
        		if (totalTime > 1000) {
        			totalTime -= 1000;
        			fps = frames;
        			frames = 0;
        		}
        		frames++;

        		//Add one to amount of frames that have passed
        		totalFrames++;

        		Graphics2D g = (Graphics2D)bs.getDrawGraphics();
        		BasicStroke defaultStroke = (BasicStroke)g.getStroke();
        		g.setColor(Color.BLACK);

        		//Clear the last frame if not in trip mode
        		if (!ParticleSettings.isTripMode() || ParticleSettings.clearNextFrame()) {
        			g.clearRect(0, 0, frame.getWidth(), frame.getHeight());
        			ParticleSettings.clearNextFrame(false);
        		}

        		//If background isn't null, we took a screenshot. We will draw it
        		if (background != null && !ParticleSettings.isTripMode())
        			g.drawImage(background, 0, 0, null);

        		//Set particle colors
        		g.setColor(ParticleSettings.getColor());

        		//Loop through particle array and render them all
        		if (!isMultiplayer) {
        			for (int i = 0; i < particles.size(); i++) {
        				try {
        					//Get particle from array
        					Particle p = particles.get(i);


        					if (ParticleSettings.particleTrails()) {
        						g.setStroke(new BasicStroke(Particle.getSize()/2));

        						//Don't draw line if off screen
        						if (p.x > 0 && p.x < windowWidth && p.y > 0 && p.y < windowHeight) {
        							g.drawLine((int)p.x+Particle.getSize()/2, (int)p.y+Particle.getSize()/2, (int)Math.abs(p.x - p.velocityX*4), (int)Math.abs(p.y - p.velocityY*4));
        						}

        						//After reset the stroke and color
        						g.setStroke(defaultStroke);
        					}

        					//Draw particles
        					g.setColor(ParticleSettings.getColor());
        					g.fillRect((int)p.x, (int)p.y, Particle.getSize(), Particle.getSize());


        					//If connect particles is true, draw a line between particles
        					if (ParticleSettings.connectingParticles()) {
        						Particle p2 = null;

        						//If i isn't 0, set p2 to the previous index, if it is, set p2 to the last index
        						if (i != 0)
        							p2 = particles.get(i-1);
        						else
        							p2 = particles.get(particles.size()-1);

        						g.drawLine((int)p.x+(Particle.getSize()/2), (int)p.y+(Particle.getSize()/2), (int)p2.x+(Particle.getSize()/2), (int)p2.y+(Particle.getSize()/2));
        					}

        					//Attract particle to mouse and calculate velocity in integrate method
        					if (ParticleSettings.isAttraction()) p.attract(mouseX-(Particle.getSize()/2), mouseY-(Particle.getSize()/2));
        					p.integrate(); 
        				} catch (Exception e) {}
        			}
        		}
        		//If we are in multiplayer and not hosting, use the info from server to draw
        		else if (isMultiplayer && !isHosting) {
        		    String toDraw = "";
        			try {
                        if (!serverInfoStack.empty())
        				toDraw = serverInfoStack.pop();
        				
        				String splitByLine[] = toDraw.split("\n");

        				//Color of other clients
        				Color clientColor = new Color(255, 255, 255);
        				for (int i = 0; i < splitByLine.length; i++) {
        					String line = splitByLine[i];
        					if (line.contains("COLOR:")) {
        						line = line.replace("COLOR:", "");
        						
        						String lineColors[] = line.split(" ");
        						int red = Integer.parseInt(lineColors[0]);
                				int green = Integer.parseInt(lineColors[1]);
                				int blue = Integer.parseInt(lineColors[2]);

                                clientColor = new Color(red, green, blue);
                                g.setColor(clientColor);
        					}
        					else {
        						int x = Integer.parseInt(line.split(" ")[0]);
            					int y = Integer.parseInt(line.split(" ")[1]);

            					g.fillRect(x, y, 3, 3);
        					}
        				}
        			} catch (Exception e) {
        				e.printStackTrace();
        			}
        		}
                
                
                //Set text colors
                g.setColor(Color.WHITE);
                
                //Draw status and hot keys
                g.drawString("Particles: " + Main.particles.size(), 5, 15);
                g.drawString("Particle size: " + Particle.getSize(), 5, 15*2);
                g.drawString("Distance modifier: " + ParticleSettings.getDistanceModifier(), 5, 15*3);
                g.drawString("Reset: R", 5, 15*4);
                g.drawString("Show settings window: Q", 5, 15*5);
                g.drawString("Increase distance modifier: W", 5, 15*6);
                g.drawString("Decrease distance modifier: S", 5, 15*7);
                g.drawString("Increase distance modifier by 25: +", 5, 15*8);
                g.drawString("Decrease disance modifier by 25: -", 5, 15*9);
                g.drawString("Attraction: A", 5, 15*10);
                g.drawString("Trip mode: T", 5, 15*11);
                
                if (ParticleSettings.isAttraction())
                    g.setColor(Color.GREEN);
                else
                    g.setColor(Color.RED);
                
                g.drawString("" + ParticleSettings.isAttraction(), 160, 15*10);
                
                if (ParticleSettings.isTripMode())
                    g.setColor(Color.GREEN);
                else
                    g.setColor(Color.RED);
                
                g.drawString("" + ParticleSettings.isTripMode(), 160, 15*11);
                
                g.setColor(ParticleSettings.getColor());
                g.drawString("FPS: " + fps, 5, 15*12);
                g.drawString("Created by: Jake Garza", 5, 15*13);
                
                
                //Draw exit box
                g.drawRect(frame.getWidth()-101, 0, 100, 50);
                g.drawString("EXIT", frame.getWidth()-60, 30);
                
                
                //Demo mode code, moving mouse and changing random settings
                if (ParticleSettings.isDemoMode()) {
                	//Mouse x and y go in a circle with radius of 100 and the center is in the middle of the screen
                	mouseX = (int)(Math.cos(ParticleSettings.getCircleInc()) * 100) + windowWidth/2;
                	mouseY = (int)(Math.sin(ParticleSettings.getCircleInc()) * 100) + windowHeight/2;

                	ParticleSettings.incrementCircle(0.02);
                	
                	if (totalFrames % (Main.targetFPS*40) == 0) {
                		int connect = genRand(1, 0);

                		ParticleSettings.clearNextFrame(true);
                		if (connect == 0) ParticleSettings.setConnectParticles(false);//connectParticles = false;
                		else ParticleSettings.setConnectParticles(true);//connectParticles = true;
                		
                		if (connect == 1)
                			setParticles(genRand(4, 2));
                		else
                			setParticles(genRand(100, 50));
                	}

                	if (ParticleSettings.isCursorShowing()) {
                		ParticleSettings.setCursorShowing(!ParticleSettings.isCursorShowing());
                		hideCursor();
                	}
                }

                //Set cursor setting each frame
                if (ParticleSettings.isCursorShowing())
                	showCursor();
                else
                	hideCursor();

                //Dispose graphics object and show what we just drew
                g.dispose();
                bs.show();

            }
        }, 0, (long)(1000/targetFPS)); //1000/targetFPS, we ask the program to run at about the target FPS


        //Mouse listener to set global variables to mouse position
        canvas.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent arg0) {}
            public void mouseMoved(MouseEvent e) {
                //If we are in demo mode, the user will not control this
                if (!ParticleSettings.isDemoMode()) {
                    mouseX = e.getX();
                    mouseY = e.getY();
                }
            }});
        
        //A click listener to see if the user clicks the exit button
        canvas.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                Rectangle r1 = new Rectangle(frame.getWidth()-101, 0, 101, 51);
                Rectangle r2 = new Rectangle(e.getX(), e.getY(), 1, 1);
                if (r1.intersects(r2))
                    System.exit(0);
            }
        });
        
        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                char key = (e.getKeyChar() + "").toLowerCase().charAt(0); //Convert char to lower case
                
                if (key == 'w')
                	ParticleSettings.incDistMod(1);
                else if (key == 's')
                	ParticleSettings.incDistMod(-1);
                else if (key == 'r') {
                    resetParticles(); //Loop through array and reset all particles
                    ParticleSettings.clearNextFrame(true); //If we are in trip mode, we want to clear the last frame
                }
                else if (key == '+')
                	ParticleSettings.incDistMod(25);
                else if (key == '-')
                	ParticleSettings.incDistMod(-25);
                else if (key == 'a')
                	ParticleSettings.setAttraction(!ParticleSettings.isAttraction());
                else if (key == 't')
                	ParticleSettings.setTripMode(!ParticleSettings.isTripMode());
            	else if  (key == 'q')
            		SettingsWindow.showSettings();
            }
        });
        
        frame.requestFocus();
    }
}