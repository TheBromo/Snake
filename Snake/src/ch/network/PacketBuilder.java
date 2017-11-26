package ch.network;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class PacketBuilder {


    private int checkNumber, waitingTime = 20000;
    private Packet newestPacket;
    private NewNetwork mNetwork;

    public PacketBuilder(NewNetwork mNetwork) {
        this.mNetwork = mNetwork;
        checkNumber = 0;
    }

    public ByteBuffer createPacket(InetAddress receiver, ByteBuffer buffer, PacketType packetType) throws UnknownHostException {

        Packet packet = new Packet(receiver, buffer, checkNumber, packetType);
        packet = fillPacket(packet);

        checkNumber++;
        return packet.getData();
    }


    public ByteBuffer createPacket(Packet packet, ByteBuffer byteBuffer) throws UnknownHostException {
        //for response
        Packet responsePacket = new Packet(packet.getReceiver(), byteBuffer, checkNumber, PacketType.RESPONSE);
        responsePacket = fillPacket(packet, responsePacket);

        checkNumber++;
        return responsePacket.getData();
    }


    public ByteBuffer createPacket(Packet packet) throws UnknownHostException {
        //for resending


        checkNumber++;
        return packet.getData();
    }


    public Packet fillPacket(Packet packet) throws UnknownHostException {

        HashMap<InetAddress, Tail> users = Lobby.getUsers();
        HashMap<InetAddress, Coordinates> heads = Lobby.getHeads();
        ByteBuffer data = packet.getData();

        if (packet.getType() == PacketType.NAME) {
            data = buildNamePacket(data, users);
        } else if (packet.getType() == PacketType.COORDINATES) {
            data = buildCoordinatesPacket(data, heads, users);
        } else if (packet.getType() == PacketType.DIRECTION) {
            data = buildDirectionPacket(data, heads);
        } else if (packet.getType() == PacketType.CONNECTION) {
            data = buildConnectionPacket(data, packet);
        }
        packet.setData(data);
        newestPacket = packet;
        return packet;
    }

    public Packet fillPacket(Packet packet, Packet responsePacket) {

        ByteBuffer data = responsePacket.getData();
        if (responsePacket.getType() == PacketType.RESPONSE) {
            buildResponsePacket(data, packet);
        } else {
            System.out.println("Wrong packetFiller ");
        }
        responsePacket.setData(data);

        return responsePacket;
    }

    private ByteBuffer buildResponsePacket(ByteBuffer data, Packet packet) {
        data.putInt(packet.getCheckNumber());
        return data;
    }

    private ByteBuffer buildConnectionPacket(ByteBuffer data, Packet packet) {
        long startTime = packet.getTimeCreated() + waitingTime;
        data.putLong(startTime);
        mNetwork.setStartTime(startTime);
        return data;
    }


    private ByteBuffer buildDirectionPacket(ByteBuffer data, HashMap<InetAddress, Coordinates> heads) throws UnknownHostException {
        //new Direction(char)
        data.putChar(heads.get(InetAddress.getByName(InetAddress.getLocalHost().getHostAddress())).nextDir);

        return data;

    }

    private ByteBuffer buildCoordinatesPacket(ByteBuffer data, HashMap<InetAddress, Coordinates> heads, HashMap<InetAddress, Tail> users) throws UnknownHostException {

        // x, y, alive
        Coordinates you = heads.get(InetAddress.getByName(InetAddress.getLocalHost().getHostAddress()));
        data = addCoordinates(data, you);

        boolean alive = users.get(InetAddress.getByName(InetAddress.getLocalHost().getHostAddress())).isAlive();
        data = addBoolean(data, alive);

        return data;
    }

    private ByteBuffer buildNamePacket(ByteBuffer data, HashMap<InetAddress, Tail> users) throws UnknownHostException {

        /* length, string,*/
        String userName = users.get(InetAddress.getLocalHost().getHostAddress()).getName();
        return addString(data, userName);
    }


    private ByteBuffer addCoordinates(ByteBuffer data, Coordinates coordinates) {

        data.putInt(coordinates.newX);
        data.putInt(coordinates.newY);

        return data;
    }

    private ByteBuffer addBoolean(ByteBuffer data, boolean bool) {

        if (bool) {
            data.putInt(1);
        } else {
            data.putInt(0);
        }
        return data;
    }


    private ByteBuffer addString(ByteBuffer data, String string) {

        byte[] stringAsBytes = string.getBytes();
        data.putInt(stringAsBytes.length);
        data.put(stringAsBytes);

        return data;
    }

    public Packet getNewestPacket() {
        return newestPacket;
    }
}
