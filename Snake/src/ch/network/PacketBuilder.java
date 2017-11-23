package ch.network;

import java.net.InetAddress;
import java.nio.ByteBuffer;

public class PacketBuilder {


    private int checkNumber;

    public PacketBuilder() {
        checkNumber = 0;
    }

    public ByteBuffer createPacket(InetAddress receiver, ByteBuffer buffer, PacketType packetType) {

        Packet packet = new Packet(receiver, buffer, checkNumber, packetType);
        packet.setData(fillPacket(packet));

        checkNumber++;
        return buffer;
    }

    public ByteBuffer fillPacket(Packet packet) {
        ByteBuffer buffer = packet.getData();


        return buffer;
    }


}
