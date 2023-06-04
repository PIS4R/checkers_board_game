package com.checkers;
import java.awt.*;


class Pawn {
    private int x;
    private int y;
    private Color color;

    public Pawn(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void draw(Graphics g, int tileSize) {
        int xOffset = tileSize / 4;
        int yOffset = tileSize / 4;

        g.setColor(color);
        //g.fillOval(x * tileSize + xOffset, y * tileSize + yOffset, tileSize / 2, tileSize / 2);
        g.fillOval((x * tileSize)  + xOffset, (y * tileSize) + yOffset, tileSize / 2, tileSize / 2);

    }

}
