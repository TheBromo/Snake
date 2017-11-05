package ch.snake;

import java.net.InetAddress;
import java.util.Random;

class Dot {

    private static int mX = 400, mY = 400, mSizeX = Draw.snakeSize, mSizeY = Draw.snakeSize;
    Random r = new Random();

    public Dot() {
        r.setSeed(Lobby.seed);

    }

    /**
     * Generates a new Position for the Dot that is not on the Snake head
     */
    void changeToNewPosition() {
        //TODO Check all and the whole Snake
        //Generates New position
        while (onTopOfSnake()|| mX < 0 || mX > 760 || mY < 0 || mY > 760) {
            mX = (r.nextInt(41) * 20);
            mY = (r.nextInt(41) * 20);
        }
    }
    boolean onTopOfSnake(){
        for (InetAddress key:Lobby.users.keySet()){
            int[] xArray = Lobby.users.get(key).getXCor();
            int[] yArray = Lobby.users.get(key).getYCor();
            for (int x = 1; x < Lobby.users.get(key).getLength(); x++) {
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
