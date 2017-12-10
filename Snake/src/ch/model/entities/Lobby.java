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

package ch.model.entities;


import ch.model.utils.Coordinates;
import ch.model.utils.NettoolsDiscovery;
import ch.model.utils.PositionSetter;
import ch.model.utils.SeedGenerator;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Lobby {

    private static InetAddress localhost;
    private static long seed;
    private ArrayList<InetAddress> players;
    private HashMap<InetAddress, Snake> snakeHashMap;
    private int screenSize;
    private PositionSetter positionSetter;


    public Lobby(int screenSize) {
        this.screenSize = screenSize;

        NettoolsDiscovery netttools = new NettoolsDiscovery();
        players = netttools.discover();

        try {
            localhost = InetAddress.getByName(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


        snakeHashMap = new HashMap<>();
        positionSetter = new PositionSetter(players.size(), screenSize);
        initializeSnakes();

        SeedGenerator seedGenerator = new SeedGenerator();
        Iterator<InetAddress> keys = snakeHashMap.keySet().iterator();
        seed = seedGenerator.generateSeed(keys.next().toString());
    }


    private void initializeSnakes() {

        for (InetAddress key : players) {

            Coordinates startCoordinates = positionSetter.getNewCoordinate();
            Snake snake = new Snake(startCoordinates);
            snakeHashMap.put(key, snake);
        }
    }


}
