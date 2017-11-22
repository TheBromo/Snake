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
import java.util.*;

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

public class Network {

    ArrayList<Packet> sentPackets = new ArrayList<>();
    ByteBuffer readBuffer, writeBuffer;
    private DatagramChannel socket;
    private Selector selector;
    private int checkNumber;
    private long startTime = 0;
    boolean startTimeReceived = false;


    public Network() throws IOException {
        checkNumber = 0;

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

    private void prepareWriteBuffer() {
        writeBuffer.position(0).limit(writeBuffer.capacity());
    }

    private void finishWritingIntoBuffer() {
        writeBuffer.flip();
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
                    sendPacket(users, heads, PacketType.RESEND);
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

    public void sendPacket(HashMap<InetAddress, Tail> users, HashMap<InetAddress, Coordinates> heads, PacketType packetType) throws IOException {
        if (packetType == PacketType.COORDINATES) {

            //removes all outdated Coordinates from being resent
            List<Packet> packets = new ArrayList<>();
            for (Packet p : sentPackets) {
                if (p.getType() == PacketType.COORDINATES) {
                    packets.add(p);
                }
            }
            sentPackets.removeAll(packets);
        }


        //Loops through all players
        for (InetAddress address : users.keySet()) {

            //skips the writing process to yourself
            if (!address.equals(InetAddress.getByName(InetAddress.getLocalHost().getHostAddress()))) {


                //Creates a new Packet that will be put into the byteBuffer to be sent
                Packet packet = new Packet(address, checkNumber);
                packet.setType(packetType);


                //Starts the process of writing to the packets
                prepareWriteBuffer();

                //Packet header
                writeBuffer.putInt(packetType.type());
                writeBuffer.putInt(packet.getCheckNumber());
                //packet content
                if (packetType == PacketType.NAME) {

                    //The name of a user will be sent
                    /*PacketType, checkNumber , length, string,*/
                    packet.addString(users.get(InetAddress.getByName(InetAddress.getLocalHost().getHostAddress())).getName());
                    writeBuffer.putInt(packet.getBytesArray().length);
                    writeBuffer.put(packet.getBytesArray());

                } else if (packetType == PacketType.COORDINATES) {

                    Coordinates you = heads.get(InetAddress.getByName(InetAddress.getLocalHost().getHostAddress()));
                    packet.addCoordinates(you.newX, you.newY, users.get(InetAddress.getByName(InetAddress.getLocalHost().getHostAddress())).isAlive());

                    //the new Coordinates of a user will be sent
                    /*PacketType,checkNumber,int x, int y, boolean,  */
                    int[] coordinates = packet.getInt();
                    for (int i = 0; i < coordinates.length; i++) {
                        writeBuffer.putInt(coordinates[i]);
                    }


                } else if (packetType == PacketType.DIRECTION) {
                    if (heads.get(InetAddress.getByName(InetAddress.getLocalHost().getHostAddress())).lastChar != heads.get(InetAddress.getByName(InetAddress.getLocalHost().getHostAddress())).nextDir) {

                        System.out.println("Last char= " + heads.get(InetAddress.getByName(InetAddress.getLocalHost().getHostAddress())).lastChar);
                        System.out.println("NextDir= " + heads.get(InetAddress.getByName(InetAddress.getLocalHost().getHostAddress())).nextDir);

                        //if the direction changed the direction is going to be sent
                        packet.setSingleChar(heads.get(InetAddress.getByName(InetAddress.getLocalHost().getHostAddress())).nextDir);
                        writeBuffer.putChar(packet.getSingleChar());

                    } else {

                        finishWritingIntoBuffer();
                        return;
                    }
                } else if (packet.getType() == PacketType.RESEND) {

                    resend();
                    return;

                } else if (packet.getType() == PacketType.CONNECTION) {

                    packet.addLong(System.currentTimeMillis() + 20000);
                    for (Long l : packet.getLongs()) {
                        startTime = l;
                        writeBuffer.putLong(l);
                    }
                }

                sentPackets.add(packet);
                //Stops the writing to the Buffer, the buffer is now ready to be sent
                finishWritingIntoBuffer();

                //creates a socket address
                InetSocketAddress socketAddress = new InetSocketAddress(address, 23723);
                //sends the data
                socket.send(writeBuffer, socketAddress);
                //increases the checkNumber used for packet control
                checkNumber++;
            }

        }
    }

    public void receivePacket(HashMap<InetAddress, Tail> users, HashMap<InetAddress, Coordinates> heads) throws IOException {
        if (selector.selectNow() > 0) {
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key = keys.next();

                //checks if any packets were received
                if (key.isReadable()) {

                    //prepares buffer for reading
                    readBuffer.position(0).limit(readBuffer.capacity());
                    //gets the address of the sender
                    SocketAddress sender = socket.receive(readBuffer);
                    readBuffer.flip();

                    //the senders InetAddress
                    InetAddress address = ((InetSocketAddress) sender).getAddress();

                    //creates a new Packet
                    Packet packet = new Packet(address);

                    //gets the packetType as number
                    int typeNumber = readBuffer.getInt();

                    //Turns the number into PacketType
                    for (PacketType pack : PacketType.values()) {
                        if (pack.type() == typeNumber) {
                            packet.setType(pack);
                            break;
                        }
                    }

                    //gets the check number of the packet
                    packet.setCheckNumber(readBuffer.getInt());

                    //Does the according action to each PacketType
                    if (packet.getType() == PacketType.NAME) {

                        //gets the strings length
                        int length = readBuffer.getInt();
                        //gets the string as bytes
                        byte[] data = new byte[length];
                        readBuffer.get(data);
                        //converts the bytes to string
                        String str = new String(data, StandardCharsets.UTF_8);

                        //gives the sender´s name to himself
                        users.get(address).setName(str);

                    } else if (packet.getType() == PacketType.COORDINATES) {

                        heads.get(address).setNewX(readBuffer.getInt());
                        heads.get(address).setNewY(readBuffer.getInt());

                        if (readBuffer.getInt() == 1) {
                            users.get(address).setAlive(true);
                        } else {
                            users.get(address).setAlive(false);
                        }

                    } else if (packet.getType() == PacketType.RESPONSE) {
                        sentPackets.removeIf(p -> p.getCheckNumber() == packet.getCheckNumber() && p.getReceiver().equals(packet.getReceiver()));

                    } else if (packet.getType() == PacketType.DIRECTION) {

                        char nextDir = readBuffer.getChar();
                        heads.get(((InetSocketAddress) sender).getAddress()).nextDir = nextDir;

                    } else if (packet.getType() == PacketType.CONNECTION) {

                        startTime = readBuffer.getLong();
                        startTimeReceived = true;
                    }
                    //Response packets must not be confirmed to be sent
                    if (packet.getType() != PacketType.RESPONSE) {

                        //response packet
                        prepareWriteBuffer();
                        writeBuffer.putInt(PacketType.RESPONSE.type());
                        writeBuffer.putInt(packet.getCheckNumber());
                        finishWritingIntoBuffer();

                        //creates a socket address
                        InetSocketAddress socketAddress = new InetSocketAddress(packet.getReceiver(), 23723);

                        //sends the data
                        socket.send(writeBuffer, socketAddress);
                    }

                }
                keys.remove();
            }
        }
    }

    public void resend() throws IOException {
        for (Packet packet : sentPackets) {

            prepareWriteBuffer();
            writeBuffer.putInt(packet.getType().type());
            writeBuffer.putInt(packet.getCheckNumber());

            if (packet.getType() == PacketType.COORDINATES) {

                int[] coordinates = packet.getInt();
                for (int i = 0; i < coordinates.length; i++) {
                    writeBuffer.putInt(coordinates[i]);
                }

            } else if (packet.getType() == PacketType.NAME) {
                writeBuffer.putInt(packet.getBytesArray().length);
                writeBuffer.put(packet.getBytesArray());

            } else if (packet.getType() == PacketType.DIRECTION) {
                writeBuffer.putChar(packet.getSingleChar());
            }
            finishWritingIntoBuffer();

            //creates a socket address
            InetSocketAddress socketAddress = new InetSocketAddress(packet.getReceiver(), 23723);

            //sends the data
            socket.send(writeBuffer, socketAddress);
        }

    }


}