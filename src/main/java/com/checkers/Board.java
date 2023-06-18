package com.checkers;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.TableModel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// import logic.MoveGenerator;
// import model.Board;
// import model.Game;
// import model.HumanPlayer;
// import model.NetworkPlayer;
// import model.Player;
// import network.Command;
// import network.Session;

//https://archiwum.warcaby.pl/kodeks-warcabowy/134-rozdzial-i-oficjalne-reguly-gry-w-warcaby
//konkretne zasady..

public class Board extends JPanel {

    private MainWindow window;
    private boolean COLOR;
    private int width, height;
    private Game game;


    public Board(MainWindow window, int width, int height) {
        super.setBackground(Color.LIGHT_GRAY);

        this.width = width;
        this.height = height;
        this.window = window;
        this.game = new Game(this, width, height);

        //tiles = new Tile[8][8];
        //pawns = new Pawn[8][8];
        game.setTiles();
        game.setPawns();

    }

    // public Board(MainWindow window) { //Game game, Player player1, Player player2

    // }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(8 * 50, 8 * 50);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);


        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                game.tiles[row][col].draw(g, 50);
                if (game.pawns[row][col] != null) {
                    game.pawns[row][col].draw(g, 50);
                }
            }
        }

        g.setColor(Color.YELLOW);
        if(!game.maxCapturePath.isEmpty()){
            for(Pawn pawn: game.pawnsWithAvaibileCaptures){
            g.drawRect(pawn.getX() * 50, pawn.getY() * 50, 50, 50);
            }
        }
        if(!game.maxMaxCapturePath.isEmpty()){
            g.setColor(Color.RED);
            int captureX;
            int captureY;
            if(game.kingMultiCaptures){

            }
            for(List<int[]> list : game.maxMaxCapturePath){
                for (int[] coords : list) { // draw only maxCapture path
                    //System.out.println("list: "+coords[0]+", "+coords[1]);

                    g.drawRect(coords[0] * 50, coords[1] * 50, 50, 50);
                }
            }
        }


        if (game.selectedPawn != null) {
            g.setColor(Color.YELLOW);
            int selectedX = game.selectedPawn.getX();
            int selectedY = game.selectedPawn.getY();
            g.drawRect(selectedX * 50, selectedY * 50, 50, 50);

            g.setColor(Color.GREEN);
            if (game.maxCapturePath.isEmpty()) {
                for (int row = 0; row < 8; row++) {
                    for (int col = 0; col < 8; col++) {
                        if(game.selectedPawn.isKing()){
                            if (game.isValidMove(game.selectedPawn, col, row)) {
                                g.drawRect(row * 50, col * 50, 50, 50);
                            }
                        }else
                            if (game.isValidMove(game.selectedPawn, row, col)) {
                                g.drawRect(col * 50, row * 50, 50, 50);
                            }
                    }
                }

            } else {
            //     g.setColor(Color.RED);
            //     int captureX;
            //     int captureY;

            //     for(List<int[]> list : maxMaxCapturePath){
            //         for (int[] coords : list) { // draw only maxCapture path
            //             g.drawRect(coords[0] * 50, coords[1] * 50, 50, 50);
            //         }
            //     }
            //     g.setColor(Color.GREEN);
            }

        }

        if (game.gameOver) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            String message = "Game Over! " + (game.winner == Color.WHITE ? "White" : "Black") + " wins!";
            int messageWidth = g.getFontMetrics().stringWidth(message);
            int x = (getWidth() - messageWidth) / 2;
            int y = getHeight() / 2;
            g.drawString(message, x, y);
        }

    }

}
