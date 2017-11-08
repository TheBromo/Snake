package ch.snake;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

class Gui extends JFrame {

   Gui(int sizeX, int sizeY) {
      super("Snake");
      sizeX-=10;
      sizeY-=10;

      //Finds out the exact Border
      JFrame f = new JFrame();
      f.setSize(100, 100);
      f.setVisible(true);
      int widthBorder = f.getWidth() - f.getContentPane().getWidth();
      int heightBorder = f.getHeight() - f.getContentPane().getHeight();
      f.dispose();

      //Settings of JFrame
      int newSizeX =sizeX + widthBorder,newSizeY=sizeY + heightBorder;
      setSize(newSizeX, newSizeY);
      setResizable(false);
      setLocationRelativeTo(null);
      setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      getContentPane().setBackground(Color.black);

      //adds 2d GraphicsComponent
      Draw lbl = null;
      try {
         lbl = new Draw();
      } catch (IOException e) {
         e.printStackTrace();
      }
      lbl.setBounds(0, 0, sizeX, sizeY);
      getContentPane().add(lbl);
      addKeyListener(lbl);
      lbl.repaint();

      setVisible(true);
      requestFocus();
   }
}
