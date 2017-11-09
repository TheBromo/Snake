package ch.snake;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * @author Manuel Strenge https://github.com/TheBromo
 * @version v0.4
 */
public class Starter {
    public Starter(int size) throws UnknownHostException {
        System.out.println("Local Address= "+InetAddress.getLocalHost());

        String[] names = {"Bromo", "Spasst"};
        try {
            InetAddress[] addresses = {InetAddress.getLocalHost(), InetAddress.getByName("192.168.1.3")};

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
