import java.awt.Color;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class UDPReceiver extends Thread {
	DatagramSocket serverSocket;
	byte receiveData[] = new byte[65507];
    byte sendData[] = new byte[65507];
    
	public void run() {
		try {
			serverSocket = new DatagramSocket(Main.UDPPort);

			while (true) {
				try {
					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					serverSocket.receive(receivePacket);
					String receiveStr = new String(receivePacket.getData()).trim();
					
					String receiveStrSplit[] = receiveStr.split(" ");
					
					String name = receiveStrSplit[2];
					int mouseX = Integer.parseInt(receiveStrSplit[0]);
					int mouseY = Integer.parseInt(receiveStrSplit[1]);
					
					int r = Integer.parseInt(receiveStrSplit[3]);
					int g = Integer.parseInt(receiveStrSplit[4]);
					int b = Integer.parseInt(receiveStrSplit[5]);

                    int prevDistMod = ParticleSettings.getDistanceModifier();
					int distMod = Integer.parseInt(receiveStrSplit[6]);
					
					//System.out.println(receiveStr);
					
					for (int i = 0; i < Main.clients.size(); i++) {
					    TCPClientHandler currentClient = Main.clients.get(i);

						if (name.equals(currentClient.getPlayerName())) {
                            currentClient.setColor(new Color(r, g, b));

							ArrayList<Particle> particles = currentClient.getParticles();
							
							for (Particle p : particles) {
                                ParticleSettings.setDistanceModifier(distMod);
								if (ParticleSettings.isAttraction()) p.attract(mouseX-(Particle.getSize()/2), mouseY-(Particle.getSize()/2));
		                        p.integrate(); 
							}

							ParticleSettings.setDistanceModifier(prevDistMod);
						}
					}
					
					

					//System.out.println("Server: " + receiveStr);
					
					
					StringBuilder sbToSend = new StringBuilder();
					
					for (int i = 0; i < Main.clients.size(); i++) {
						TCPClientHandler client = Main.clients.get(i);
						Color currentClientColor = client.getColor();
						r = currentClientColor.getRed();
						g = currentClientColor.getGreen();
						b = currentClientColor.getBlue();
						
						sbToSend.append("COLOR:" + r + " " + g + " " + b + "\n");
						
						ArrayList<Particle> particles = client.getParticles();
						
						for (int j = 0; j < particles.size(); j++) {
							Particle p = particles.get(j);
							
							sbToSend.append((int)p.x + " " + (int)p.y + "\n");
						}
					}

					byte[] sendData = sbToSend.toString().getBytes();
					//System.out.println("Length: " + sendData.length);
					
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
					serverSocket.send(sendPacket);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
}