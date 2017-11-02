package ch.snake;

import java.net.InetAddress;
import java.util.HashMap;

class Lobby {
    //TODO add seed Generator,
    static String[] players = {"Bromo", "Spasst", "Opfer", "Peter", "Adolf", "Goering", "Fredrick", "Elsa", "Opfa"};
    static int[] scores = {33, 444, 5, 3, 4, 4, 5, 4, 5, 6};
    public static HashMap<InetAddress,Tail> users= new HashMap<InetAddress, Tail>();

    public Lobby(String[] names,InetAddress[] ipAdresses){
        for (int i=0; i<names.length;i++){
            users.put(ipAdresses[i],new Tail(names[i]));
        }
    }
}
