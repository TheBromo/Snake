package ch.model.network.packet;

import java.nio.ByteBuffer;

public enum Packet {
    SNAKE((byte) 0), CONNECTION((byte) 1), NAME((byte) 2), RESPONSE((byte) 3);

    byte type;

    Packet(byte type) {
        this.type = type;
    }
}
