package ch.snake;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Manuel Strenge
 * @version v0.4
 */
public class Starter {
    public Starter() {
        String[] names = {"Bromo", "Spasst"};
        try {
            InetAddress[] addresses = { InetAddress.getLocalHost(), InetAddress.getByName("localhost")};
            new Lobby(names, addresses);
            new Gui(800, 800);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new Starter();
    }
}
