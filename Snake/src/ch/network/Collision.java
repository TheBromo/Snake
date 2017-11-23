package ch.network;

import java.awt.*;

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

public class Collision {

    private int[] xArray;
    private int[] yArray;

    public void checkBorders(Coordinates head, Tail user, int snakeSize, Rectangle bounds) {
        if (head.getNewX() < 0 || head.getNewX() + snakeSize + 1 >= bounds.width || head.getNewY() < 0 || head.getNewY() + snakeSize + 1 >= bounds.height) {
            user.setAlive(false);
        }
    }

    public void checkSnakes(Tail snakeOne, Tail snakeTwo) {

        xArray = snakeOne.getXCor();
        yArray = snakeOne.getYCor();


        int[] secondXArray = snakeTwo.getXCor();
        int[] secondYArray = snakeTwo.getYCor();
        for (int x = 1; x < snakeTwo.getLength(); x++) {
            if (xArray[0] == secondXArray[x]) {
                if (yArray[0] == secondYArray[x]) {
                    snakeOne.setAlive(false);
                }
            }
        }
    }

}
