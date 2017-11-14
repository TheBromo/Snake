package ch.snake;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.Iterator;
import javax.swing.*;

class Draw extends JLabel implements KeyListener {

    private static boolean nameVisible;
    private static Color[] colors = new Color[100];
    private Dot dot = new Dot();
    static int snakeSize = Lobby.getSnakeSize();
    private int interval = (int) (100 / (20 / snakeSize));
    private long last = 0, counter = 0;
    private DatagramChannel socket;
    private Selector selector;
    ByteBuffer readBuffer, writeBuffer;

    public Draw() {
        generateHSB();
        try {
            socket = DatagramChannel.open();
            socket.configureBlocking(false);
            socket.bind(new InetSocketAddress(23723));

            selector = Selector.open();
            socket.register(selector, SelectionKey.OP_READ);

            readBuffer = ByteBuffer.allocate(1024);
            writeBuffer = ByteBuffer.allocate(1024);


        } catch (IOException e) {
            e.printStackTrace();
        }
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
                    //System.out.println("The real coordinates:\ny:"+Lobby.getHeads().get(InetAddress.getLocalHost()).getNewX()+" \ny:"+Lobby.getHeads().get(InetAddress.getLocalHost()).getNewY());
                    try {

                        //Sends the new Coordinates
                        writeBuffer.position(0).limit(writeBuffer.capacity());

                        writeBuffer.putInt(Lobby.getHeads().get(InetAddress.getLocalHost()).getNewX());
                        writeBuffer.putInt(Lobby.getHeads().get(InetAddress.getLocalHost()).getNewY());
                        // X , Y, AliveBool, CheckNumber
                        int bool = 0;
                        if (Lobby.getUsers().get(InetAddress.getLocalHost()).isAlive()) {
                            bool = 1;
                        }
                        writeBuffer.putInt(bool);
                        writeBuffer.putLong(counter);
                        writeBuffer.flip();
                        for (InetAddress address : Lobby.getUsers().keySet()) {
                            if (!address.equals(InetAddress.getLocalHost())) {
                                InetSocketAddress socketAddress = new InetSocketAddress(address, 23723);
                                socket.send(writeBuffer, socketAddress);
                            }
                        }


                        long received = -1;
                        while (counter != received) {

                        }

                        //must be at the end
                        counter++;
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }


        try {

            if (selector.selectNow() > 0) {
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();

                    if (key.isReadable()) {
                        //reads the new coordinates
                        readBuffer.position(0).limit(readBuffer.capacity());
                        SocketAddress sender = socket.receive(readBuffer);
                        readBuffer.flip();
                        System.out.println(sender);
                        InetSocketAddress socketAddress = (InetSocketAddress) sender;
                        Lobby.getHeads().get(socketAddress.getAddress()).setPos(readBuffer.getInt(), readBuffer.getInt());

                        if (readBuffer.getInt() == 1) {
                            Lobby.getUsers().get(InetAddress.getLocalHost()).setAlive(true);

                        } else {
                            Lobby.getUsers().get(InetAddress.getLocalHost()).setAlive(false);
                        }
                        writeBuffer.position(0).limit(writeBuffer.capacity());
                        writeBuffer.putLong(readBuffer.getLong());
                        writeBuffer.flip();
                        for (InetAddress address : Lobby.getUsers().keySet()) {
                            if (!address.equals(InetAddress.getLocalHost())) {
                                InetSocketAddress inetsocketAddress = new InetSocketAddress(address, 23723);
                                socket.send(writeBuffer, inetsocketAddress);
                            }
                        }

                        System.out.println("Received Coordinates: " + readBuffer.getInt(0) + " " + readBuffer.getInt(4));
                        System.out.println("Alive?: " + Lobby.getUsers().get(socketAddress.getAddress()).isAlive() + "\n");

                    }
                    keys.remove();
                }
            }
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
                    if (Lobby.getHeads().get(key).getNewX() < 0 || Lobby.getHeads().get(key).getNewX() + snakeSize + 1 >= bounds.width || Lobby.getHeads().get(key).getNewY() < 0 || Lobby.getHeads().get(key).getNewY() + snakeSize + 1 >= bounds.height) {
                        Lobby.getUsers().get(key).setAlive(false);
                    }
                    /*
                    //checks if the snake collides with other snakes
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
                    }*/

                } else {
                    //keeps the snake in place when it is dead
                    Lobby.getUsers().get(key).reset();
                }
            }
            last = now;
        }

        for (InetAddress key : Lobby.getUsers().keySet()) {
            //draws all the snakes
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
        } else if (e.getKeyCode() == KeyEvent.VK_TAB) {
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