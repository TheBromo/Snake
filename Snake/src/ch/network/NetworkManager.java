package ch.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

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
        mNetwork.receivePacket();
    }

    public void resend() throws IOException {
        for (Packet packet:mNetwork.getSentPackets()) {
            mNetwork.resendPacket(packet);
        }
    }

    public void receive() throws IOException {
        mNetwork.receivePacket();
    }

    public void sendDirections() throws IOException {
        for (InetAddress key : Lobby.getHeads().keySet()) {
            mNetwork.sendPacket(key, PacketType.DIRECTION);
        }
    }

    public void sendCoordinates() throws IOException {
        for (InetAddress key : Lobby.getHeads().keySet()) {
            mNetwork.sendPacket(key, PacketType.COORDINATES);
        }
    }

    public void sendNames() throws IOException {
        for (InetAddress key : Lobby.getHeads().keySet()) {
            mNetwork.sendPacket(key, PacketType.NAME);
        }
    }

}
