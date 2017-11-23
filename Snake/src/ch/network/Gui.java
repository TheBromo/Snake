package ch.network;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

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

class Gui extends JFrame {

    Gui(int sizeX, int sizeY) throws IOException {
        super("Snake");
        sizeX -= 10;
        sizeY -= 10;

        //Finds out the exact Border
        JFrame f = new JFrame();
        f.setSize(100, 100);
        f.setVisible(true);
        int widthBorder = f.getWidth() - f.getContentPane().getWidth();
        int heightBorder = f.getHeight() - f.getContentPane().getHeight();
        f.dispose();

        //Settings of JFrame
        int newSizeX = sizeX + widthBorder, newSizeY = sizeY + heightBorder;
        setSize(newSizeX, newSizeY);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.black);

        //adds 2d GraphicsComponent
        Draw lbl;

        lbl = new Draw();

        lbl.setBounds(0, 0, sizeX, sizeY);
        getContentPane().add(lbl);
        addKeyListener(lbl);
        lbl.repaint();

        setVisible(true);
        requestFocus();
    }
}
