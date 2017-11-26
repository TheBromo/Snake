package ch.network;



import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class NewNetwork {

    private ArrayList<Packet> sentPackets = new ArrayList<>();
    private PacketBuilder mPacketBuilder;
    private PacketReader mPacketReader;

    private ByteBuffer readBuffer, writeBuffer;
    private DatagramChannel socket;
    private Selector selector;

    private long startTime = 0;
    private int checkNumber;
    private long last = 0;
    boolean startTimeReceived = false;

    public NewNetwork() throws IOException {

        //Initialize parser and Builder
        mPacketBuilder = new PacketBuilder(this);
        mPacketReader = new PacketReader(this);

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
        writeBuffer = mPacketBuilder.createPacket(packet);
        finishWritingIntoBuffer();

        //creates a socket address
        InetSocketAddress socketAddress = new InetSocketAddress(packet.getReceiver(), 23723);

        //sends the data
        socket.send(writeBuffer, socketAddress);

    }

    private void answerPacket(Packet packet) throws IOException {
        prepareWritingIntoBuffer();
        writeBuffer = mPacketBuilder.createPacket(packet, writeBuffer);
        finishWritingIntoBuffer();

        //creates a socket address
        InetSocketAddress socketAddress = new InetSocketAddress(packet.getReceiver(), 23723);

        //sends the data
        socket.send(writeBuffer, socketAddress);

    }

    public void readPacket(ByteBuffer data) throws IOException {
        //prepares buffer for reading
        readBuffer.position(0).limit(readBuffer.capacity());
        //gets the address of the sender
        SocketAddress sender = socket.receive(readBuffer);
        readBuffer.flip();

        //the senders InetAddress
        InetAddress address = ((InetSocketAddress) sender).getAddress();

        Packet packet = mPacketReader.readPacket(address, data);
        answerPacket(packet);
    }
    public void waitForConnection(HashMap<InetAddress, Tail> users, HashMap<InetAddress, Coordinates> heads) throws IOException {

        Iterator<InetAddress> addressIterator = users.keySet().iterator();

        if (addressIterator.hasNext()) {

            InetAddress address = addressIterator.next();
            if (InetAddress.getByName(InetAddress.getLocalHost().getHostAddress()).equals(address)) {
                sendPacket(users, heads, PacketType.CONNECTION);
            }

            while (true) {

                if (InetAddress.getByName(InetAddress.getLocalHost().getHostAddress()).equals(address)) {
                    long now = System.currentTimeMillis();
                    if (now - last == 20) {
                        sendPacket( ,PacketType.CONNECTION);
                        last = now;
                    }
                    if (startTime <= System.currentTimeMillis()) {
                        break;
                    }

                } else {

                    receivePacket(users, heads);
                    if (startTimeReceived && startTime <= System.currentTimeMillis()) {
                        break;
                    }
                }
            }
        }

    }


    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setStartTimeReceived(boolean startTimeReceived) {
        this.startTimeReceived = startTimeReceived;
    }

    public boolean isStartTimeReceived() {
        return startTimeReceived;
    }

    public ArrayList<Packet> getSentPackets() {
        return sentPackets;
    }

    public void setSentPackets(ArrayList<Packet> sentPackets) {
        this.sentPackets = sentPackets;
    }
}
