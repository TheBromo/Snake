package ch.network;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class PacketReader {
    private NewNetwork mNetwork;

    public PacketReader(NewNetwork newNetwork){
        this.mNetwork=newNetwork;
    }

    //used to get info out of a packet
    public Packet readPacket(InetAddress receiver, ByteBuffer data, ByteBuffer answer) throws UnknownHostException {
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
            readCoordinatesPacket();
        } else if (packet.getType() == PacketType.DIRECTION) {
            readDirectionPacket();
        } else if (packet.getType() == PacketType.CONNECTION) {
            readConnectionPacket();
        } else if (packet.getType() == PacketType.RESPONSE) {
            readResponsePacket();
        }
    }

    private void readNamePacket(HashMap<InetAddress, Tail> users, ByteBuffer data, Packet packet) {
        //gets the strings length
        int length = data.getInt();
        //gets the string as bytes
        byte[] name = new byte[length];
        data.get(name);
        //converts the bytes to string
        String str = new String(name, StandardCharsets.UTF_8);
        users.get(packet.getReceiver()).setName(str);
    }

    private void readCoordinatesPacket() {

    }

    private void readDirectionPacket() {

    }

    private void readConnectionPacket() {

    }


    private void readResponsePacket(){}
}
