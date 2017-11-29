package ch.network.netzwerk;

import ch.network.Lobby;

import java.io.IOException;
import java.net.InetAddress;

public class NetworkManager {

    private NewNetwork mNetwork;
    private long last;

    public NetworkManager() {
        try {
            mNetwork = new NewNetwork();
            mNetwork.waitForConnection(Lobby.getUsers(), Lobby.getHeads());
            sendNames();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateCoordinates() throws IOException {
        sendCoordinates();
    }

    public void getData() throws IOException {
        mNetwork.receivePackets();
    }

    public void resend() throws IOException {
        for (Packet packet : mNetwork.getSentPackets()) {
            mNetwork.resendPacket(packet);
        }
    }

    public void receive() throws IOException {
        mNetwork.receivePackets();
    }

    public void sendDirections() throws IOException {
        for (InetAddress key : Lobby.getHeads().keySet()) {
         //   if (!key.equals(InetAddress.getByName(InetAddress.getLocalHost().getHostAddress()))) {
                mNetwork.sendPacket(key, PacketType.DIRECTION);
           // }
        }
    }

    public void sendCoordinates() throws IOException {
        for (InetAddress key : Lobby.getHeads().keySet()) {
            //if (!key.equals(InetAddress.getByName(InetAddress.getLocalHost().getHostAddress()))) {

                mNetwork.sendPacket(key, PacketType.COORDINATES);

            //}
        }
    }

    public void sendNames() throws IOException {
        for (InetAddress key : Lobby.getHeads().keySet()) {
            //if (!key.equals(InetAddress.getByName(InetAddress.getLocalHost().getHostAddress()))) {
                mNetwork.sendPacket(key, PacketType.NAME);
            //}
        }
    }

}
