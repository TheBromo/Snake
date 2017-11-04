package ch.snake;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

class Lobby {
    //TODO seed Generator needs to be used
    public static long seed;
    public static HashMap<InetAddress, Tail> users = new HashMap<InetAddress, Tail>();

    public Lobby(String[] names, InetAddress[] ipAdresses,int size) {
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

    private void positionSetter(int size){
        int playerCount = users.size();
        if (playerCount==2){
            InetAddress[] keys;
            keys = users.keySet().toArray(new InetAddress[users.size()]);
            users.get(keys[0]).setPos(size/4,size/2,keys[0]);
            users.get(keys[1]).setPos((size/4)*3,size/2,keys[1]);
        }


    }
}
