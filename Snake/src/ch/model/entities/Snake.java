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
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Snake {
    private ArrayList<Coordinates> coordinates = new ArrayList<>();
    private Coordinates startCoordinates;
    private String name;
    private int score;
    private Color color;
    private boolean alive;


    public Snake(Coordinates startCoordinates) {
        for (int i = 0; i < 6; i++) {
            Coordinates coordinate = new Coordinates(startCoordinates);
            coordinates.add(coordinate);
        }
        this.startCoordinates = startCoordinates;
        name = "default";
        score = 0;
        alive = true;
        color = Color.WHITE;

    }


    public void reset() {
        coordinates.clear();
        for (int i = 0; i < 6; i++) {
            Coordinates coordinate = new Coordinates(startCoordinates);
            coordinates.add(coordinate);
        }
    }
}
