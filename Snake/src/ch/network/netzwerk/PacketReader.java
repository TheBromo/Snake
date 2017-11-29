package ch.network.netzwerk;

import ch.network.Entities.Coordinates;
import ch.network.Lobby;
import ch.network.Entities.Tail;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class PacketReader {

    private NewNetwork mNetwork;

    public PacketReader(NewNetwork newNetwork) {
        this.mNetwork = newNetwork;
    }

    //used to get info out of a packet
    public Packet readPacket(InetAddress receiver, ByteBuffer data) throws UnknownHostException {
        Packet packet = new Packet(receiver, data);
        readData(packet);
        // Put in PacketBuilder
        // answer = createResponse;
        //dont create response when packet is a response packet
        return packet;
    }

    private void readData(Packet packet) {
        HashMap<InetAddress, Tail> users = Lobby.getUsers();
        HashMap<InetAddress, Coordinates> heads = Lobby.getHeads();
        ByteBuffer data = packet.getData();

        if (packet.getType() == PacketType.NAME) {
            readNamePacket(users, data, packet);
        } else if (packet.getType() == PacketType.COORDINATES) {
            readCoordinatesPacket(packet, users, heads, data);
        } else if (packet.getType() == PacketType.DIRECTION) {
            readDirectionPacket(packet, heads, data);
        } else if (packet.getType() == PacketType.CONNECTION) {
            readConnectionPacket(data);
        } else if (packet.getType() == PacketType.RESPONSE) {
            readResponsePacket(packet, data);
        }
    }

    private void readNamePacket(HashMap<InetAddress, Tail> users, ByteBuffer data, Packet packet) {
        users.get(packet.getReceiver()).setName(readString(data));
    }

    private void readCoordinatesPacket(Packet packet, HashMap<InetAddress, Tail> users, HashMap<InetAddress, Coordinates> heads, ByteBuffer data) {
        readCoordinates(data, heads.get(packet.getReceiver()));
        users.get(packet.getReceiver()).setAlive(readBoolean(data));
    }

    private void readDirectionPacket(Packet packet, HashMap<InetAddress, Coordinates> heads, ByteBuffer data) {
        char nextDir = data.getChar();
        heads.get(packet.getReceiver()).setNextDir(nextDir);

    }

    private void readConnectionPacket(ByteBuffer data) {
        mNetwork.setStartTime(data.getLong());
        mNetwork.setStartTimeReceived(true);


    }


    private void readResponsePacket(Packet packet, ByteBuffer data) {
        int checkNumber = data.getInt();
        ArrayList<Packet> packets = mNetwork.getSentPackets();
        packets.removeIf(p -> checkNumber == p.getCheckNumber() && p.getReceiver().equals(packet.getReceiver()));
        mNetwork.setSentPackets(packets);

    }


    private String readString(ByteBuffer data) {
        //gets the strings length
        int length = data.getInt();
        //gets the string as bytes
        byte[] name = new byte[length];
        data.get(name);
        //converts the bytes to string
        String str = new String(name, StandardCharsets.UTF_8);
        return str;
    }

    private void readCoordinates(ByteBuffer data, Coordinates coordinates) {
        coordinates.setNewX(data.getInt());
        coordinates.setNewY(data.getInt());
    }

    private boolean readBoolean(ByteBuffer data) {
        if (data.getInt() == 1) {
            return true;
        } else {
            return false;
        }
    }

}
