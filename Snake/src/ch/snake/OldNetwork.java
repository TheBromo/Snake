package ch.snake;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * SwingSnake
 * Copyright (C) 2017  Manuel Strenge
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

public class OldNetwork {
    private DatagramChannel socket;
    private Selector selector;
    ByteBuffer readBuffer, writeBuffer;
    private int checkNumber,nameChecknumber =5;

    public OldNetwork() throws IOException {
        checkNumber = 0;
        socket = DatagramChannel.open();
        socket.configureBlocking(false);
        socket.bind(new InetSocketAddress(23723));

        selector = Selector.open();
        socket.register(selector, SelectionKey.OP_READ);

        readBuffer = ByteBuffer.allocate(1024);
        writeBuffer = ByteBuffer.allocate(1024);
    }

    public void getNames(String name) throws IOException {
        sendString(name);
        receiveNames();
        packageCheckString(name);
    }

    public void sendString(String name) throws IOException {
        //Sends the new Coordinates
        writeBuffer.position(0).limit(writeBuffer.capacity());


        byte[] data = name.getBytes(StandardCharsets.UTF_8);
        writeBuffer.putInt(data.length);
        writeBuffer.put(data);

        // X , Y, AliveBool, CheckNumber
        writeBuffer.putInt(checkNumber);
        writeBuffer.flip();
        for (InetAddress address : Lobby.getUsers().keySet()) {
            if (!address.equals(InetAddress.getLocalHost())) {
                InetSocketAddress socketAddress = new InetSocketAddress(address, 23723);
                socket.send(writeBuffer, socketAddress);
            }
        }
    }

    public void receiveNames() throws IOException {
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

                    int length = readBuffer.getInt();
                    byte[] data = new byte[length];
                    readBuffer.get(data);

                    String str = new String(data, StandardCharsets.UTF_8);
                    Lobby.getUsers().get(socketAddress.getAddress()).setName(str);

                    writeBuffer.position(0).limit(writeBuffer.capacity());
                    writeBuffer.putInt(readBuffer.getInt());
                    writeBuffer.flip();
                    socket.send(writeBuffer, sender);
                }
                keys.remove();
            }
        }
    }

    public void packageCheckString(String name) throws IOException {
        writeBuffer.position(0).limit(writeBuffer.capacity());


        byte[] data = name.getBytes(StandardCharsets.UTF_8);
        writeBuffer.putInt(data.length);
        writeBuffer.put(data);
        writeBuffer.putInt(checkNumber);
        writeBuffer.flip();
        ArrayList<InetAddress> addresses = new ArrayList<>();
        for (InetAddress i : Lobby.getUsers().keySet()) {
            if (!i.equals(InetAddress.getLocalHost())) {
                addresses.add(i);
            }
        }
        while (addresses.size()>0){
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
                        if (readBuffer.getInt() == nameChecknumber) {
                            System.out.println();
                            addresses.remove(socketAddress.getAddress());
                        }
                    }
                    keys.remove();
                }
            }
            for (InetAddress address :addresses) {
                if (!address.equals(InetAddress.getLocalHost())) {
                    InetSocketAddress socketAddress = new InetSocketAddress(address, 23723);
                    socket.send(writeBuffer, socketAddress);
                }
            }


        }
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
        writeBuffer.put((byte)bool);
        writeBuffer.putInt(checkNumber);
        writeBuffer.flip();
        for (InetAddress address : Lobby.getUsers().keySet()) {
            if (!address.equals(InetAddress.getLocalHost())) {
                InetSocketAddress socketAddress = new InetSocketAddress(address, 23723);
                socket.send(writeBuffer, socketAddress);
            }
        }
    }

    public void receiveCoordinates() throws IOException {
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

                    Lobby.getUsers().get(socketAddress.getAddress()).setAlive(readBuffer.get() == 1);

                    writeBuffer.position(0).limit(writeBuffer.capacity());
                    writeBuffer.putInt(readBuffer.getInt());
                    writeBuffer.flip();
                    socket.send(writeBuffer, sender);

                    System.out.println("Received Coordinates: " + readBuffer.getInt(0) + " " + readBuffer.getInt(4));
                    System.out.println("Alive?: " + Lobby.getUsers().get(socketAddress.getAddress()).isAlive() + "\n");

                }
                keys.remove();
            }
        }
    }


    public void checkCoordinatesPacketSuccess(int x, int y, boolean alive) throws IOException {
        writeBuffer.position(0).limit(writeBuffer.capacity());
        // X , Y, AliveBool, CheckNumber
        writeBuffer.putInt(x);
        writeBuffer.putInt(y);
        int bool = 0;
        if (alive) {
            bool = 1;
        }
        writeBuffer.put((byte)bool);
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
                        SocketAddress sender = socket.receive(readBuffer);
                        readBuffer.flip();
                        InetSocketAddress socketAddress = (InetSocketAddress) sender;
                        if (readBuffer.getInt() == checkNumber) {
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
        checkNumber++;
    }


    public void waitForConnection() throws IOException {

    }
}
