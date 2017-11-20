package ch.snake;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.*;
import javax.swing.*;

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

class Draw extends JLabel implements KeyListener {

    private static boolean nameVisible;
    private static Color[] colors = new Color[100];
    private Dot dot = new Dot();
    static int snakeSize = Lobby.getSnakeSize();
    private int interval = (int) (100 / (20 / snakeSize));
    private long last = 0;
    private Network mNetwork;
    private Collision mCollision;
    private int[] xArray;
    private int[] yArray;
    private HUD mHUD;

    public Draw() throws IOException {
        generateHSB();
        mNetwork = new Network();
        mCollision = new Collision();
        mHUD = new HUD();
        mNetwork.sendPacket(Lobby.getUsers(), Lobby.getHeads(), PacketType.NAME);
    }

    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        // is used for the size adjustment to the Screen
        Rectangle bounds = getBounds();

        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //records the time to update snake Position in regular interval
        long now = System.currentTimeMillis();


        //every 100ms it readjusts the tail and checks if the dot gets eaten
        if (now - last >= interval) {

            try {
                if (Lobby.getUsers().get(InetAddress.getLocalHost()).isAlive()) {
                    //The Movement in Steps of
                    if (SnakeHead.nextDir == 'N') {
                        int y = Lobby.getHeads().get(InetAddress.getLocalHost()).getNewY();
                        Lobby.getHeads().get(InetAddress.getLocalHost()).setNewY(y - snakeSize);
                        SnakeHead.lastChar = 'N';
                    } else if (SnakeHead.nextDir == 'E') {
                        int x = Lobby.getHeads().get(InetAddress.getLocalHost()).getNewX();
                        Lobby.getHeads().get(InetAddress.getLocalHost()).setNewX(x + snakeSize);
                        SnakeHead.lastChar = 'E';
                    } else if (SnakeHead.nextDir == 'S') {
                        int y = Lobby.getHeads().get(InetAddress.getLocalHost()).getNewY();
                        Lobby.getHeads().get(InetAddress.getLocalHost()).setNewY(y + snakeSize);
                        SnakeHead.lastChar = 'S';
                    } else if (SnakeHead.nextDir == 'W') {
                        int x = Lobby.getHeads().get(InetAddress.getLocalHost()).getNewX();
                        Lobby.getHeads().get(InetAddress.getLocalHost()).setNewX(x - snakeSize);
                        SnakeHead.lastChar = 'W';
                    }
                    mNetwork.sendPacket(Lobby.getUsers(), Lobby.getHeads(), PacketType.COORDINATES);

                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        try {
            mNetwork.receivePacket(Lobby.getUsers(), Lobby.getHeads());
            mNetwork.resend();
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (now - last >= interval) {
            for (InetAddress key : Lobby.getUsers().keySet()) {
                xArray = Lobby.getUsers().get(key).getXCor();
                yArray = Lobby.getUsers().get(key).getYCor();
                if (Lobby.getUsers().get(key).isAlive()) {
                    //moves the snake tail further
                    Lobby.getUsers().get(key).refresh(Lobby.getHeads().get(key).getNewY(), Lobby.getHeads().get(key).getNewX());
                    //Checks if snake collides with the dot
                    //TODO Does not work
                    Lobby.getUsers().get(key).dotCheck(dot);
                    //checks if the snake collides with the border
                    mCollision.checkBorders(Lobby.getHeads().get(key), Lobby.getUsers().get(key), snakeSize, bounds);


                    //checks if the snake collides with other snakes
                    for (InetAddress secondSnakeKey : Lobby.getUsers().keySet()) {
                        mCollision.checkSnakes(Lobby.getUsers().get(key), Lobby.getUsers().get(secondSnakeKey));
                    }

                } else {
                    //keeps the snake in place when it is dead
                    Lobby.getUsers().get(key).reset();
                }
            }
            last = now;
        }


        for (InetAddress key : Lobby.getUsers().keySet()) {
            //draws all the snakes
            Lobby.getUsers().get(key).draw(g, snakeSize);
        }

        //paints dot
        g.setColor(Color.white);
        g.fillRect(dot.getX(), dot.getY(), dot.getSizeX(), dot.getSizeY());

        if (nameVisible) {
            mHUD.showStats(g, bounds);
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
        } else if (e.getKeyCode() == KeyEvent.VK_T) {
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