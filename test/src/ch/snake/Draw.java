package ch.snake;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import javax.swing.*;

class Draw extends JLabel implements KeyListener {

    private static boolean nameVisible;
    private static Color[] colors = new Color[Lobby.getUsers().size()];
    private Dot dot = new Dot();
    static int snakeSize = 10;
    private int interval = (int) (100 / (20 / snakeSize));
    public static HashMap<InetAddress, Coordinates> heads = new HashMap<>();
    private long last = 0;

    public Draw() {
        generateHSB();
    }

    protected void paintComponent(Graphics g) {

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
            try {
                if (Lobby.getUsers().get(InetAddress.getLocalHost()).isAlive()) {
                    //The Movement in Steps of 20
                    if (SnakeHead.nextDir == 'N') {
                        int y = heads.get(InetAddress.getLocalHost()).getNewY();
                        heads.get(InetAddress.getLocalHost()).setNewY(y - snakeSize);
                        SnakeHead.lastChar = 'N';
                    } else if (SnakeHead.nextDir == 'E') {
                        int x = heads.get(InetAddress.getLocalHost()).getNewX();
                        heads.get(InetAddress.getLocalHost()).setNewX(x + snakeSize);
                        SnakeHead.lastChar = 'E';
                    } else if (SnakeHead.nextDir == 'S') {
                        int y = heads.get(InetAddress.getLocalHost()).getNewY();
                        heads.get(InetAddress.getLocalHost()).setNewY(y + snakeSize);
                        SnakeHead.lastChar = 'S';
                    } else if (SnakeHead.nextDir == 'W') {
                        int x = heads.get(InetAddress.getLocalHost()).getNewX();
                        heads.get(InetAddress.getLocalHost()).setNewX(x - snakeSize);
                        SnakeHead.lastChar = 'W';
                    }
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        /*
            TODO SnakeHead data must be received here
            TODO Network stuff here
            TODO Reform the code so that it checks if Coordinates have changed and then check collision...
         */


        if (now - last >= interval) {
            for (InetAddress key : Lobby.getUsers().keySet()) {
                xArray = Lobby.getUsers().get(key).getXCor();
                yArray = Lobby.getUsers().get(key).getYCor();
                if (Lobby.getUsers().get(key).isAlive()) {
                    Lobby.getUsers().get(key).refresh(heads.get(key).getNewY(), heads.get(key).getNewX());
                    Lobby.getUsers().get(key).dotCheck();
                    if (heads.get(key).getNewX() < 0 || heads.get(key).getNewX() + snakeSize + 1 >= bounds.width || heads.get(key).getNewY() < 0 || heads.get(key).getNewY() + snakeSize + 1 >= bounds.height) {
                        Lobby.getUsers().get(key).setAlive(false);
                    }
                    for (InetAddress secondSnakeKey : Lobby.getUsers().keySet()) {
                        int[] secondXArray = Lobby.getUsers().get(secondSnakeKey).getXCor();
                        int[] secondYArray = Lobby.getUsers().get(secondSnakeKey).getYCor();
                        for (int x = 1; x < Lobby.getUsers().get(secondSnakeKey).getLength(); x++) {
                            if (xArray[0] == secondXArray[x]) {
                                if (yArray[0] == secondYArray[x]) {
                                    Lobby.getUsers().get(key).setAlive(false);
                                }
                            }
                        }
                    }

                } else {
                    Lobby.getUsers().get(key).reset();
                }
            }
            last = now;
        }

        for (InetAddress key : Lobby.getUsers().keySet()) {
            xArray = Lobby.getUsers().get(key).getXCor();
            yArray = Lobby.getUsers().get(key).getYCor();

            for (int x = 0; x <= Lobby.getUsers().get(key).getLength() - 1; x++) {
                g.setColor(Lobby.getUsers().get(key).getColor());
                g.fillRect(xArray[x], yArray[x], snakeSize, snakeSize);
            }
        }

        //paints dot
        g.setColor(Color.white);
        g.fillRect(dot.getX(), dot.getY(), dot.getSizeX(), dot.getSizeY());

        if (nameVisible) {
            int shift = 15, counter = 0;
            //Displays all the names of the users in their according color
            for (InetAddress key : Lobby.getUsers().keySet()) {
                g.setColor(Lobby.getUsers().get(key).getColor());
                g.fillRect(counter * (bounds.width / Lobby.getUsers().size()) + shift, 10, 20, 20);
                g.drawString(Lobby.getUsers().get(key).getName(), counter * (bounds.width / Lobby.getUsers().size()) + 25 + shift, 25);
                g.setColor(Color.black);
                int newX = counter * (bounds.width / Lobby.getUsers().size()) + shift;
                int length = String.valueOf(Lobby.getUsers().get(key).getScore()).length();
                if (length == 1) {
                    newX += 7;
                } else if (length == 2) {
                    newX += 3;
                }
                g.drawString("" + Lobby.getUsers().get(key).getScore(), newX, 25);
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
        for (int i = 0; i < Lobby.getUsers().size(); i++) {
            colors[i] = Color.getHSBColor((float) (0.1 * i), (float) (0.5), (float) (1.0));

        }
        setColors();
    }

    private void setColors() {
        int counter = 0;
        for (InetAddress key : Lobby.getUsers().keySet()) {
            Lobby.getUsers().get(key).setColor(colors[counter]);
            counter++;
        }
    }
}