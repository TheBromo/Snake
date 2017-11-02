package ch.snake;

import java.util.Random;

class Dot {

    private static int mX = 400, mY = 400, mSizeX = Draw.snakeSize, mSizeY = Draw.snakeSize;

    /**
     * Generates a new Position for the Dot that is not on the Snake head
     */
    void changeToNewPosition() {
        //TODO Check all and the whole Snake
        //Generates New position
        Random r = new Random();
        while (mX == SnakeHead.xHead && mY == SnakeHead.yHead || mX < 0 || mX > 760 || mY < 0 || mY > 760) {
            mX = (r.nextInt(41) * 20);
            mY = (r.nextInt(41) * 20);
        }
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
