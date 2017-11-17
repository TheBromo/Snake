package ch.snake;

import java.awt.*;
import java.net.InetAddress;

/**
 * SwingSnake
 * Copyright (C) 2017  Manuel Strenge
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

public class HUD {
    public void showStats(Graphics g, Rectangle bounds){
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
