package ch.Model.Utils;

public enum Direction {
    NORTH((byte) 0), EAST((byte) 1), SOUTH((byte) 2), WEST((byte) 3);

    private byte dir;

    Direction(byte dir) {
        this.dir = dir;
    }

    public byte getDir() {
        return dir;
    }


}
