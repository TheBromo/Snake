package ch.snake;

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

public class Tail {

    private int[] xArray = new int[100];
    private int[] yArray = new int[100];
    private String name="";
    private int score = 0;
    private int length = 6;
    private boolean alive=true;
    private Color mColor;
    private long checkNumber;

    Tail(String name) {
        this.name = name;
    }


    /**
     * Simulates the snakestale going backwards
     * Also Shifts the X Coordinate back the Tail by one
     *
     * @param lastX is the Coordinate of the SnakeHead and is given back the Tail
     */
    private void refreshX(int lastX) {

        int temp1 = lastX, temp2;
        //shifts the coordinates back the minimum snake spine
        for (int x = 1; x < length; x++) {
            temp2 = xArray[x];
            xArray[x] = temp1;
            temp1 = temp2;
        }
    }

    void reset() {

        for (int x = 0; x < length; x++) {
            xArray[x] = 2000;
        }

        for (int y = 0; y < length; y++) {
            yArray[y] = 2000;
        }
        length = 6;
    }

    /**
     * Simulates the snakestale going backwards
     * Also Shifts the Y Coordinate back the Tail by one
     *
     * @param lastY is the Coordinate of the SnakeHead and is given back the Tail
     */
    private void refreshY(int lastY) {

        int temp1 = lastY, temp2;
        //shifts the coordinates back the minimum snake spine
        for (int x = 1; x < length; x++) {
            temp2 = yArray[x];
            yArray[x] = temp1;
            temp1 = temp2;
        }

    }

    /**
     * Sets the Tail and head Colour of the Snake
     *
     * @param color
     */
    void setColor(Color color) {
        mColor = color;
    }

    int getLength() {
        return length;
    }

    /**
     * Checks if the Snake Collides with the Dot, if it collides it becomes one bPart bigger
     */
    void dotCheck(Dot mDot) {
        //TODO does not work
        if (yArray[0] == mDot.getY() && xArray[0] == mDot.getX()) {
            //adds one tail part
            length++;
            //sets the new tailpart outside off map so it doesnt show up
            xArray[length - 1] = 900;
            yArray[length - 1] = 900;
            mDot.changeToNewPosition();
        }
    }

    int[] getXCor() {
        return xArray;
    }

    int[] getYCor() {
        return yArray;
    }


    Color getColor() {
        return mColor;
    }

    /**
     * It let's you set values by indexes
     * @param index the given indexes
     * @param x coordinate
     * @param y coordinate
     */
    void setupArray(int index, int x,int y){
        xArray[index]=x;
        yArray[index]=y;
    }

    /**
     * refreshes both coordinates
     *
     * @param newY will be the new X coordinate of the head
     * @param newX will be the new Y coordinate of the head
     */
    void refresh(int newY, int newX) {

        //the last newY cor gets saved and the head gets updated
        int lastY = yArray[0];
        yArray[0] = newY;
        refreshY(lastY);

        //the last newX cor gets saved and the head gets updated
        int lastX = xArray[0];
        xArray[0] = newX;
        refreshX(lastX);


    }

    boolean isAlive() {
        return alive;
    }

    void setAlive(boolean alive) {
        this.alive = alive;
    }

    public String getName() {
        return name;
    }

    int getScore() {
        return score;
    }

    public void draw(Graphics g,int snakeSize){
        for (int x = 0; x <= getLength() - 1; x++) {
            g.setColor(getColor());
            g.fillRect(xArray[x], yArray[x], snakeSize, snakeSize);
        }
    }

    public void setName(String name) {
        this.name = name;
    }

}
