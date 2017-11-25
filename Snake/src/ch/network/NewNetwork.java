package ch.network;


import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;

public class NewNetwork {

    private ArrayList<Packet> sentPackets = new ArrayList<>();
    private PacketBuilder mPacketBuilder;
    private PacketReader mPacketReader;

    private ByteBuffer readBuffer, writeBuffer;
    private DatagramChannel socket;
    private Selector selector;

    private static long startTime = 0;
    private int checkNumber;
    private long last = 0;
    boolean startTimeReceived = false;

    public NewNetwork() throws IOException {

        //Initialize parser and Builder
        mPacketBuilder = new PacketBuilder();
        mPacketReader = new PacketReader();

        //prepares the socket
        socket = DatagramChannel.open();
        socket.configureBlocking(false);
        socket.bind(new InetSocketAddress(23723));

        //creates a selector for reading incoming Traffic
        selector = Selector.open();
        socket.register(selector, SelectionKey.OP_READ);

        readBuffer = ByteBuffer.allocate(1024);
        writeBuffer = ByteBuffer.allocate(1024);
    }

    private void prepareWritingIntoBuffer() {
        writeBuffer.position(0).limit(writeBuffer.capacity());
    }

    private void finishWritingIntoBuffer() {
        writeBuffer.flip();
    }


    public void sendPacket(InetAddress receiver, PacketType packetType) throws IOException {
        prepareWritingIntoBuffer();
        writeBuffer = mPacketBuilder.createPacket(receiver, writeBuffer, packetType);
        finishWritingIntoBuffer();

        //creates a socket address
        InetSocketAddress socketAddress = new InetSocketAddress(receiver, 23723);

        //sends the data
        socket.send(writeBuffer, socketAddress);

        sentPackets.add(mPacketBuilder.getNewestPacket());
    }

    public void resendPacket(Packet packet) throws IOException {
        prepareWritingIntoBuffer();
        writeBuffer= mPacketBuilder.createPacket(packet);
        finishWritingIntoBuffer();

        //creates a socket address
        InetSocketAddress socketAddress = new InetSocketAddress(packet.getReceiver(), 23723);

        //sends the data
        socket.send(writeBuffer, socketAddress);

    }

    public void answerPacket(Packet packet) throws IOException {
        prepareWritingIntoBuffer();
        writeBuffer = mPacketBuilder.createPacket(packet, writeBuffer);
        finishWritingIntoBuffer();

        //creates a socket address
        InetSocketAddress socketAddress = new InetSocketAddress(packet.getReceiver(), 23723);

        //sends the data
        socket.send(writeBuffer, socketAddress);

    }


    public static long getStartTime() {
        return startTime;
    }

    public static void setStartTime(long startTime) {
        NewNetwork.startTime = startTime;
    }
}
