package ch.network;

import java.net.InetAddress;
import java.util.Random;

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

class Dot {

    private static int mX = 400, mY = 400, mSizeX = Draw.snakeSize, mSizeY = Draw.snakeSize;
    Random r = new Random();

    public Dot() {
        r.setSeed(Lobby.getSeed());

    }

    /**
     * Generates a new Position for the Dot that is not on the Snake head
     */
    void changeToNewPosition() {
        //Generates New position
        while (onTopOfSnake() || mX < 0 || mX > 760 || mY < 0 || mY > 760) {
            mX = (r.nextInt(41) * 20);
            mY = (r.nextInt(41) * 20);
        }
    }

    boolean onTopOfSnake() {
        for (InetAddress key : Lobby.getUsers().keySet()) {
            int[] xArray = Lobby.getUsers().get(key).getXCor();
            int[] yArray = Lobby.getUsers().get(key).getYCor();
            for (int x = 1; x < Lobby.getUsers().get(key).getLength(); x++) {
                if (mX == xArray[x]) {
                    if (mY == yArray[x]) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    int getSizeX() {
        return mSizeX;
    }

    int getSizeY() {
        return mSizeY;
    }

    int getY() {
        return mY;
    }

    int getX() {
        return mX;
    }
}
