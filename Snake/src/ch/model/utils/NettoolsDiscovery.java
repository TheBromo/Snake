/*
 *     Swing Snake
 *     A local snake mp game
 *     Copyright (C) 2017 Manuel Strenge
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.model.utils;

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
