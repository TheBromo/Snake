package ch.network;


import ch.snake.Tail;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
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

public class Network {
    ArrayList<Packet> sentPackets = new ArrayList<>();
    ArrayList<Packet> receivedPackets = new ArrayList<>();
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

    private void prepareWriteBuffer() {
        writeBuffer.position(0).limit(writeBuffer.capacity());
    }

    private void finishWritingIntoBuffer() {
        writeBuffer.flip();
    }

    public void sendPacket(HashMap<InetAddress, Tail> users, PacketType packetType) throws IOException {
        for (InetAddress address : users.keySet()) {
            if (address.equals(InetAddress.getLocalHost())) continue;

            Packet packet = new Packet(address);
            sentPackets.add(packet);
            prepareWriteBuffer();

            writeBuffer.putInt(packet.getType().type());
            if (packetType == PacketType.NAME) {
                /*PacketType, length, string, checkNumber */
                packet.addString(users.get(InetAddress.getLocalHost()).getName());
                writeBuffer.put(packet.getBytesArray());

            } else if (packetType == PacketType.COORDINATES) {
                /*PacketType,int, int, boolean, checkNumber */
                for (int i : packet.getInt()) {
                    writeBuffer.putInt(i);
                }
                //TODO
            }

            writeBuffer.putInt(packet.getCheckNumber());
            finishWritingIntoBuffer();

            InetSocketAddress socketAddress = new InetSocketAddress(address, 23723);
            socket.send(writeBuffer, socketAddress);
        }
        checkNumber++;
    }

    public void receivePacket(HashMap<InetAddress, Tail> users) throws IOException {
        if (selector.selectNow() > 0) {
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key = keys.next();

                if (key.isReadable()) {
                    readBuffer.position(0).limit(readBuffer.capacity());
                    SocketAddress sender = socket.receive(readBuffer);
                    readBuffer.flip();
                    InetAddress address = ((InetSocketAddress) sender).getAddress();

                    Packet packet = new Packet(address);
                    int typeNumber = readBuffer.getInt();
                    for (PacketType pack : PacketType.values()) {
                        if (pack.type() == typeNumber) packet.setType(pack);
                    }

                    //TODO maybe move checkNumber up here

                    if (packet.getType() == PacketType.NAME) {
                        int length = readBuffer.getInt();
                        byte[] data = new byte[length];

                        readBuffer.get(data);
                        String str = new String(data, StandardCharsets.UTF_8);
                        users.get(address).setName(str);
                    } else if (packet.getType() == PacketType.COORDINATES) {

                    } else if (packet.getType() == PacketType.RESPONSE) {
                        int checkNumber = readBuffer.getInt();
                        for (Packet p : sentPackets) {
                            if (p.getCheckNumber() == checkNumber && p.getReceiver().equals(packet.getReceiver())) {
                                sentPackets.remove(p);
                                return;
                            }
                        }
                    }

                    packet.setCheckNumber(readBuffer.getInt());
                    receivedPackets.add(packet);
                    //TODO send back the checkNumber
                }
                keys.remove();
            }
        }
    }

    public void resend() {
        //TODO

    }

    public void answer() {
        //TODO
        /* id, checksum*/
    }


}

