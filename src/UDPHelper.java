import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPHelper {
    public static String getGameInfoFromServer(String toSendStr) throws Exception {
        DatagramSocket clientSocket = new DatagramSocket();

        String toSend = toSendStr;

        byte[] sendData = toSend.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(Main.IP), Main.UDPPort);
        clientSocket.send(sendPacket);

        byte receiveData[] = new byte[65507];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        clientSocket.close();

        String receiveStr = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-8");
        return receiveStr;
    }
}