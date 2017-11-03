package ch.snake;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;

class Lobby {
    //TODO add seed Generator,
    public static long seed;
    public static HashMap<InetAddress, Tail> users = new HashMap<InetAddress, Tail>();

    public Lobby(String[] names, InetAddress[] ipAdresses) {
        seed=generateSeed(names);
        for (int i = 0; i < names.length; i++) {
            users.put(ipAdresses[i], new Tail(names[i]));
        }
    }

    private long generateSeed(String[] names) {
        Arrays.sort(names);
        String s="";
        for (int i=0; i<names.length; i++){
            s=s.concat(names[i]);
        }
        long hash = 0;
        for (char c : s.toCharArray()) {
            hash = 31L * hash + c;
        }
        return hash;
    }
}
