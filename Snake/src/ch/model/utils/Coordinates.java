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

public class Coordinates {
    private int x, y;
    private Direction direction;

    public Coordinates() {
        x = 0;
        y = 0;
        direction = Direction.NORTH;
    }

    public Coordinates(Coordinates coordinates) {
        x = coordinates.getX();
        y = coordinates.getY();
        direction = coordinates.getDirection();

    }

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
        direction = Direction.NORTH;

    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
