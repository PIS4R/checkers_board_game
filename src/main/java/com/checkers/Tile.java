package com.checkers;


import java.awt.*;

import javax.swing.JButton;
import javax.swing.JPanel;


public class Tile extends JButton{  //Rectangle

    //private Piece piece;
    private int x;
    private int y;
    private Color color;

    public int RECT_X;
    public int RECT_Y;

    private int RECT_HEIGHT = 50;
    private int RECT_WIDTH = 50;


    public Tile(int x, int y, Color color){


        // this.color = color;
        // this.RECT_X = x;
        // this.RECT_Y = y;
        this.x = x;
        this.y = y;
        this.color = color;
        // this.RECT_WIDTH = width;
        // this.RECT_HEIGHT = height;
        //System.out.println(RECT_X);
        //System.out.println(RECT_Y);

    }


    public void draw(Graphics g, int tileSize) {
        g.setColor(color);
        g.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
    }


    public Color getColor() {
        return null;
    }



}
