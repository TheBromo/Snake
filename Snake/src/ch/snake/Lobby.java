package ch.snake;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

class Lobby {
    private static int snakeSize = 10;
    private static long seed;
    private static HashMap<InetAddress, Tail> users = new HashMap<>();
    private static HashMap<InetAddress, Coordinates> heads = new HashMap<>();

    public Lobby(String[] names, InetAddress[] ipAdresses, int size) {
        seed = generateSeed(names);
        for (int i = 0; i < names.length; i++) {
            users.put(ipAdresses[i], new Tail(names[i]));
        }
        for (int i = 0; i < names.length; i++) {
            heads.put(ipAdresses[i], new Coordinates());
        }
        positionSetter(size);

        int ySteps = 0;
        for (InetAddress key : users.keySet()) {
            for (int index = 1; index < users.get(key).getLength(); index++) {
                users.get(key).setupArray(index, size + 50, ySteps);
                ySteps += snakeSize;
            }
        }

    }

    private long generateSeed(String[] names) {

        Arrays.sort(names);
        String s = "";
        for (int i = 0; i < names.length; i++) {
            s = s.concat(names[i]);
        }
        long hash = 0;
        for (char c : s.toCharArray()) {
            hash = 31L * hash + c;
        }
        return hash;
    }

    public static long getSeed() {
        return seed;
    }

    public static HashMap<InetAddress, Tail> getUsers() {
        return users;
    }

    public static HashMap<InetAddress, Coordinates> getHeads() {
        return heads;
    }

    public static int getSnakeSize() {
        return snakeSize;
    }

    private void positionSetter(int size) {
        int playerCount = users.size();
        if (playerCount == 3) {
            playerCount = 4;
        } else if (playerCount > 3 && playerCount < 10) {
            playerCount = 9;
        } else if (playerCount > 9) {
            playerCount = 16;
        }
        if (playerCount == 2) {
            InetAddress[] keys;
            keys = users.keySet().toArray(new InetAddress[users.size()]);
            Lobby.getHeads().get(keys[0]).setPos(size / 4, size / 2);
            Lobby.getHeads().get(keys[1]).setPos((size / 4) * 3, size / 2);
        } else {
            Iterator<InetAddress> iterator = users.keySet().iterator();
            int steps = size / ((int) Math.sqrt(playerCount));
            for (int x = 0; x < size; x = x + steps) {
                for (int y = 0; y < size; y = y + steps) {
                    if (iterator.hasNext()) {
                        Lobby.getHeads().get(iterator.next()).setPos(x + (steps / 2), x + (steps / 2));
                    }
                }
            }
        }


    }
}
