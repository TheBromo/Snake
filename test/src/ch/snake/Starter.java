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
    public Starter(int size) {

        Scanner scanner = new Scanner(System.in);
        String i = scanner.nextLine();
        String[] names = {"Bromo", "Spasst"};
        try {
            InetAddress[] addresses = {InetAddress.getLocalHost(), InetAddress.getByName("192.168.56.1")};
            new Lobby(names, addresses, size);
            new Gui(size, size);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Starter(800);
    }
}
