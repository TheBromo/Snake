package ch.network;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class PacketReader {
    //used to get info out of a packet
    public ByteBuffer readPacket(InetAddress receiver, ByteBuffer data,ByteBuffer answer) throws UnknownHostException {
        Packet packet = new Packet(receiver, data);
        readData(data);
        answer = createResponse;
    }

    private void readData(Packet packet){

    }
}
