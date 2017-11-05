package ch.snake;

public class Coordinates {
    int newX, newY;

    public int getNewX() {
        return newX;
    }

    public void setNewX(int newX) {
        this.newX = newX;
    }

    public int getNewY() {
        return newY;
    }

    public void setNewY(int newY) {
        this.newY = newY;
    }

    public void setPos(int x,int y){
        this.newY =y;
        this.newX =x;
    }
}
