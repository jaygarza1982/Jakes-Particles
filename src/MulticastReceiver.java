//Purpose of class is to receive a packet from the MulticastPublisher

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastReceiver extends Thread {
    protected MulticastSocket socket = null;
    protected byte[] buf = new byte[256];

    public void run() {
        try {
            socket = new MulticastSocket(Main.UDPMulitcastPort);
            InetAddress group = InetAddress.getByName("224.0.2.60");
            socket.joinGroup(group);
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                
                //Window.textArea.setText(Window.textArea.getText() + packet.getAddress() + " " + received + "\n");
                String toAdd = packet.getAddress() + " " + received;
                if (!Window.model.contains(toAdd)) {
                	Window.model.addElement(toAdd);
                    Window.list.setModel(Window.model);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}