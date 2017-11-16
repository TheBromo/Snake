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
import java.util.Iterator;

public class Network {
    ByteBuffer readBuffer, writeBuffer;
    private DatagramChannel socket;
    private Selector selector;
    private int checkNumber;

    public Network() throws IOException {
        checkNumber = 0;
        socket = DatagramChannel.open();
        socket.configureBlocking(false);
        socket.bind(new InetSocketAddress(23723));

        selector = Selector.open();
        socket.register(selector, SelectionKey.OP_READ);

        readBuffer = ByteBuffer.allocate(1024);
        writeBuffer = ByteBuffer.allocate(1024);
    }

    public void createNamePackage(String name) {

    }


    public void sendCoordinates(int x, int y, boolean alive) throws IOException {
        //Sends the new Coordinates
        writeBuffer.position(0).limit(writeBuffer.capacity());

        writeBuffer.putInt(x);
        writeBuffer.putInt(y);
        // X , Y, AliveBool, CheckNumber
        int bool = 0;
        if (alive) {
            bool = 1;
        }
        writeBuffer.putInt(bool);
        writeBuffer.putInt(checkNumber);
        writeBuffer.flip();
        for (InetAddress address : Lobby.getUsers().keySet()) {
            if (!address.equals(InetAddress.getLocalHost())) {
                InetSocketAddress socketAddress = new InetSocketAddress(address, 23723);
                socket.send(writeBuffer, socketAddress);
            }
        }
    }

    public void receiveData() throws IOException {
        if (selector.selectNow() > 0) {
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key = keys.next();

                if (key.isReadable()) {
                    //reads the new coordinates
                    readBuffer.position(0).limit(readBuffer.capacity());
                    SocketAddress sender = socket.receive(readBuffer);
                    readBuffer.flip();
                    System.out.println(sender);
                    InetSocketAddress socketAddress = (InetSocketAddress) sender;
                    Lobby.getHeads().get(socketAddress.getAddress()).setPos(readBuffer.getInt(), readBuffer.getInt());

                    if (readBuffer.getInt() == 1) {
                        Lobby.getUsers().get(InetAddress.getLocalHost()).setAlive(true);

                    } else {
                        Lobby.getUsers().get(InetAddress.getLocalHost()).setAlive(false);
                    }
                    writeBuffer.position(0).limit(writeBuffer.capacity());
                    writeBuffer.putLong(readBuffer.getLong());
                    writeBuffer.flip();
                    socket.send(writeBuffer, sender);


                }
                keys.remove();
            }
        }
    }


    public void checkPacketSuccess(int x, int y, boolean alive) throws IOException {
        writeBuffer.position(0).limit(writeBuffer.capacity());

        writeBuffer.putInt(x);
        writeBuffer.putInt(y);
        // X , Y, AliveBool, CheckNumber
        writeBuffer.putInt(checkNumber);
        writeBuffer.flip();
        ArrayList<InetAddress> addresses = new ArrayList<>();
        for (InetAddress i : Lobby.getUsers().keySet()) {
            if (!i.equals(InetAddress.getLocalHost())) {
                addresses.add(i);
            }
        }
        while (addresses.size() > 0) {
            if (selector.selectNow() > 0) {
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();

                    if (key.isReadable()) {
                        //reads the new coordinates
                        readBuffer.position(0).limit(readBuffer.capacity());
                        System.out.println();
                        SocketAddress sender = socket.receive(readBuffer);
                        readBuffer.flip();
                        InetSocketAddress socketAddress = (InetSocketAddress) sender;
                        System.out.println(readBuffer.getInt(0) + "");
                        if (readBuffer.getInt() == checkNumber) {
                            addresses.remove(socketAddress.getAddress());
                        }
                    }
                    keys.remove();
                }
            }
            writeBuffer.position(0).limit(writeBuffer.capacity());
            writeBuffer.flip();
            for (InetAddress i : addresses) {
                InetSocketAddress socketAddress = new InetSocketAddress(i, 23723);
                socket.send(writeBuffer, socketAddress);
            }
        }
        checkNumber++;
    }


    public void waitForConnection() throws IOException {
        writeBuffer.position(0).limit(writeBuffer.capacity());
        writeBuffer.putInt(555);
        writeBuffer.flip();
        ArrayList<InetAddress> addresses = new ArrayList<>();
        for (InetAddress i : Lobby.getUsers().keySet()) {
            if (!i.equals(InetAddress.getLocalHost())) {
                addresses.add(i);
            }
        }
        while (addresses.size() > 0) {
            if (selector.selectNow() > 0) {
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();

                    if (key.isReadable()) {
                        //reads the new coordinates
                        readBuffer.position(0).limit(readBuffer.capacity());
                        SocketAddress sender = socket.receive(readBuffer);
                        readBuffer.flip();
                        InetSocketAddress socketAddress = (InetSocketAddress) sender;
                        if (readBuffer.getInt() == 555) {
                            addresses.remove(socketAddress.getAddress());
                        }
                    }

                    keys.remove();
                }
            }

            for (InetAddress i : addresses) {
                InetSocketAddress socketAddress = new InetSocketAddress(i, 23723);
                socket.send(writeBuffer, socketAddress);
            }
        }
        System.out.println("Success");
    }
}