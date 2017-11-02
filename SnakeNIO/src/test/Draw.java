package test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Random;

public class Draw {
    int playerCount=4;
    Tail[] t = new Tail[playerCount];

    public Draw(){
        Random r = new Random();
        //names each tale and sets it alive
        for (int i=0; i<playerCount;i++) {
            t[i].setName(r.nextDouble()+"");
            t[i].setAlive(true);
        }

        try {
            DatagramChannel socket = DatagramChannel.open();
            socket.configureBlocking(false);
            socket.bind(new InetSocketAddress(23723));

            Selector selector = Selector.open();
            socket.register(selector, SelectionKey.OP_READ);

            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            ByteBuffer writeBuffer = ByteBuffer.allocate(1024);

            //should simulate repaint
            while (true){
                if (selector.selectNow() > 0) {
                    Iterator<   SelectionKey> keys = selector.selectedKeys().iterator();
                    while (keys.hasNext()) {
                        SelectionKey key = keys.next();

                        if (key.isReadable()) {
                            readBuffer.position(0).limit(readBuffer.capacity());
                            SocketAddress sender = socket.receive(readBuffer);
                            readBuffer.flip();

                        }

                        keys.remove();
                    }
                }

                writeBuffer.position(0).limit(writeBuffer.capacity());
                // TODO Fill buffer
                writeBuffer.flip();
                socket.send(writeBuffer, /* Address of receiver */null);

                for (int i=0; i<playerCount;i++) {
                    t[i].refresh();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
