package com.checkers;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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

public class Board extends JPanel{


    final static int DOWN_RIGHT = 0;
    final static int DOWN_LEFT = 1;
    final static int UP_RIGHT = 2;
    final static int UP_LEFT = 3;
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

    List<Pawn> capturedPawns;

    //Map<Integer, Integer> movesAfterCapture;
    ArrayList<int[]> movesAfterCapture;

    ArrayList<Node> nodesInCapture;



    List<int[]> maxCapturePath = new ArrayList<>();
    List<int[]> capturePath = new ArrayList<>();


    public Board(MainWindow window, int width, int height){
        //setSize( 400, 400 );
        super.setBackground(Color.LIGHT_GRAY);

        this.width = width;
        this.height = height;
        this.window = window;

        currentPlayer = Color.BLACK;
        gameOver = false;
        capturingMoveAvailable = false;
        capturedPawns = new ArrayList<>();
        //movesAfterCapture = new HashMap<>();
        movesAfterCapture = new ArrayList<int[]>();

        nodesInCapture = new ArrayList<Node>();

        tiles = new Tile[8][8];
        pawns = new Pawn[8][8];
        setTiles();
        setPawns();

        maxCapturePath = new ArrayList<>();
        capturePath = new ArrayList<>();

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



                if (selectedPawn == null) { //pick a pawn
                    if (pawns[row][col] != null && pawns[row][col].getColor() == currentPlayer) {
                        selectedPawn = pawns[row][col];

                        int maxCapture = getMaxCapture(selectedPawn);
                        System.out.println("max capture is: "+maxCapture + "\n");
                        for (int[] coords : maxCapturePath) {
                            int coordsRow = coords[0];
                            int coordsCol = coords[1];
                            System.out.println("Row: " + coordsRow + ", Col: " + coordsCol);
                        }

                        movesAfterCapture.clear();
                        capturedPawns.clear();
                        findCapturingMove(selectedPawn);
                        if(!movesAfterCapture.isEmpty()){
                            // for(Map.Entry<Integer, Integer> cords : movesAfterCapture.entrySet()){
                            //     System.out.println("[ " + cords.getKey() + ", " + cords.getValue() + " ]");
                            // }


                            for (int[] coords : movesAfterCapture) {
                                int coordsRow = coords[0];
                                int coordsCol = coords[1];
                                //System.out.println("Row: " + coordsRow + ", Col: " + coordsCol);
                            }
                        }
                        repaint();

                        System.out.println("\n            nodes in capture            \n");

                        for (Node node : nodesInCapture) {
                            int coordsRow = node.node_X;
                            int coordsCol = node.node_Y;
                            System.out.println("Row: " + coordsRow + ", Col: " + coordsCol);
                        }

                        System.out.println("\n\n");

                    }
                } else{ //perform a move



                    if(!movesAfterCapture.isEmpty()){

                        performMove(selectedPawn, row, col);
                        checkForKing(selectedPawn);


                        for (Pawn pawn : capturedPawns){
                            //System.out.println("PAWNSTOCAPTURE[ " + pawn.getX() + ", " + pawn.getY() + " ]");
                            pawns[pawn.getY()][pawn.getX()] = null;

                        }

                        checkForGameOver();
                        selectedPawn = null;
                        switchPlayer();

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
        // pawns[0][1] = new Pawn(1, 0, Color.BLACK);
        // pawns[0][3] = new Pawn(3, 0, Color.BLACK);
        // pawns[0][5] = new Pawn(5, 0, Color.BLACK);
        // pawns[0][7] = new Pawn(7, 0, Color.BLACK);

        // pawns[1][0] = new Pawn(0, 1, Color.BLACK);
        // pawns[1][2] = new Pawn(2, 1, Color.BLACK);
        // pawns[1][4] = new Pawn(4, 1, Color.BLACK);
        // pawns[1][6] = new Pawn(6, 1, Color.BLACK);

        // pawns[2][1] = new Pawn(1, 2, Color.BLACK);
        // pawns[2][3] = new Pawn(3, 2, Color.BLACK);
        // pawns[2][5] = new Pawn(5, 2, Color.BLACK);
        // pawns[2][7] = new Pawn(7, 2, Color.BLACK);


        // pawns[7][0] = new Pawn(0, 7, Color.WHITE);
        // pawns[7][2] = new Pawn(2, 7, Color.WHITE);
        // pawns[7][4] = new Pawn(4, 7, Color.WHITE);
        // pawns[7][6] = new Pawn(6, 7, Color.WHITE);

        // pawns[6][1] = new Pawn(1, 6, Color.WHITE);
        // pawns[6][3] = new Pawn(3, 6, Color.WHITE);
        // pawns[6][5] = new Pawn(5, 6, Color.WHITE);
        // pawns[6][7] = new Pawn(7, 6, Color.WHITE);

        // pawns[5][0] = new Pawn(0, 5, Color.WHITE);
        // pawns[5][2] = new Pawn(2, 5, Color.WHITE);
        // pawns[5][4] = new Pawn(4, 5, Color.WHITE);
        // pawns[5][6] = new Pawn(6, 5, Color.WHITE);

                        // TESTS //

        // pawns[6][1] = new Pawn(1, 6, Color.WHITE);
        // pawns[4][3] = new Pawn(3, 4, Color.WHITE);
        // pawns[4][1] = new Pawn(1, 4, Color.WHITE);
        // pawns[1][0] = new Pawn(0, 1, Color.BLACK);


        // pawns[0][5] = new Pawn(5, 0, Color.BLACK);
        // pawns[1][4] = new Pawn(4, 1, Color.WHITE);
        // pawns[3][2] = new Pawn(2, 3, Color.WHITE);
        // pawns[5][2] = new Pawn(2, 5, Color.WHITE);
        // pawns[7][4] = new Pawn(4, 7, Color.WHITE);
        // pawns[3][6] = new Pawn(6, 3, Color.WHITE);

        // pawns[0][7] = new Pawn(7, 0, Color.BLACK);
        // pawns[1][6] = new Pawn(6, 1, Color.WHITE);
        // pawns[1][4] = new Pawn(4, 1, Color.WHITE);
        // pawns[1][2] = new Pawn(2, 1, Color.WHITE);
        // pawns[3][2] = new Pawn(2, 3, Color.WHITE);
        // pawns[5][2] = new Pawn(2, 5, Color.WHITE);
        // pawns[5][4] = new Pawn(4, 5, Color.WHITE);

        pawns[0][5] = new Pawn(5, 0, Color.BLACK);
        pawns[1][6] = new Pawn(6, 1, Color.WHITE);
        pawns[3][6] = new Pawn(6, 3, Color.WHITE);
        pawns[5][6] = new Pawn(6, 5, Color.WHITE);
        pawns[5][4] = new Pawn(4, 5, Color.WHITE);
        pawns[5][2] = new Pawn(2, 5, Color.WHITE);
        pawns[3][0] = new Pawn(0, 3, Color.WHITE);



    }

    private boolean isValidMove(Pawn pawn, int new_X, int new_Y) {
        if (new_X < 0 || new_X >= 8 ||
            new_Y < 0 || new_Y >= 8 ||
            pawns[new_X][new_Y] != null) {
                return false;
        }

        if(pawn.getColor() != currentPlayer)
            return false;

        // for(Map.Entry<Integer, Integer> cords : movesAfterCapture.entrySet()){
        //     if(cords.getValue() == new_X && cords.getKey() == new_Y){
        //         return true;
        //     }
        // }
        for (int[] coords : movesAfterCapture) {
            int coordsRow = coords[0];
            int coordsCol = coords[1];
            if(coordsRow == new_X && coordsCol == new_Y){
                return true;
            }
        }


        int current_X = pawn.getY();
        int current_Y = pawn.getX();

        int rowDiff = new_X - current_X;
        int colDiff = new_Y - current_Y;

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

        int rowDirection = rowDiff >= 0 ? 1 : -1;
        int colDirection = colDiff >= 0 ? 1 : -1;

        if(colDiff < 0 || rowDiff < 0){
            //System.out.println("prprprp valid");
            //return false;
        }

        int midRow = current_X + rowDirection;
        int midCol = current_Y + colDirection;

        while (midRow != new_X && midCol != new_Y) {

            if(midRow > 7 || midCol > 7)
                break;

            if (pawns[midRow][midCol] != null &&
                pawns[midRow][midCol].getColor() != pawn.getColor()) {

                midRow += rowDirection;
                midCol += colDirection;

                if (midRow == new_X && midCol == new_Y) {
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

    private void performMove(Pawn pawn, int new_X, int new_Y) {
        int current_X = pawn.getY();
        int current_Y = pawn.getX();

        pawns[current_X][current_Y] = null;
        pawns[new_X][new_Y] = pawn;

        pawn.setX(new_Y);
        pawn.setY(new_X);


        int rowDiff = Math.abs(new_X - current_X);
        int colDiff = Math.abs(new_Y - current_Y);

        if (rowDiff == colDiff) {
            int rowDirection = new_X - current_X > 0 ? 1 : -1;
            int colDirection = new_Y - current_Y > 0 ? 1 : -1;

            for (int i = 1; i < rowDiff; i++) {
                int checkRow = current_X + (i * rowDirection);
                int checkCol = current_Y + (i * colDirection);

                if (pawns[checkRow][checkCol] != null && pawns[checkRow][checkCol].getColor() != pawn.getColor()) {
                    pawns[checkRow][checkCol] = null;
                }
            }
        }
    }


    private Integer searchCapturingMove(Pawn pawn, int current_X, int current_Y) {

        Node new_node = new Node(current_X, current_Y);

        //down-right
        if (isValidCapture(pawn, current_X, current_Y, current_X + 2, current_Y + 2, current_X + 1, current_Y + 1)) {


            new_node.setDirection(current_X + 2, current_Y + 2, DOWN_RIGHT);
            nodesInCapture.add(new_node);

            capturedPawns.add(pawns[current_Y + 1][current_X + 1]);
            int[] temp = {current_X + 2, current_Y + 2};
            movesAfterCapture.add(temp);
            //System.out.println("right3 capturedPawn added[ " + pawns[current_Y + 1][current_X + 1].getX() + " " + pawns[current_Y + 1][current_X + 1].getY() + " ]");

            return searchCapturingMove(pawn, current_X += 2, current_Y +=2);

        }

        //down-left
        if (isValidCapture(pawn,current_X, current_Y, current_X - 2, current_Y + 2, current_X - 1, current_Y + 1)) {

            new_node.setDirection(current_X - 2, current_Y + 2, DOWN_LEFT);
            nodesInCapture.add(new_node);

            capturedPawns.add(pawns[current_Y + 1][current_X - 1]);
            int[] temp = {current_X - 2, current_Y + 2};
            movesAfterCapture.add(temp);

            return searchCapturingMove(pawn, current_X -= 2, current_Y +=2);

        }
        //up-right
        if (isValidCapture(pawn,current_X, current_Y, current_X + 2, current_Y - 2, current_X + 1, current_Y - 1)) {

            new_node.setDirection(current_X + 2, current_Y - 2, UP_RIGHT);
            nodesInCapture.add(new_node);

            capturedPawns.add(pawns[current_Y - 1][current_X + 1]);
            int[] temp = {current_X + 2, current_Y - 2};
            movesAfterCapture.add(temp);

            return searchCapturingMove(pawn, current_X += 2, current_Y -=2);

        }

        //up-left
        if (isValidCapture(pawn,current_X, current_Y, current_X - 2, current_Y - 2, current_X - 1, current_Y - 1)) {

            new_node.setDirection(current_X - 2, current_Y - 2, UP_RIGHT);
            nodesInCapture.add(new_node);

            capturedPawns.add(pawns[current_Y - 1][current_X - 1]);
            int[] temp = {current_X - 2, current_Y - 2};
            movesAfterCapture.add(temp);

            return searchCapturingMove(pawn, current_X -= 2, current_Y -=2);
        }


        //334, 404


        if(capturedPawns.isEmpty())
            return 0;


        nodesInCapture.add(new_node);
        return 1;

    }


    private void findCapturingMove(Pawn pawn) {
        int current_X = pawn.getX();
        int current_Y = pawn.getY();
        //List<Pawn> capturedPawns = new ArrayList<>();
        searchCapturingMove(pawn, current_X, current_Y);

        //return capturedPawns;
    }


    public int getMaxCapture(Pawn currentPawn) {
        int maxCapture = 0;


        int currentCapture = getMaxCaptureForPiece(currentPawn, currentPawn.getX(), currentPawn.getY(), capturePath);
        maxCapture = currentCapture;
        maxCapturePath = capturePath;
        // for (int row = 0; row < 8; row++) {
        //     for (int col = 0; col < 8; col++) {
        //         if (pawns[row][col] == currentPawn) {
        //             List<int[]> capturePath = new ArrayList<>();
        //             int currentCapture = getMaxCaptureForPiece(currentPawn, row, col, capturePath);

        //             if (currentCapture > maxCapture) {
        //                 maxCapture = currentCapture;
        //                 maxCapturePath = capturePath;
        //             }
        //         }
        //     }
        // }

        // Print the coordinates of the path
        for (int[] coords : maxCapturePath) {
            int row = coords[0];
            int col = coords[1];
            //System.out.println("Row: " + row + ", Col: " + col);
        }

        return maxCapture;
    }

    private int getMaxCaptureForPiece(Pawn currentPawn, int row, int col, List<int[]> capturePath) {
        int maxCapture = 0;

        // Check all possible capture directions
        for (int directionRow = -1; directionRow <= 1; directionRow += 2) {
            for (int directionCol = -1; directionCol <= 1; directionCol += 2) {

                int capturedRow = row + directionRow;
                int capturedCol = col + directionCol;
                int targetRow = row + 2 * directionRow;
                int targetCol = col + 2 * directionCol;

                // Check if the capture is valid
                if (isValidCapture(currentPawn,row,col,targetRow,targetCol,capturedRow,capturedCol)) {
                    // Perform the capture
                    Pawn capturedPiece = new Pawn(capturedRow,capturedCol, pawns[capturedCol][capturedRow].getColor()); //= new Pawn...
                    if(pawns[capturedCol][capturedRow].isKing())
                        capturedPiece.makeKing();
                    pawns[capturedCol][capturedRow] = null;
                    pawns[targetCol][targetRow] = currentPawn;

                    Pawn localPawn = new Pawn(targetRow, targetCol, currentPawn.getColor());

                    // Recursively check for additional captures
                    List<int[]> subCapturePath = new ArrayList<>();
                    int currentCapture = 1 + getMaxCaptureForPiece(localPawn, targetRow, targetCol, subCapturePath);

                    // Update the maximum capture and capture path
                    if (currentCapture > maxCapture) {
                        maxCapture = currentCapture;
                        capturePath.clear();
                        capturePath.add(new int[]{row, col});
                        capturePath.addAll(subCapturePath);
                    }

                    // Undo the capture
                    pawns[targetCol][targetRow] = null;
                    pawns[capturedCol][capturedRow] = capturedPiece;
                    if(capturedPiece.isKing())
                        pawns[capturedCol][capturedRow].makeKing();
                }
            }
        }

        return maxCapture;
    }


    private boolean isValidCapture(Pawn piece, int current_X, int current_Y, int new_X, int new_Y, int captured_X, int captured_Y) {
        if (new_X < 0 || new_X >= 8 || new_Y < 0 || new_Y >= 8 ||
                captured_X < 0 || captured_X >= 8 || captured_Y < 0 || captured_Y >= 8 ||
                pawns[new_Y][new_X] != null || pawns[captured_Y][captured_X] == null ||
                pawns[captured_Y][captured_X].getColor() == piece.getColor()) {
            return false;
        }

        int X_Diff = new_X - current_X;
        int Y_Diff = new_Y - current_Y;

        for (Pawn pawn : capturedPawns) {
            int captureX = pawn.getX();
            int captureY = pawn.getY();
            if(captureX == captured_X && captureY == captured_Y){
                return false;
            }

        }

        // if (!piece.isKing()) {
        //     if (piece.getColor() == Color.BLACK && X_Diff < 0) {
        //         return false;
        //     }
        //     if (piece.getColor() == Color.WHITE && Y_Diff > 0) {
        //         return false;
        //     }
        // }

        // if((Math.abs(X_Diff) == Math.abs(Y_Diff)) && (Math.abs(X_Diff - Y_Diff) == Y_Diff*2)){
        //     movesAfterCapture.put(new_X, new_X);
        //     System.out.println("lol" + current_X + ", " +  current_Y);
        // }

        return Math.abs(X_Diff) == Math.abs(Y_Diff);
        //Math.abs(X_Diff) == 2 && Math.abs(Y_Diff) == 2; // Math.abs(X_Diff) == Math.abs(Y_Diff);//Math.abs(rowDiff) == 2 && Math.abs(colDiff) == 2;
    }


    private void checkForKing(Pawn pawn) {
        if (!pawn.isKing()) {
            int row = pawn.getY();
            if (row == 0 && pawn.getColor() == Color.WHITE) {
                pawn.makeKing();
            } else if (row == 8 - 1 && pawn.getColor() == Color.BLACK) {
                pawn.makeKing();
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

    private void switchPlayer() {
        currentPlayer = (currentPlayer == Color.BLACK ? Color.WHITE: Color.BLACK);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(8 * 50, 8 * 50);
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
            if(movesAfterCapture.isEmpty()){
                for (int row = 0; row < 8; row++) {
                    for (int col = 0; col < 8; col++) {
                        if (isValidMove(selectedPawn, row, col)) {
                            g.drawRect(col * 50, row * 50, 50, 50);
                        }
                    }
                }
            } else{
                g.setColor(Color.RED);
                int captureX;
                int captureY;
                // for(Map.Entry<Integer, Integer> cords : movesAfterCapture.entrySet()){
                //     captureY = cords.getValue();
                //     captureX = cords.getKey();
                //     System.out.println("draw[ " + captureX + ", " + captureY + " ]");
                //     g.drawRect(captureX*50, captureY*50, 50, 50);
                // }
                for (int[] coords : movesAfterCapture) {
                    captureX = coords[0];
                    captureY = coords[1];
                    g.drawRect(captureX*50, captureY*50, 50, 50);
                }


                g.setColor(Color.GREEN);
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

