package ch.network;

import java.util.ArrayList;

public class Packet {
    private int id, checkNumber;
    private ArrayList<Integer> integers = new ArrayList<>();
    private ArrayList<Byte> bytes = new ArrayList<>();

    public void addBoolean() {

    }

    public void addString(String string) {
        bytes.add((Byte) string.getBytes());
    }

    public void addInt(int integer) {
        integers.add(integer);

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public ArrayList<Byte> getBytes() {
        return bytes;
    }


}
