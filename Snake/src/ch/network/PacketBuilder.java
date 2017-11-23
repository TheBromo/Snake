package ch.network;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class PacketBuilder {


    private int checkNumber;

    public PacketBuilder() {
        checkNumber = 0;
    }

    public ByteBuffer createPacket(InetAddress receiver, ByteBuffer buffer, PacketType packetType) throws UnknownHostException {

        Packet packet = new Packet(receiver, buffer, checkNumber, packetType);
        packet.setData(fillPacket(packet));

        checkNumber++;
        return buffer;
    }

    public ByteBuffer fillPacket(Packet packet) throws UnknownHostException {
        HashMap<InetAddress, Tail> users = Lobby.getUsers();
        HashMap<InetAddress, Coordinates> heads = Lobby.getHeads();
        ByteBuffer data = packet.getData();

        if (packet.getType() == PacketType.NAME) {
            data = buildNamePacket(data, users);
        } else if (packet.getType() == PacketType.COORDINATES) {

        } else if (packet.getType() == PacketType.DIRECTION) {

        } else if (packet.getType() == PacketType.RESEND) {

        } else if (packet.getType() == PacketType.CONNECTION) {

        }

        return data;
    }

    private ByteBuffer buildNamePacket(ByteBuffer data, HashMap<InetAddress, Tail> users) throws UnknownHostException {

        /* length, string,*/
        String userName = users.get(InetAddress.getLocalHost().getHostAddress()).getName();
        return addString(data, userName);
    }

    private ByteBuffer addString(ByteBuffer data, String string) {

        byte[] stringAsBytes = string.getBytes();
        data.putInt(stringAsBytes.length);
        data.put(stringAsBytes);

        return data;
    }


}
