package com.checkers;
import java.awt.*;
import java.util.List;
import javax.swing.JPanel;
//https://www.kurnik.pl/warcaby/zasady.phtml
//https://archiwum.warcaby.pl/kodeks-warcabowy/134-rozdzial-i-oficjalne-reguly-gry-w-warcaby
//zasady

public class Board extends JPanel {

    public static final int TILE_SIZE = 50;
    public static final int BOARD_SIZE = 8;

    private boolean COLOR;

    private Game game;


    public Board() {
        super.setBackground(Color.LIGHT_GRAY);



        this.game = new Game(this);

        game.setTiles();
        game.setPawns();

    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(BOARD_SIZE * TILE_SIZE, BOARD_SIZE * TILE_SIZE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);


        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                game.tiles[row][col].draw(g, TILE_SIZE);
                if (game.pawns[row][col] != null) {
                    game.pawns[row][col].draw(g, TILE_SIZE);
                }
            }
        }

        g.setColor(Color.YELLOW);
        if(!game.maxCapturePath.isEmpty()){
            for(Pawn pawn: game.pawnsWithAvaibileCaptures){
            g.drawRect(pawn.getX() * TILE_SIZE, pawn.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
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
                    g.drawRect(coords[0] * TILE_SIZE, coords[1] * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }


        if (game.selectedPawn != null) {
            g.setColor(Color.YELLOW);
            int selectedX = game.selectedPawn.getX();
            int selectedY = game.selectedPawn.getY();
            g.drawRect(selectedX * TILE_SIZE, selectedY * TILE_SIZE, TILE_SIZE, TILE_SIZE);

            g.setColor(Color.GREEN);
            if (game.maxCapturePath.isEmpty()) {
                for (int row = 0; row < BOARD_SIZE; row++) {
                    for (int col = 0; col < BOARD_SIZE; col++) {
                        if(game.selectedPawn.isKing()){
                            if (game.isValidMove(game.selectedPawn, col, row)) {
                                g.drawRect(row * TILE_SIZE, col * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                            }
                        }else
                            if (game.isValidMove(game.selectedPawn, row, col)) {
                                g.drawRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                            }
                    }
                }
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
        if (game.gameDraw) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            String message = "Draw!";
            int messageWidth = g.getFontMetrics().stringWidth(message);
            int x = (getWidth() - messageWidth) / 2;
            int y = getHeight() / 2;
            g.drawString(message, x, y);
        }
    }

}
