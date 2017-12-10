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


import java.util.ArrayList;
import java.util.Iterator;

public class PositionSetter {
    private int playerSize, screenSize;
    private Iterator<Coordinates> iterator;

    public PositionSetter(int playerSize, int screenSize) {
        this.screenSize = screenSize;
        this.playerSize = playerSize;
        if (playerSize >= 3) {
            this.playerSize = 4;
        } else if (playerSize > 4 && playerSize < 10) {
            this.playerSize = 9;
        } else if (playerSize > 9) {
            this.playerSize = 16;
        }
        generate();
    }


    private void generate() {
        ArrayList<Coordinates> coordinates1 = new ArrayList<>();

        int steps = screenSize / ((int) Math.sqrt(playerSize));
        for (int x = 0; x < screenSize; x = x + steps) {
            for (int y = 0; y < screenSize; y = y + steps) {
                coordinates1.add(new Coordinates(x + (steps / 2), y + (steps / 2)));
            }
        }
        iterator = coordinates1.iterator();

    }

    public Coordinates getNewCoordinate() {
        if (iterator.hasNext()) {
            return iterator.next();
        } else {
            System.err.println("Not enough start Positions generated ");
            return null;
        }

    }
}
