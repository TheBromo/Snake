package ch.snake;

import java.awt.*;
import java.net.InetAddress;

public class HUD {
    public void showStats(Graphics g,Rectangle bounds){
        int shift = 15, counter = 0;
        //Displays all the names of the users in their according color
        for (InetAddress key : Lobby.getUsers().keySet()) {
            g.setColor(Lobby.getUsers().get(key).getColor());
            g.fillRect(counter * (bounds.width / Lobby.getUsers().size()) + shift, 10, 20, 20);
            g.drawString(Lobby.getUsers().get(key).getName(), counter * (bounds.width / Lobby.getUsers().size()) + 25 + shift, 25);
            g.setColor(Color.black);
            int newX = counter * (bounds.width / Lobby.getUsers().size()) + shift;
            int length = String.valueOf(Lobby.getUsers().get(key).getScore()).length();
            if (length == 1) {
                newX += 7;
            } else if (length == 2) {
                newX += 3;
            }
            g.drawString("" + Lobby.getUsers().get(key).getScore(), newX, 25);
            counter++;
        }
    }
}
