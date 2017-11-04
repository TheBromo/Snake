package ch.snake;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Manuel Strenge
 * @version v0.4
 */
public class Starter {
    public Starter(int size) {
        String[] names = {"Bromo", "Spasst"};
        try {
            InetAddress[] addresses = { InetAddress.getLocalHost(), InetAddress.getByName("localhost")};
            new Lobby(names, addresses,size);
            new Gui(size , size);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new Starter(800);
    }
}
