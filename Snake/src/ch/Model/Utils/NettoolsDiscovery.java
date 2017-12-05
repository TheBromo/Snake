package ch.Model.Utils;

import ch.thecodinglab.nettools.Discovery;
import ch.thecodinglab.nettools.SocketAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class NettoolsDiscovery implements Discovery.Callback {

    private ArrayList<InetAddress> addresses = new ArrayList<>();

    /**
     * Gets all the addresses of other players searching over the same Port
     *
     * @return all found Addresses
     */
    public ArrayList<InetAddress> discover() {

        Discovery.initailize((short) 12345);
        Discovery.setCallback(this);
        Discovery.search((short) 12345, true);

        long started = System.currentTimeMillis();

        while (true) {
            Discovery.update();

            long now = System.currentTimeMillis();
            if (now - started >= 1000) {
                break;
            }
        }

        Discovery.close();

        return null;
    }

    @Override
    public boolean discoveryRequestReceived(SocketAddress socketAddress) {
        return false;
    }

    @Override
    public void discoveryClientFound(SocketAddress socketAddress) {
        try {
            InetAddress addr = InetAddress.getByAddress(socketAddress.getAddress());
            addresses.add(addr);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void discoveryPingResult(SocketAddress socketAddress, int i, boolean b) {

    }
}
