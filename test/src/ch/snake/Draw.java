package ch.snake;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.InetAddress;
import java.util.HashMap;
import javax.swing.*;

class Draw extends JLabel implements KeyListener {
    //temporary will be moved to lobby handler


    private static boolean nameVisible;
    private static Color[] colors = new Color[10];
    private Dot dot = new Dot();

    static int snakeSize = 10;
    private int interval = (int) (100 / (20 / snakeSize));



    //Tail needs to be turned into an array for multiplayer
    //private Tail[] player = new Tail[10];
    public  HashMap<InetAddress, Coordinates> heads = new HashMap<>();
    private Tail p = new Tail("ThrBromo");

    private long last = 0;

    public Draw(){
        generateHSB();
    }

    protected void paintComponent(Graphics g) {


        //TODO Add Network
        super.paintComponent(g);
        // is used for the size adjustment to the Screen
        Rectangle bounds = getBounds();

        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //records the time to update snake Position in regular interval
        long now = System.currentTimeMillis();

        int[] xArray;
        int[] yArray;



        //every 100ms it readjusts the tail and checks if the dot gets eaten
        if (now - last >= interval) {
            /*
                Snake head must be set here
             */

            if (p.isAlive()) {
                //The Movement in Steps of 20
                if (SnakeHead.nextDir == 'N') {
                    SnakeHead.yHead -= snakeSize;
                    SnakeHead.lastChar = 'N';
                } else if (SnakeHead.nextDir == 'E') {
                    SnakeHead.xHead += snakeSize;
                    SnakeHead.lastChar = 'E';
                } else if (SnakeHead.nextDir == 'S') {
                    SnakeHead.yHead += snakeSize;
                    SnakeHead.lastChar = 'S';
                } else if (SnakeHead.nextDir == 'W') {
                    SnakeHead.xHead -= snakeSize;
                    SnakeHead.lastChar = 'W';
                }
            }
        }
        /*
            SnakeHead data must be received here
            Network stuff here

         */


        if (now - last >= interval) {
            if (p.isAlive()) {
                p.refresh(SnakeHead.yHead, SnakeHead.xHead);
                p.dotCheck();
            } else {
                p.reset();
            }


            /*
            for (int i = 0; i < 10; i++) {
                int[] secondXArray = player[i].getXCor();
                int[] secondYArray = player[i].getYCor();
                //Checks for Collision
                for (int x = 1; x < player[0].getLength(); x++) {
                    if (xArray[0] == secondXArray[x]) {
                        if (yArray[0] == secondYArray[x]) {
                            lost = true;
                            SnakeHead.yHead = 400;
                            SnakeHead.xHead = 400;
                        }
                    }
                }
            }
*/

            //copies coordinates of main snake (work in progress, needs to work for multiple snakes)
            xArray = p.getXCor();
            yArray = p.getYCor();

            if (p.isAlive()) {
                //stops head from crossing border temporary normally Game Over
                if (SnakeHead.xHead < 0 || SnakeHead.xHead + snakeSize + 1 >= bounds.width || SnakeHead.yHead < 0 || SnakeHead.yHead + snakeSize + 1 >= bounds.height) {
                    System.out.println("Game Over");
                    p.setAlive(false);
                }


                //checks if the snake collides with itself
                for (int x = 1; x < p.getLength(); x++) {
                    if (xArray[0] == xArray[x]) {
                        if (yArray[0] == yArray[x]) {
                            System.out.println("Game Over");
                            p.setAlive(false);
                        }
                    }
                }
            }


            last = now;
        }
        xArray = p.getXCor();
        yArray = p.getYCor();


        //draws snake
        for (int x = 0; x <= p.getLength() - 1; x++) {
            //needs to be adjusted for multiplayer
            g.setColor(p.getColor());
            g.fillRect(xArray[x], yArray[x], snakeSize, snakeSize);
        }

        //paints dot
        g.setColor(Color.white);
        g.fillRect(dot.getX(), dot.getY(), dot.getSizeX(), dot.getSizeY());

        if (nameVisible) {
            int shift = 15 , counter=0;
            //Displays all the names of the users in their according color
            for (InetAddress key: Lobby.users.keySet()) {
                g.setColor(Lobby.users.get(key).getColor());
                g.fillRect(counter * (bounds.width / Lobby.users.size()) + shift, 10, 20, 20);
                g.drawString(Lobby.users.get(key).getName(), counter * (bounds.width / Lobby.users.size()) + 25 + shift, 25);
                g.setColor(Color.black);
                int newX = counter * (bounds.width / Lobby.users.size()) + shift;
                int length = String.valueOf(Lobby.users.get(key).getScore()).length();
                if (length == 1) {
                    newX += 7;
                } else if (length == 2) {
                    newX += 3;
                }
                g.drawString("" + Lobby.users.get(key).getScore(), newX, 25);
                counter++;
            }
        }
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_W) {
            if (SnakeHead.lastChar != 'S') {
                SnakeHead.nextDir = 'N';
            }
        } else if (e.getKeyCode() == KeyEvent.VK_A) {
            if (SnakeHead.lastChar != 'E') {
                SnakeHead.nextDir = 'W';
            }
        } else if (e.getKeyCode() == KeyEvent.VK_S) {
            if (SnakeHead.lastChar != 'N') {
                SnakeHead.nextDir = 'S';
            }
        } else if (e.getKeyCode() == KeyEvent.VK_D) {
            if (SnakeHead.lastChar != 'W') {
                SnakeHead.nextDir = 'E';
            }
        } else if (e.getKeyCode() == KeyEvent.VK_U) {
            nameVisible = true;
        } else if (e.getKeyCode() == KeyEvent.VK_F) {
            p.setAlive(true);
            SnakeHead.yHead = 400;
            SnakeHead.xHead = 400;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_U) {
            nameVisible = false;
        }
    }

    private void generateHSB() {
        //generates player colors
        for (int i = 0; i < Lobby.users.size(); i++) {
            colors[i] = Color.getHSBColor((float) (0.1 * i), (float) (0.5), (float) (1.0));

        }
        p.setColor(colors[0]);
        setColors();
    }
    private void setColors(){
        int counter=0;
        for (InetAddress key:Lobby.users.keySet()){
            Lobby.users.get(key).setColor(colors[counter]);
            counter++;
        }
    }
}