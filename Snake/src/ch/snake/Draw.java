package ch.snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

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

    static int snakeSize = Lobby.getSnakeSize();
    private static boolean nameVisible;
    private static Color[] colors = new Color[100];
    private Dot dot = new Dot();
    private int interval = (int) (100 / (20 / snakeSize));
    private long last = 0, last2 = 0;
    private Network mNetwork;
    private Collision mCollision;
    private HUD mHUD;
    private HashMap<InetAddress, Coordinates> heads;
    private HashMap<InetAddress, Tail> users;

    public Draw() throws IOException {

        heads = Lobby.getHeads();
        users = Lobby.getUsers();

        generateHSB();

        mNetwork = new Network();
        mCollision = new Collision();
        mHUD = new HUD();

        mNetwork.sendPacket(users, heads, PacketType.NAME);
        mNetwork.receivePacket(users, heads);
        mNetwork.waitForConnection(users,heads);
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
                mNetwork.sendPacket(users, heads, PacketType.ADVANCEDDIR);
                for (InetAddress key : users.keySet()) {
                    if (users.get(key).isAlive()) {
                        //The Movement in Steps of
                        if (heads.get(key).nextDir == 'N') {
                            int y = heads.get(key).getNewY();
                            heads.get(key).setNewY(y - snakeSize);
                            heads.get(key).lastChar = 'N';
                        } else if (heads.get(key).nextDir == 'E') {
                            int x = heads.get(key).getNewX();
                            heads.get(key).setNewX(x + snakeSize);
                            heads.get(key).lastChar = 'E';
                        } else if (heads.get(key).nextDir == 'S') {
                            int y = heads.get(key).getNewY();
                            heads.get(key).setNewY(y + snakeSize);
                            heads.get(key).lastChar = 'S';
                        } else if (heads.get(key).nextDir == 'W') {
                            int x = heads.get(key).getNewX();
                            heads.get(key).setNewX(x - snakeSize);
                            heads.get(key).lastChar = 'W';
                        }
                    }
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        try {
            mNetwork.receivePacket(users, heads);
            if (now - last2 >= 10) {
                mNetwork.sendPacket(users,heads,PacketType.RESEND);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (now - last >= interval) {
            for (InetAddress key : users.keySet()) {
                int[] xArray = users.get(key).getXCor();
                int[] yArray = users.get(key).getYCor();

                if (users.get(key).isAlive()) {

                    //moves the snake tail further
                    users.get(key).refresh(heads.get(key).getNewY(), heads.get(key).getNewX());

                    //Checks if snake collides with the dot
                    //TODO Does not work
                    users.get(key).dotCheck(dot);

                    //checks if the snake collides with the border
                    mCollision.checkBorders(heads.get(key), users.get(key), snakeSize, bounds);

                    //checks if the snake collides with other snakes
                    for (InetAddress secondSnakeKey : users.keySet()) {
                        mCollision.checkSnakes(users.get(key), users.get(secondSnakeKey));
                    }

                } else {

                    //keeps the snake in place when it is dead
                    users.get(key).reset();
                }
            }
            last = now;
        }


        for (InetAddress key : users.keySet()) {

            //draws all the snakes
            users.get(key).draw(g, snakeSize);
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
        try {
            if (e.getKeyCode() == KeyEvent.VK_W) {

                if (heads.get(InetAddress.getByName(InetAddress.getLocalHost().getHostAddress())).lastChar != 'S') {
                    heads.get(InetAddress.getByName(InetAddress.getLocalHost().getHostAddress())).nextDir = 'N';
                }

            } else if (e.getKeyCode() == KeyEvent.VK_A) {
                if (heads.get(InetAddress.getByName(InetAddress.getLocalHost().getHostAddress())).lastChar != 'E') {
                    heads.get(InetAddress.getByName(InetAddress.getLocalHost().getHostAddress())).nextDir = 'W';
                }
            } else if (e.getKeyCode() == KeyEvent.VK_S) {
                if (heads.get(InetAddress.getByName(InetAddress.getLocalHost().getHostAddress())).lastChar != 'N') {
                    heads.get(InetAddress.getByName(InetAddress.getLocalHost().getHostAddress())).nextDir = 'S';
                }
            } else if (e.getKeyCode() == KeyEvent.VK_D) {
                if (heads.get(InetAddress.getByName(InetAddress.getLocalHost().getHostAddress())).lastChar != 'W') {
                    heads.get(InetAddress.getByName(InetAddress.getLocalHost().getHostAddress())).nextDir = 'E';
                }
            } else if (e.getKeyCode() == KeyEvent.VK_T) {
                nameVisible = true;
            }
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
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
        for (int i = 0; i < users.size(); i++) {
            colors[i] = Color.getHSBColor((float) (0.1 * i), (float) (0.5), (float) (1.0));
        }
        setColors();
    }

    private void setColors() {

        int counter = 0;
        for (InetAddress key : users.keySet()) {
            users.get(key).setColor(colors[counter]);
            counter++;
        }
    }
}