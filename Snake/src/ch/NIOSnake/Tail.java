package ch.NIOSnake;

import java.util.Random;

public class Tail {
    private int x,y;
    private String name;
    private boolean alive;

//should simulate the movement
    public void refresh(){
        Random random = new Random();
        x= random.nextInt();
        y=random.nextInt();

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }
}
