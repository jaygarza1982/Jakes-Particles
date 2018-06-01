import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

@SuppressWarnings("serial")
public class Window extends JFrame {

	private JPanel contentPane;
	private JTextField txtNumParticles;
	private JTextField txtParticleSize;
	private JTextField txtTargetFPS;
	private JCheckBox cbUseScreenBG;
	private JCheckBox cbFullScreen;
	private JButton btnStart;
	private static Window frame;
	static DefaultListModel<String> model = new DefaultListModel<String>();
	static JList<String> list;
	private JTextField txtJoinName;

	private ServerSocket serverSocket;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Thread receiveThread = new Thread(new MulticastReceiver());
					receiveThread.start();
					frame = new Window();
					frame.setVisible(true);
					
					//Java 6 does not support later version of TLS
					if (System.getProperty("java.version").startsWith("1.6")) {
						System.setProperty("https.protocols", "TLSv1");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	/**
	 * Create the frame.
	 */
	public Window() {
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
		setTitle("Jake Garza's Particle Program");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 547, 385);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNumberOfParticles = new JLabel("Number of Particles: ");
		lblNumberOfParticles.setBounds(10, 11, 111, 14);
		contentPane.add(lblNumberOfParticles);

		txtNumParticles = new JTextField();
		txtNumParticles.setText("10000");
		txtNumParticles.setBounds(125, 8, 105, 20);
		contentPane.add(txtNumParticles);
		txtNumParticles.setColumns(10);

		JLabel lblParticleSize = new JLabel("Particle size: ");
		lblParticleSize.setBounds(10, 36, 111, 14);
		contentPane.add(lblParticleSize);

		txtParticleSize = new JTextField();
		txtParticleSize.setText("3");
		txtParticleSize.setColumns(10);
		txtParticleSize.setBounds(125, 33, 105, 20);
		contentPane.add(txtParticleSize);

		JLabel lblTargetFps = new JLabel("Target FPS: ");
		lblTargetFps.setBounds(10, 61, 111, 14);
		contentPane.add(lblTargetFps);

		cbUseScreenBG = new JCheckBox("Use screen as background");
		cbUseScreenBG.setBounds(6, 82, 224, 23);
		contentPane.add(cbUseScreenBG);

		cbFullScreen = new JCheckBox("Fullscreen window");
		cbFullScreen.setSelected(true);
		cbFullScreen.setBounds(6, 107, 224, 23);
		contentPane.add(cbFullScreen);

		txtTargetFPS = new JTextField();
		txtTargetFPS.setText("60");
		txtTargetFPS.setColumns(10);
		txtTargetFPS.setBounds(125, 58, 105, 20);
		contentPane.add(txtTargetFPS);

		btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					int particleNumber = Integer.parseInt(txtNumParticles.getText());
					int particleSize = Integer.parseInt(txtParticleSize.getText());
					long targetFPS = Long.parseLong(txtTargetFPS.getText());
					boolean screenBackground = cbUseScreenBG.isSelected();
					Main.fullScreen = cbFullScreen.isSelected();

					frame.dispose(); //Close this window without exiting program

					Main.startParticles(particleNumber, particleSize, targetFPS, screenBackground);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.getMessage());
				}
			}
		});
		btnStart.setBounds(10, 142, 135, 23);
		contentPane.add(btnStart);

		JButton btnCheckForUpdates = new JButton("Check for updates");
		btnCheckForUpdates.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (Updater.isUpdate()) {
						//File chooser object with jar files as default file to save to
						JFileChooser jfc = new JFileChooser();
						jfc.setDialogTitle("Select a save loction for the updated version.");
						jfc.setFileFilter(new FileNameExtensionFilter("JAR Files", "jar"));
						jfc.showOpenDialog(null);

						File toSave = jfc.getSelectedFile();

						//If the name of the file dosen't have .jar at the end, add it to the file name
						if (!toSave.getName().toLowerCase().endsWith(".jar"))
							toSave = new File(toSave.getAbsolutePath() + ".jar");

						Updater.downloadFile("https://github.com/jaygarza1982/Jakes-Particles/raw/master/bin/Particles.jar", toSave.getAbsolutePath());
					}
					else {
						JOptionPane.showMessageDialog(null, "You are running the latest version.");
					}
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, e.getMessage());
				}
			}
		});
		btnCheckForUpdates.setBounds(10, 313, 135, 23);
		contentPane.add(btnCheckForUpdates);

		JButton btnDemoMode = new JButton("Demo mode");
		btnDemoMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Turn demo mode on
				//Main.demoMode = true;
				ParticleSettings.setDemoMode(true);

				//Click the start button
				btnStart.doClick();
			}
		});
		btnDemoMode.setBounds(289, 7, 135, 23);
		contentPane.add(btnDemoMode);

		JLabel lblOpenServers = new JLabel("Open Servers");
		lblOpenServers.setBounds(155, 204, 84, 14);
		contentPane.add(lblOpenServers);

		list = new JList<String>(model);
		list.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent evt) {
		        if (evt.getClickCount() == 2) {
		           
		            String ip = list.getSelectedValue().replace("/", "");
		            ip = ip.replace(" ", "");
		            
		            //TODO: Send TCP message to the server containing the player name
                    try {
                    	Socket socket = new Socket(ip, Main.TCPPort);
                        PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
                        final BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        pw.println("NAME:" + txtJoinName.getText());
                        Main.playerName = txtJoinName.getText();
                        Main.isMultiplayer = true;
                        Main.isHosting = false;
                        Main.IP = ip;

                        //TODO: Uncomment this in release
                        Main.fullScreen = cbFullScreen.isSelected();

                        Main.startParticles(0, Integer.parseInt(txtParticleSize.getText()), 30, cbUseScreenBG.isSelected());
                        
                        //Start a new thread to let the server know we are still here
                        Thread serverConnectionThread = new Thread(new Runnable() {
                        	public void run() {
                        		 try {
									br.readLine();
								} catch (IOException e) {
									e.printStackTrace();
								}
                        	}
                        });
                        serverConnectionThread.start();
                        
                        frame.dispose();
                       
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, e.getMessage());
                    }

		            //System.out.println(ip);
		        }
		    }
		});
		list.setBounds(155, 229, 366, 107);
		contentPane.add(list);

		JButton btnHost = new JButton("Host");
		btnHost.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					new MulticastPublisher().multicast("");
                    serverSocket = new ServerSocket(Main.TCPPort);
                    Main.isHosting = true;
                    
                    Thread udpServerThread = new UDPReceiver();
                    udpServerThread.start();

                    Thread serverThread = new Thread(new Runnable() {
                        public void run() {
                            while (true) {
                                try {
                                    new TCPClientHandler(serverSocket.accept());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

                    serverThread.start();
                    
                    
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, e.getMessage());
				}
			}
		});
		btnHost.setBounds(386, 175, 135, 23);
		contentPane.add(btnHost);
		
		txtJoinName = new JTextField();
		txtJoinName.setEditable(false);
		txtJoinName.setText("Player" + (int)(Math.random()*500));
		txtJoinName.setBounds(396, 201, 125, 20);
		contentPane.add(txtJoinName);
		txtJoinName.setColumns(10);
		
		JLabel lblPlayerName = new JLabel("Player Name");
		lblPlayerName.setBounds(306, 204, 84, 14);
		contentPane.add(lblPlayerName);
	}
}
