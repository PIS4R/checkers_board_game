package com.checkers;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

public class Board extends JPanel{


	//private Game game;
    private MainWindow window;
    private boolean COLOR;

    private int width, height;


    private Tile[][] tiles;
    private Pawn[][] pawns;
    private transient Pawn selectedPawn;
    private Color currentPlayer;
    private boolean gameOver;
    private Color winner;
    private boolean capturingMoveAvailable;


    public Board(MainWindow window, int width, int height){
        //setSize( 400, 400 );
        super.setBackground(Color.LIGHT_GRAY);

        this.width = width;
        this.height = height;
        this.window = window;

        currentPlayer = Color.BLACK;
        gameOver = false;
        capturingMoveAvailable = false;


        tiles = new Tile[8][8];
        pawns = new Pawn[8][8];
        setTiles();
        setPawns();

        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

                if (gameOver) {
                    return;
                }

                int mouseX = e.getX();
                int mouseY = e.getY();

                int row = mouseY / 50;
                int col = mouseX / 50;

                if (selectedPawn == null) {
                    if (pawns[row][col] != null && pawns[row][col].getColor() == currentPlayer) {
                        selectedPawn = pawns[row][col];
                        repaint();
                    }
                } else{
                    if(isValidMove(selectedPawn, row, col)){
                        performMove(selectedPawn, row, col);
                        checkForKing(selectedPawn);
                        checkForGameOver();
                        selectedPawn = null;
                        switchPlayer();
                        repaint();
                    } else{
                        selectedPawn = pawns[row][col];
                    }
                    repaint();
                }
            }
        });
	}

    // public Board(MainWindow window) { //Game game, Player player1, Player player2

    // }

    private void setTiles(){
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Color color = (row + col) % 2 == 0 ? Color.WHITE : Color.GRAY;
                tiles[row][col] = new Tile(col, row, color);
            }
        }
    }

    private void setPawns(){
        pawns[0][1] = new Pawn(1, 0, Color.BLACK);
        pawns[0][3] = new Pawn(3, 0, Color.BLACK);
        pawns[0][5] = new Pawn(5, 0, Color.BLACK);
        pawns[0][7] = new Pawn(7, 0, Color.BLACK);

        pawns[1][0] = new Pawn(0, 1, Color.BLACK);
        pawns[1][2] = new Pawn(2, 1, Color.BLACK);
        pawns[1][4] = new Pawn(4, 1, Color.BLACK);
        pawns[1][6] = new Pawn(6, 1, Color.BLACK);

        pawns[2][1] = new Pawn(1, 2, Color.BLACK);
        pawns[2][3] = new Pawn(3, 2, Color.BLACK);
        pawns[2][5] = new Pawn(5, 2, Color.BLACK);
        pawns[2][7] = new Pawn(7, 2, Color.BLACK);


        pawns[7][0] = new Pawn(0, 7, Color.WHITE);
        pawns[7][2] = new Pawn(2, 7, Color.WHITE);
        pawns[7][4] = new Pawn(4, 7, Color.WHITE);
        pawns[7][6] = new Pawn(6, 7, Color.WHITE);

        pawns[6][1] = new Pawn(1, 6, Color.WHITE);
        pawns[6][3] = new Pawn(3, 6, Color.WHITE);
        pawns[6][5] = new Pawn(5, 6, Color.WHITE);
        pawns[6][7] = new Pawn(7, 6, Color.WHITE);

        pawns[5][0] = new Pawn(0, 5, Color.WHITE);
        pawns[5][2] = new Pawn(2, 5, Color.WHITE);
        pawns[5][4] = new Pawn(4, 5, Color.WHITE);
        pawns[5][6] = new Pawn(6, 5, Color.WHITE);
    }

    private boolean isValidMove(Pawn pawn, int newRow, int newCol) {
        if (newRow < 0 || newRow >= 8 ||
            newCol < 0 || newCol >= 8 ||
            pawns[newRow][newCol] != null) {
                return false;
        }

        if(pawn.getColor() != currentPlayer)
            return false;

        int currentRow = pawn.getY();
        int currentCol = pawn.getX();

        int rowDiff = newRow - currentRow;
        int colDiff = newCol - currentCol;

        //return rowDiff == colDiff;
        if (Math.abs(rowDiff) != Math.abs(colDiff)) {
            return false;
        }

        if (!pawn.isKing()) {
            if (pawn.getColor() == Color.BLACK && rowDiff < 0) {
                return false;
            }
            if (pawn.getColor() == Color.WHITE && rowDiff > 0) {
                return false;
            }
        }


        if (Math.abs(rowDiff) == 1 && Math.abs(colDiff) == 1) {
            return true;
        }

        int rowDirection = rowDiff > 0 ? 1 : -1;
        int colDirection = colDiff > 0 ? 1 : -1;

        int midRow = currentRow + rowDirection;
        int midCol = currentCol + colDirection;

        while (midRow != newRow && midCol != newCol) {

            if(midRow > 7 || midCol > 7)
                break;

            if (pawns[midRow][midCol] != null &&
                pawns[midRow][midCol].getColor() != pawn.getColor()) {

                midRow += rowDirection;
                midCol += colDirection;

                if (midRow == newRow && midCol == newCol) {
                    return true;
                }

                if (pawns[midRow][midCol] != null) {
                    return false;
                }
            } else {
                return false;
            }
        }

        return false;

    }



    private void performMove(Pawn pawn, int newRow, int newCol) {
        int currentRow = pawn.getY();
        int currentCol = pawn.getX();

        pawns[currentRow][currentCol] = null;
        pawns[newRow][newCol] = pawn;

        pawn.setX(newCol);
        pawn.setY(newRow);


        int rowDiff = Math.abs(newRow - currentRow);
        int colDiff = Math.abs(newCol - currentCol);

        if (rowDiff == colDiff) {
            int rowDirection = newRow - currentRow > 0 ? 1 : -1;
            int colDirection = newCol - currentCol > 0 ? 1 : -1;

            for (int i = 1; i < rowDiff; i++) {
                int checkRow = currentRow + (i * rowDirection);
                int checkCol = currentCol + (i * colDirection);

                if (pawns[checkRow][checkCol] != null && pawns[checkRow][checkCol].getColor() != pawn.getColor()) {
                    pawns[checkRow][checkCol] = null;
                }
            }
        }
    }
    private void checkForKing(Pawn piece) {
        if (!piece.isKing()) {
            int row = piece.getY();
            if (row == 0 && piece.getColor() == Color.WHITE) {
                piece.makeKing();
            } else if (row == 8 - 1 && piece.getColor() == Color.BLACK) {
                piece.makeKing();
            }
        }
    }

    private void checkForGameOver() {
        boolean whitePiecesLeft = false;
        boolean blackPiecesLeft = false;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (pawns[row][col] != null) {
                    if (pawns[row][col].getColor() == Color.WHITE) {
                        whitePiecesLeft = true;
                    } else {
                        blackPiecesLeft = true;
                    }
                }
            }
        }

        if (!whitePiecesLeft) {
            gameOver = true;
            winner = Color.BLACK;
        } else if (!blackPiecesLeft) {
            gameOver = true;
            winner = Color.WHITE;
        }

        //add other possibilites to win (no move possible)
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(8 * 50, 8 * 50);
    }
    private void switchPlayer() {
        currentPlayer = (currentPlayer == Color.BLACK ? Color.WHITE: Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                tiles[row][col].draw(g, 50);
                if (pawns[row][col] != null) {
                    pawns[row][col].draw(g, 50);
                }
            }
        }

        if (selectedPawn != null) {
            g.setColor(Color.YELLOW);
            int selectedX = selectedPawn.getX();
            int selectedY = selectedPawn.getY();
            g.drawRect(selectedX * 50, selectedY * 50, 50, 50);

            g.setColor(Color.GREEN);
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (isValidMove(selectedPawn, row, col)) {
                        g.drawRect(col * 50, row * 50, 50, 50);
                    }
                }
            }
        }

        if (gameOver) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            String message = "Game Over! " + (winner == Color.WHITE ? "White" : "Black") + " wins!";
            int messageWidth = g.getFontMetrics().stringWidth(message);
            int x = (getWidth() - messageWidth) / 2;
            int y = getHeight() / 2;
            g.drawString(message, x, y);
        }



    }
}
