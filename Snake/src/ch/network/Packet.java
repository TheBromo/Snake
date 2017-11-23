package ch.network;

import java.net.InetAddress;
import java.nio.ByteBuffer;

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

enum PacketType {
    NAME(1),
    COORDINATES(2),
    DIRECTION(3),
    RESPONSE(4),
    RESEND(5),
    CONNECTION(6);


    private final int type;

    PacketType(int type) {
        this.type = type;
    }

    public int type() {
        return type;
    }
}

class PacketHeader {

    private ByteBuffer buffer;

    public ByteBuffer addHeader(Packet packet) {

        buffer = packet.getData();

        // packetType, timeSent, checkNumber,
        buffer.putInt(packet.getType().type());
        buffer.putLong(packet.getTimeCreated());
        buffer.putInt(packet.getCheckNumber());

        return buffer;
    }


}

public class Packet {

    private PacketHeader header;
    private InetAddress receiver;
    private PacketType type;
    private ByteBuffer data;
    private int checkNumber;
    private long timeCreated;


    public Packet(InetAddress receiver, ByteBuffer data, int checkNumber, PacketType type) {

        this.timeCreated = System.currentTimeMillis();
        this.type = type;
        this.receiver = receiver;
        this.data = data;
        this.checkNumber = checkNumber;

        header = new PacketHeader();
        data = header.addHeader(this);

    }

    public Packet(InetAddress receiver) {
        this.receiver = receiver;
    }


    public ByteBuffer getData() {
        return data;
    }

    public void setData(ByteBuffer data) {
        this.data = data;
    }

    public long getTimeCreated() {
        return timeCreated;
    }


    public PacketType getType() {
        return type;
    }

    public void setType(PacketType type) {
        this.type = type;
    }

    public InetAddress getReceiver() {
        return receiver;
    }

    public int getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(int checkNumber) {
        this.checkNumber = checkNumber;
    }


}


