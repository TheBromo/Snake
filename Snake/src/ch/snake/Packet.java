package ch.snake;

import java.net.InetAddress;
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

enum PacketType {
    NAME(1),
    COORDINATES(2),
    RESPONSE(3);

    private final int type;

    PacketType(int type) {
        this.type = type;
    }

    public int type() {
        return type;
    }
}

public class Packet {

    private InetAddress receiver;
    private PacketType type;
    private int checkNumber;
    private String name;
    private ArrayList<Integer> integers = new ArrayList<>();
    private ArrayList<Byte> bytes = new ArrayList<>();

    public Packet(InetAddress receiver, int checkNumber) {
        this.receiver = receiver;
        this.checkNumber = checkNumber;
    }

    public Packet(InetAddress receiver) {
        this.receiver = receiver;
    }

    public void addBoolean(boolean bool) {
        int b;
        if (bool) {
            b = 1;
            integers.add(b);
        } else {
            b = 0;
            integers.add(b);
        }
    }

    public void addString(String string) {
        /*  length, string */
        type = PacketType.NAME;
        byte[] data = string.getBytes(StandardCharsets.UTF_8);
        bytes.add((byte) data.length);
        for (int index = 0; index < data.length; index++) {
            bytes.add(data[index]);
        }
    }

    public void addInt(int integer) {
        integers.add(integer);
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

    public ArrayList<Integer> getIntegers() {
        return integers;
    }

    public int intSize() {
        return integers.size();
    }

    public String getName() {
        return null;
    }

    public int[] getInt() {
        if (integers.size() == 0) return null;

        int[] array = new int[integers.size()];
        Iterator<Integer> iterator = integers.iterator();
        for (int i = 0; i < array.length; i++) {
            array[i] = iterator.next().intValue();
        }
        return array;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIntegers(ArrayList<Integer> integers) {
        this.integers = integers;
    }

    public void setBytes(ArrayList<Byte> bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytesArray() {

        if (bytes.size() == 0) return null;

        byte[] array = new byte[bytes.size()];
        Iterator<Byte> iterator = bytes.iterator();
        for (int i = 0; i < array.length; i++) {
            array[i] = iterator.next().byteValue();
        }
        return array;
    }


    public ArrayList<Byte> getBytes() {
        return bytes;
    }


}


