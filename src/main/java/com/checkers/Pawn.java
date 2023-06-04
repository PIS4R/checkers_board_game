package com.checkers;
import java.awt.*;


class Pawn {
    private int x;
    private int y;
    private Color color;
    private boolean isKing;

    public Pawn(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.isKing = false;
    }
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public Color getColor() {
        return this.color;
    }
    public void setColor(Color newColor) {
        this.color = newColor;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
    public boolean isKing() {
        return isKing;
    }

    public void makeKing() {
        isKing = true;
    }


    public void draw(Graphics g, int tileSize) {
        int xOffset = tileSize / 4;
        int yOffset = tileSize / 4;

        int pieceHeight = tileSize / 2;
        if (isKing) {
            pieceHeight = tileSize;
        }

        g.setColor(color);
        //g.fillOval(x * tileSize + xOffset, y * tileSize + yOffset, tileSize / 2, tileSize / 2);
        g.fillOval((x * tileSize)  + xOffset, (y * tileSize) + yOffset, tileSize / 2,  pieceHeight);

    }

}
