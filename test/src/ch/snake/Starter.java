package ch.snake;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Manuel Strenge https://github.com/TheBromo
 * @version v0.4
 */
public class Starter {
    public Starter(int size) throws UnknownHostException {
        System.out.println("Local Address= "+InetAddress.getLocalHost());

        String[] names = {"Bromo", "Spasst"};
        try {
            InetAddress[] addresses = {InetAddress.getLocalHost(), InetAddress.getByName("192.168.1.5")};

            new Lobby(names, addresses, size);
            new Gui(size, size);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws UnknownHostException {
        new Starter(800);
    }
}
