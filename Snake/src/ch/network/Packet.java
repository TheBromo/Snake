package ch.network;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

enum PacketType {
    NAME(1),
    COORDINATES(2);

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
    private int id, checkNumber;
    private ArrayList<Integer> integers = new ArrayList<>();
    private ArrayList<Byte> bytes = new ArrayList<>();

    public Packet(InetAddress receiver) {
        this.receiver = receiver;
        Random random = new Random();
        checkNumber = random.nextInt();
    }

    public void addBoolean() {

    }

    public void addString(String string) {
        /* id,checkNumber, length, string */
        id = PacketType.NAME.type();
        byte[] data = string.getBytes(StandardCharsets.UTF_8);
        bytes.add((byte) data.length);
        for (int index = 0; index < data.length; index++) {
            bytes.add(data[index]);
        }
    }

    public void addInt(int integer) {
        integers.add(integer);

    }

    public int getId() {
        return id;
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

    public int[] getInt() {
        if (integers.size() == 0) return null;

        int[] array = new int[integers.size()];
        Iterator<Integer> iterator = integers.iterator();
        for (int i = 0; i < array.length; i++) {
            array[i] = iterator.next().intValue();
        }
        return array;
    }

    public byte[] getBytesArray() {
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


