package ch.Model.Entities;


import ch.Model.Utils.NettoolsDiscovery;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

public class Lobby {

    private ArrayList<InetAddress> players;
    private HashMap<InetAddress, Snake> snakeHashMap;


    public Lobby() {


        NettoolsDiscovery netttools = new NettoolsDiscovery();
        players = netttools.discover();


        snakeHashMap = new HashMap<>();
        initializeSnakes();

    }


    private void initializeSnakes() {
        //TODO
    }





}
