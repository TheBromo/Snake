package ch.snake;

import ch.thecodinglab.nettools.Discovery;
import ch.thecodinglab.nettools.SocketAddress;
import ch.thecodinglab.nettools.WinNative;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Manuel Strenge https://github.com/TheBromo
 * @version v0.4
 */
public class Starter implements Discovery.Callback {

    ArrayList<InetAddress> inetAddresses=new ArrayList<>();

    public Starter(int size) throws UnknownHostException {
        WinNative.loadLibrary(new File("lib/native"));

        Discovery.initailize((short)12345);
        Discovery.setCallback(this);
        Discovery.search((short)12345, true);
        long started = System.currentTimeMillis();
        while (true) {
            Discovery.update();

            long now = System.currentTimeMillis();
            if (now - started >= 10000) {
                break;
            }
        }
        Discovery.close();
        System.out.println("DONE");

        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        System.out.println("Local Address= "+InetAddress.getLocalHost());
        inetAddresses.add(InetAddress.getLocalHost());
        String[] names = {"Bromo", "Spasst"};
        try {
            InetAddress[] addresses =inetAddresses.toArray(new InetAddress[inetAddresses.size()]) ;
            new Lobby(names, addresses, size);
            new Gui(size, size);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws UnknownHostException {
        new Starter(800);
    }

    @Override
    public boolean discoveryRequestReceived(SocketAddress socketAddress) {
        return true;
    }

    @Override
    public void discoveryClientFound(SocketAddress socketAddress) {
        try {
            InetAddress addr = InetAddress.getByAddress(socketAddress.getAddress());
            inetAddresses.add(addr);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.println(socketAddress.toString());
    }

    @Override
    public void discoveryPingResult(SocketAddress socketAddress, int i, boolean b) {

    }
}
