//Purpose of class is to be a handler for incoming clients
//We will know if a person closes their program because we will get an exception while waiting for input

import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class TCPClientHandler extends Thread {
	private BufferedReader br = null;
	private int maxNameLength = 18;
	private String name = "NONAME";
	private Color color = new Color(255, 255, 255);
	private CopyOnWriteArrayList<TCPClientHandler> clients = Main.clients;
	
	private ArrayList<Particle> particles = new ArrayList<Particle>();

	public TCPClientHandler(Socket socket) throws Exception {
		br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		//Init particles arraylist
		for (int i = 0; i < 1000; i++) {
			int x = (int)(Math.random() * 300);
            int y = (int)(Math.random() * 300);
			particles.add(new Particle(x, y, 0, 0, i));
		}
			
		

		//Start the thread of this
		this.start();
	}

	public String getPlayerName() {
		return name;
	}

	public void setPlayerName(String name) throws Exception {
		if (name.length() > maxNameLength)
			throw new Exception("Client name was too long.");
		
		this.name = name;
	}
	
	public Color getColor() { return color; }
	
	public void setColor(Color color) { this.color = color; }
	
	//Random x and y values to particles
    public void resetParticles() {
        for (int i = 0; i < particles.size(); i++) {
            int x = (int)(Math.random() * Main.frame.getSize().getWidth());
            int y = (int)(Math.random() * Main.frame.getSize().getHeight());
            Particle particle = new Particle(x, y, 0, 0, i);
            particles.set(i, particle);
        }
    }
    
    public ArrayList<Particle> getParticles() {
    	return particles;
    }

	public void run() {
		while (true) {
			String input = null;
			try {
				//Get input from client
				input = br.readLine();

				//Check for commands from client
				if (input.contains("NAME:")) {
					input = input.replace("NAME:", "").replace(":", "").replace(" ", "");
					setPlayerName(input);
					Main.clients.add(this);
				}
				
				System.out.println(input);
			}
			catch (Exception e) {
				//e.printStackTrace();
                System.out.println(getPlayerName() + " has left.");
				clients.remove(this);
				return;
			}
		}
	}
}