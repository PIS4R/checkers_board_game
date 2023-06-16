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

public class Board extends JPanel {

    final static int DOWN_RIGHT = 0;
    final static int DOWN_LEFT = 1;
    final static int UP_RIGHT = 2;
    final static int UP_LEFT = 3;
    // private Game game;
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
    List<Pawn> maxCapturedPawns;

    // Map<Integer, Integer> movesAfterCapture;
    ArrayList<int[]> movesAfterCapture;

    ArrayList<Node> maxNodesInCapture;

    static List<int[]> maxCapturePath = new ArrayList<>();
    List<int[]> capturePath = new ArrayList<>();



    List<List<int[]>> maxMaxCapturePath;
    List<List<Pawn>> maxMaxCapturedPawns;

    List<Pawn> pawnsWithAvaibileCaptures = new ArrayList<>();


    public Board(MainWindow window, int width, int height) {
        // setSize( 400, 400 );
        super.setBackground(Color.LIGHT_GRAY);

        this.width = width;
        this.height = height;
        this.window = window;

        currentPlayer = Color.BLACK;
        gameOver = false;
        capturingMoveAvailable = false;
        capturedPawns = new ArrayList<>();
        maxCapturedPawns = new ArrayList<>();
        // movesAfterCapture = new HashMap<>();
        movesAfterCapture = new ArrayList<int[]>();

        maxNodesInCapture = new ArrayList<Node>();

        tiles = new Tile[8][8];
        pawns = new Pawn[8][8];
        setTiles();
        setPawns();

        //maxCapturePath = new ArrayList<>();
        //capturePath = new ArrayList<>();
        maxMaxCapturePath = new ArrayList<>();
        maxMaxCapturedPawns = new ArrayList<>();
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

                //todo naprawic bugi zwiazene z odkilkaniem
                if (selectedPawn == null) { // pick a pawn
                    if (pawns[row][col] != null && pawns[row][col].getColor() == currentPlayer) {
                        if(!maxCapturePath.isEmpty()){
                            for(Pawn pawn: pawnsWithAvaibileCaptures){
                                if(pawns[row][col] == pawn){
                                    selectedPawn = pawns[row][col];
                                    selectedPawn.setColor(currentPlayer);
                                }
                            }
                        } else{
                            selectedPawn = pawns[row][col];
                        }
                        if(selectedPawn.isKing()){
                            List<int[]> kingCapturePath = new ArrayList<>();
                            getMaxKingCapture(selectedPawn);
                            System.out.println("szmato ");

                        }

                        repaint();

                    }
                } else { // perform a move

                    //if(maxMaxCapturePath.get(maxMaxCapturePath.size() -1).indexOf(coords) ==

                    if (!maxMaxCapturePath.isEmpty()) { //capture



                        for(List<int[]> list : maxMaxCapturePath){
                            if(list.size() != 1){
                                if(row == list.get(list.size() - 1)[1] &&
                                col == list.get(list.size() - 1)[0]){
                                    performMove(selectedPawn, row, col);
                                    checkForKing(selectedPawn);
                                    for(List<Pawn> listOfPawns : maxMaxCapturedPawns){
                                        for (Pawn pawn : listOfPawns){
                                            pawns[pawn.getY()][pawn.getX()] = null;
                                        }
                                    }
                                    checkForGameOver();
                                    selectedPawn = null;
                                    //switchPlayer();
                                }
                            }
                            else{
                                if(row == list.get(0)[1] &&
                                col == list.get(0)[0]){
                                    performMove(selectedPawn, row, col);
                                    checkForKing(selectedPawn);
                                    for(List<Pawn> listOfPawns : maxMaxCapturedPawns){
                                        for (Pawn pawn : listOfPawns){
                                            pawns[pawn.getY()][pawn.getX()] = null;
                                        }
                                    }
                                    checkForGameOver();
                                    selectedPawn = null;
                                    //switchPlayer();
                                }
                            }
                        }

                    } else { //normal move
                        if (isValidMove(selectedPawn, row, col)) {
                            performMove(selectedPawn, row, col);
                            checkForKing(selectedPawn);
                            checkForGameOver();
                            selectedPawn = null;
                            //checkForAvaibileCaptures(currentPlayer);
                            repaint();

                        } else {
                            selectedPawn = pawns[row][col];
                        }
                    }

                    switchPlayer();
                    repaint();
                }
            }
        });
    }

    // public Board(MainWindow window) { //Game game, Player player1, Player player2

    // }

    private void setTiles() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Color color = (row + col) % 2 == 0 ? Color.WHITE : Color.GRAY;
                tiles[row][col] = new Tile(col, row, color);
            }
        }
    }

    private void setPawns() {
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

        //              TESTS //

        // pawns[6][1] = new Pawn(1, 6, Color.WHITE);
        // pawns[4][3] = new Pawn(3, 4, Color.WHITE);
        // pawns[4][1] = new Pawn(1, 4, Color.WHITE);
        // pawns[1][0] = new Pawn(0, 1, Color.BLACK);

        // pawns[0][5] = new Pawn(5, 0, Color.BLACK);
        // pawns[1][4] = new Pawn(4, 1, Color.WHITE);
        // pawns[3][2] = new Pawn(2, 3, Color.WHITE);
        // pawns[5][2] = new Pawn(2, 5, Color.WHITE);
        // pawns[5][4] = new Pawn(4, 5, Color.WHITE);
        // pawns[3][4] = new Pawn(4, 3, Color.WHITE);
        // pawns[1][2] = new Pawn(2, 1, Color.WHITE);
        // pawns[7][4] = new Pawn(4, 7, Color.WHITE);
        // pawns[3][6] = new Pawn(6, 3, Color.WHITE);

        // pawns[0][7] = new Pawn(7, 0, Color.BLACK);
        // pawns[1][6] = new Pawn(6, 1, Color.WHITE);
        // pawns[1][4] = new Pawn(4, 1, Color.WHITE);
        // pawns[1][2] = new Pawn(2, 1, Color.WHITE);
        // pawns[3][2] = new Pawn(2, 3, Color.WHITE);
        // pawns[5][2] = new Pawn(2, 5, Color.WHITE);
        // pawns[5][4] = new Pawn(4, 5, Color.WHITE);
        // pawns[5][6] = new Pawn(6, 5, Color.WHITE);
        // pawns[3][4] = new Pawn(4, 3, Color.WHITE);

        // pawns[0][5] = new Pawn(5, 0, Color.BLACK);
        // pawns[1][6] = new Pawn(6, 1, Color.WHITE);
        // pawns[3][6] = new Pawn(6, 3, Color.WHITE);
        // pawns[5][6] = new Pawn(6, 5, Color.WHITE);
        // pawns[5][4] = new Pawn(4, 5, Color.WHITE);
        // pawns[5][2] = new Pawn(2, 5, Color.WHITE);
        // pawns[3][2] = new Pawn(2, 3, Color.WHITE);
        // pawns[3][0] = new Pawn(0, 3, Color.WHITE);

        // pawns[2][3] = new Pawn(3, 2, Color.BLACK);
        // pawns[4][5] = new Pawn(5, 4, Color.WHITE);


        // pawns[6][1] = new Pawn(1, 6, Color.BLACK);
        // pawns[5][4] = new Pawn(4, 5, Color.WHITE);
        // pawns[4][5] = new Pawn(5, 4, Color.WHITE);
        // pawns[3][2] = new Pawn(2, 3, Color.WHITE);
        // pawns[1][6] = new Pawn(6, 1, Color.WHITE);

        Pawn temp = new Pawn(6, 7, Color.BLACK);
        temp.makeKing();
        pawns[7][6] = temp;
        pawns[5][4] = new Pawn(4, 5, Color.WHITE);
        pawns[3][2] = new Pawn(2, 3, Color.WHITE);
        pawns[1][2] = new Pawn(2, 1, Color.WHITE);
        pawns[6][1] = new Pawn(1, 6, Color.WHITE);
        pawns[1][4] = new Pawn(4, 1, Color.WHITE);
        pawns[4][5] = new Pawn(5, 4, Color.WHITE);
        pawns[6][3] = new Pawn(3, 6, Color.WHITE);




    }



    private boolean isValidMove(Pawn pawn, int new_X, int new_Y) {
        if (new_X < 0 || new_X >= 8 ||
                new_Y < 0 || new_Y >= 8 ||
                pawns[new_X][new_Y] != null) {
            return false;
        }
        if (pawn.getColor() != currentPlayer)
            return false;

        for (int[] coords : movesAfterCapture) {
            int coordsRow = coords[0];
            int coordsCol = coords[1];
            if (coordsRow == new_X && coordsCol == new_Y) {
                return true;
            }
        }

        int current_X = pawn.getY();
        int current_Y = pawn.getX();

        int rowDiff = new_X - current_X;
        int colDiff = new_Y - current_Y;


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
        }else{
            return Math.abs(rowDiff) == Math.abs(colDiff);
        }

        if (Math.abs(rowDiff) == 1 && Math.abs(colDiff) == 1) {
            return true;
        }

        int rowDirection = rowDiff >= 0 ? 1 : -1;
        int colDirection = colDiff >= 0 ? 1 : -1;

        int midRow = current_X + rowDirection;
        int midCol = current_Y + colDirection;

        while (midRow != new_X && midCol != new_Y) {

            if (midRow > 7 || midCol > 7)
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

    public int getMaxKingCapture(Pawn king) {
        int maxCapture = 0;
        List<int[]> maxKingCapturePath = new ArrayList<>();


        if (king.getColor() == currentPlayer && king.isKing()) {
            List<int[]> kingCapturePath = new ArrayList<>();
            int kingX = king.getX();
            int kingY = king.getY();
            //pawns[kingY][kingX] = null;
            //System.out.println("king max capture: " + maxCapture);
            //int currentCapture = 1;
            int currentCapture = getMaxCaptureForKing(king, king.getX(), king.getY(), maxCapture, capturePath);
            //pawns[kingY][kingX] = king;
            System.out.println("king max capture: " + (currentCapture));

            if (currentCapture > maxCapture) {
                maxCapture = currentCapture;
                maxKingCapturePath = capturePath;
            }
        }

        // Print the coordinates of the path
        for (int[] coords : maxKingCapturePath) {
            int row = coords[0];
            int col = coords[1];
        }

        return maxCapture;
    }
    private boolean kingisLanding = false;
    private boolean endFlag = false;

    List<Pawn> replacedPawns = new ArrayList<>();
    List<Pawn> subKings = new ArrayList<>();
    List<Pawn> kingCapturePath = new ArrayList<>();

    private int getMaxCaptureForKing(Pawn king, int row, int col, int maxCapture, List<int[]> capturePath) {
        //todo usunac podstawowy pionek z planszy
        //todo usuwac i nadawac krola pionkom
        // Check all possible capture directions
        for (int directionRow = -1; directionRow <= 1; directionRow += 2) {
            for (int directionCol = -1; directionCol <= 1; directionCol += 2) {

                int currentRow = row+directionRow;
                int currentCol = col+directionCol;
                //znalezienie bicia dla danej przekątnej
                while(currentRow>=0 && currentRow<=7 && currentCol>=0 && currentCol<=7){
                    //jezeli pionek znaleziony za ktorym choc jedno wolne pole

                    if(pawns[currentCol][currentRow] != null && currentCol+directionCol <= 7 && currentCol+directionCol>=0 && currentRow+directionRow<=7 && currentRow+directionRow>=0&& pawns[currentCol+directionCol][currentRow+directionRow] == null){
                        //lądowanie za
                        int capturedRow = currentRow;
                        int capturedCol = currentCol;
                        int targetRow = currentRow+directionRow;
                        int targetCol = currentCol+directionCol;

                        while(targetRow>=0 && targetRow<=7 && targetCol>=0 && targetCol<=7){
                            if(isValidCaptureForKing(king,directionRow,directionCol,row, col, targetRow, targetCol, capturedRow, capturedCol,null)){
                                System.out.println("King path Row: " + capturedRow + ", Col: " + capturedCol);

                                Pawn subKing = new Pawn(targetRow, targetCol, currentPlayer);
                                subKing.makeKing();
                                if(subKing != null)
                                    subKings.add(subKing);
                                Pawn capturedPawn = new Pawn(capturedRow,capturedCol, pawns[capturedCol][capturedRow].getColor());
                                if(pawns[targetCol][targetRow] != null)
                                    replacedPawns.add(pawns[targetCol][targetRow]);
                                pawns[capturedCol][capturedRow] = null;

                                pawns[targetCol][targetRow] = subKing;
                                int currentCapture = 1;
                                currentCapture += getMaxCaptureForKing(subKing, targetRow, targetCol, maxCapture, null);
                                if(currentCapture > maxCapture){
                                    maxCapture = currentCapture;
                                }

                                pawns[targetCol][targetRow] = null;
                                if(!replacedPawns.isEmpty()){
                                    for(Pawn replacedPawn : replacedPawns)
                                        pawns[replacedPawn.getY()][replacedPawn.getX()] = replacedPawn;
                                }
                                pawns[capturedCol][capturedRow] = capturedPawn;
                                if(endFlag){
                                    break;
                                }
                            }
                                                            targetRow += directionRow;
                                targetCol += directionCol;
                                if(endFlag){
                                    break;
                                }
                            //sprawdzanie wszystkich pol na przekatnej osobna rekurencyjna func ?
                        }
                        if(endFlag){
                            break;
                        }
                    }
                    currentRow += directionRow;
                    currentCol += directionCol;

                    if(endFlag){
                        break;
                    }
                }
                if(endFlag){
                    break;
                }
            }
            if(endFlag){
                break;
            }
        }

        endFlag = true;
        return maxCapture;
    }
    private boolean isValidCaptureForKing(Pawn piece,int directionRow,int directionCol, int current_X, int current_Y, int new_X, int new_Y, int captured_X, int captured_Y, List<Pawn> capturedPawns) {
        if (new_X < 0 || new_X >= 8 || new_Y < 0 || new_Y >= 8 ||
                captured_X < 0 || captured_X >= 8 || captured_Y < 0 || captured_Y >= 8 ||
                pawns[new_Y][new_X] != null || pawns[captured_Y][captured_X] == null ||
                pawns[captured_Y][captured_X].getColor() == piece.getColor()) {
            return false;
        }
        //todo sprawdzać czy z obecnej pozycji da sie zbic capture i wyladowac na target
        //debug error capture 3,4 na target 0,5 mimo 'bycia' w sub 1,4
        //bo potem tworzy subKing w target 0,5 i normalnie zbija w tyl...
        //int difX = captured_X - current_X; //2-1        1
        //int difY = captured_Y - current_Y; //3-4       -1
        // int temp_capture_X = captured_X;
        // int temp_capture_Y = captured_Y;
        // while(true){
        //     if(temp_capture_X < 0 && temp_capture_X >7 && temp_capture_Y <0 && temp_capture_Y >7){
        //         return false;
        //     }
        //     if(temp_capture_X == new_X && temp_capture_Y == new_Y){
        //         break;
        //     }

        //     temp_capture_X += directionRow;
        //     temp_capture_Y += directionCol;
        // }

        // for(Pawn subKing : subKings){
        //     if(subKing.getY()== current_Y&& subKing.getX()==current_X){ //piece.getY() piece.getX()
        //         return false;
        //     }
        // }
        //todo iterowanie po tablicy zbitych i sprawdzanie czy spowrotem nie zbijamy
        int X_Diff = new_X - current_X;
        int Y_Diff = new_Y - current_Y;

        return Math.abs(X_Diff) == Math.abs(Y_Diff);
        //return true;
    }


    public void checkForAvaibileCaptures(Color currentPlayer){

        maxCapturePath.clear();
        maxMaxCapturePath.removeAll(maxMaxCapturePath);
        maxMaxCapturedPawns.removeAll(maxMaxCapturedPawns);
        pawnsWithAvaibileCaptures.removeAll(pawnsWithAvaibileCaptures);
        int maxCapture = 0;

        for(int j = 0; j < 8; j++){
            for(int i = 0; i < 8; i++){
                if(pawns[j][i] != null && pawns[j][i].getColor() == currentPlayer){
                    int subMaxCapture = getMaxCapture(pawns[j][i]);

                    if(subMaxCapture > maxCapture){
                        pawnsWithAvaibileCaptures.removeAll(pawnsWithAvaibileCaptures);
                        maxMaxCapturePath.removeAll(maxMaxCapturePath);
                        maxMaxCapturedPawns.removeAll(maxMaxCapturedPawns);

                        pawnsWithAvaibileCaptures.add(pawns[j][i]);
                        maxCapture = subMaxCapture;

                        List<int[]> temp = new ArrayList<>();
                        for(int[] coords : maxCapturePath){
                            temp.add(coords);
                        }

                        maxMaxCapturePath.add(temp);
                        maxMaxCapturedPawns.add(maxCapturedPawns);
                    }
                    else if(subMaxCapture == maxCapture && maxCapture != 0){
                        pawnsWithAvaibileCaptures.add(pawns[j][i]);
                        List<int[]> temp = new ArrayList<>();
                        for(int[] coords : maxCapturePath){
                            temp.add(coords);
                        }
                        maxMaxCapturePath.add(temp);
                        maxMaxCapturedPawns.add(maxCapturedPawns);
                    }
                }
            }
        }
        System.out.println("max capture is: " + maxMaxCapturePath + "\n");
        for (int[] coords : maxCapturePath) {
            int coordsRow = coords[0];
            int coordsCol = coords[1];
            System.out.println("Row: " + coordsRow + ", Col: " + coordsCol);
        }

    }

    public int getMaxCapture(Pawn currentPawn) {
        int maxCapture = 0;

        int currentCapture = getMaxCaptureForPiece(currentPawn, currentPawn.getX(), currentPawn.getY(), capturePath,
                capturedPawns);
        maxCapture = currentCapture;
        maxCapturePath = capturePath;
        maxCapturedPawns = capturedPawns;

        // for (Pawn pawn : maxCapturedPawns) {
        //     System.out.println("Captured Row: " + pawn.getX() + ", Col: " + pawn.getY());
        // }

        return maxCapture;
    }

    private boolean isValidCapture(Pawn piece, int current_X, int current_Y, int new_X, int new_Y, int captured_X,
            int captured_Y, List<Pawn> capturedPawns) {
        if (new_X < 0 || new_X >= 8 || new_Y < 0 || new_Y >= 8 ||
                captured_X < 0 || captured_X >= 8 || captured_Y < 0 || captured_Y >= 8 ||
                pawns[new_Y][new_X] != null || pawns[captured_Y][captured_X] == null ||
                pawns[captured_Y][captured_X].getColor() == piece.getColor()) {
            return false;
        }

        int X_Diff = new_X - current_X;
        int Y_Diff = new_Y - current_Y;

        return Math.abs(X_Diff) == Math.abs(Y_Diff);
    }

    private int getMaxCaptureForPiece(Pawn currentPawn, int row, int col, List<int[]> capturePath,
            List<Pawn> capturedPawns) {
        int maxCapture = 0;


        // Check all possible capture directions
        for (int directionRow = -1; directionRow <= 1; directionRow += 2) {
            for (int directionCol = -1; directionCol <= 1; directionCol += 2) {

                int capturedRow = row + directionRow;
                int capturedCol = col + directionCol;
                int targetRow = row + 2 * directionRow;
                int targetCol = col + 2 * directionCol;

                // Check if the capture is valid
                if (isValidCapture(currentPawn, row, col, targetRow, targetCol, capturedRow, capturedCol,
                        capturedPawns)) {
                    // Perform the capture
                    Pawn capturedPiece = new Pawn(capturedRow, capturedCol, pawns[capturedCol][capturedRow].getColor());

                    if (pawns[capturedCol][capturedRow].isKing())
                        capturedPiece.makeKing();
                    pawns[capturedCol][capturedRow] = null;
                    pawns[targetCol][targetRow] = currentPawn;
                    capturedPawns.add(capturedPiece); // dodajemy

                    List<Pawn> subCapturedPawns = new ArrayList<>();

                    Pawn localPawn = new Pawn(targetRow, targetCol, currentPawn.getColor());

                    // Recursively check for additional captures
                    List<int[]> subCapturePath = new ArrayList<>();

                    int currentCapture = 1 + getMaxCaptureForPiece(localPawn, targetRow, targetCol, subCapturePath, subCapturedPawns);
                    //System.out.println("subCapturePath: " + subCapturePath);

                    // Update the maximum capture and capture path
                    if (currentCapture > maxCapture) {
                        maxCapture = currentCapture;
                        capturePath.clear();
                        capturePath.add(new int[] { targetRow, targetCol });
                        capturePath.addAll(subCapturePath);
                        maxCapturedPawns = capturedPawns;
                        capturedPawns.clear();
                        capturedPawns.add(new Pawn(capturedRow, capturedCol,localPawn.getEnemyColor(currentPlayer)));
                        capturedPawns.addAll(subCapturedPawns); // dodajemy do listy wlasciwej
                    }

                    // Undo the capture
                    pawns[targetCol][targetRow] = null;
                    pawns[capturedCol][capturedRow] = capturedPiece;
                    capturedPawns.remove(capturedPiece); // usuwamy
                    if (capturedPiece.isKing())
                        pawns[capturedCol][capturedRow].makeKing();
                }
            }
        }

        return maxCapture;
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

        // add other possibilites to win (no move possible)
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == Color.BLACK ? Color.WHITE : Color.BLACK);
        checkForAvaibileCaptures(currentPlayer);

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

        g.setColor(Color.YELLOW);
        if(!maxCapturePath.isEmpty()){
            for(Pawn pawn: pawnsWithAvaibileCaptures){
            g.drawRect(pawn.getX() * 50, pawn.getY() * 50, 50, 50);
            }
        }
        if(!maxMaxCapturePath.isEmpty()){
            g.setColor(Color.RED);
            int captureX;
            int captureY;

            for(List<int[]> list : maxMaxCapturePath){
                for (int[] coords : list) { // draw only maxCapture path
                    System.out.println("list: "+coords[0]+", "+coords[1]);

                    g.drawRect(coords[0] * 50, coords[1] * 50, 50, 50);
                }
            }
        }


        if (selectedPawn != null) {
            g.setColor(Color.YELLOW);
            int selectedX = selectedPawn.getX();
            int selectedY = selectedPawn.getY();
            g.drawRect(selectedX * 50, selectedY * 50, 50, 50);

            g.setColor(Color.GREEN);
            if (maxCapturePath.isEmpty()) {
                for (int row = 0; row < 8; row++) {
                    for (int col = 0; col < 8; col++) {
                        if (isValidMove(selectedPawn, row, col)) {
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

        if (gameOver) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            String message = "Game Over! " + (winner == Color.WHITE ? "White" : "Black") + " wins!";
            int messageWidth = g.getFontMetrics().stringWidth(message);
            int x = (getWidth() - messageWidth) / 2;
            int y = getHeight() / 2;
            g.drawString(message, x, y);
        }

    }

}
