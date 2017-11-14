package ch.snake;

import java.awt.*;

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
