package com.checkers;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
public class Game {
    final static int DOWN_RIGHT = 0;
    final static int DOWN_LEFT = 1;
    final static int UP_RIGHT = 2;
    final static int UP_LEFT = 3;
    // private Game game;
    private Board board;
    public Tile[][] tiles;
    public Pawn[][] pawns;
    public transient Pawn selectedPawn;
    private Color currentPlayer;
    public boolean gameOver;
    public boolean gameDraw;
    public Color winner;
    //private boolean capturingMoveAvailable;
    List<Pawn> capturedPawns;
    List<Pawn> maxCapturedPawns;
    //ArrayList<int[]> movesAfterCapture;
    static List<int[]> maxCapturePath = new ArrayList<>();
    List<int[]> capturePath = new ArrayList<>();
    List<List<int[]>> maxMaxCapturePath;
    List<List<Pawn>> maxMaxCapturedPawns;
    List<Pawn> pawnsWithAvaibileCaptures = new ArrayList<>();
    int movesWithKings;


    public Game(Board board) {
        // setSize( 400, 400 );
        //super.setBackground(Color.LIGHT_GRAY);
        this.board = board;
        currentPlayer = Color.BLACK;
        gameOver = false;
        gameDraw = false;
        movesWithKings = 0;
        //capturingMoveAvailable = false;
        capturedPawns = new ArrayList<>();
        maxCapturedPawns = new ArrayList<>();
        // movesAfterCapture = new HashMap<>();
        //movesAfterCapture = new ArrayList<int[]>();
        tiles = new Tile[8][8];
        pawns = new Pawn[8][8];
        setTiles();
        setPawns();
        //maxCapturePath = new ArrayList<>();
        //capturePath = new ArrayList<>();
        maxMaxCapturePath = new ArrayList<>();
        maxMaxCapturedPawns = new ArrayList<>();

        checkForAvaibileCaptures(currentPlayer);
        board.repaint();
        board.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

                if (gameOver) {
                    return;
                }
                int mouseX = e.getX();
                int mouseY = e.getY();

                int row = mouseY / 50;
                int col = mouseX / 50;

                if (selectedPawn == null ) { // pick a pawn
                    if (pawns[row][col] != null && pawns[row][col].getColor() == currentPlayer) {
                        if(!maxMaxCapturePath.isEmpty()){
                            for(Pawn pawn: pawnsWithAvaibileCaptures){
                                if(col==pawn.getX()&&row==pawn.getY()){
                                    selectedPawn = pawns[row][col];
                                    selectedPawn.setColor(currentPlayer);

                                }
                            }
                        } else{
                            selectedPawn = pawns[row][col];
                        }
                        board.repaint();

                    }
                } else { // perform a move
                    if (!maxMaxCapturePath.isEmpty()) { //capture
                        for(List<int[]> list : maxMaxCapturePath){
                            if(list.size() != 1){
                                if(row == list.get(list.size() - 1)[1] &&
                                col == list.get(list.size() - 1)[0]){
                                    performMove(selectedPawn, row, col);
                                    for(Pawn pawn : maxMaxCapturedPawns.get(maxMaxCapturePath.indexOf(list))){ //maxMaxCapturedPawns
                                        pawns[pawn.getY()][pawn.getX()] = null;
                                    }
                                    checkForGameOver();
                                    checkForKing(pawns[row][col]);
                                    movesWithKings=0;
                                    selectedPawn = null;
                                    break;
                                }
                            }
                            else{
                                if(row == list.get(0)[1] &&
                                col == list.get(0)[0]){
                                    performMove(selectedPawn, row, col);
                                    for(Pawn pawn : maxMaxCapturedPawns.get(maxMaxCapturePath.indexOf(list))){ //maxMaxCapturedPawns
                                        pawns[pawn.getY()][pawn.getX()] = null;
                                    }
                                    checkForGameOver();
                                    checkForKing(pawns[row][col]);
                                    movesWithKings=0;
                                    selectedPawn = null;
                                    break;
                                }
                            }
                        }
                        maxMaxCapturePath.clear();
                    } else { //normal move
                        if (isValidMove(selectedPawn, row, col)) {
                            performMove(selectedPawn, row, col);
                            if(selectedPawn.isKing())
                                movesWithKings++;
                            else
                                movesWithKings=0;
                            checkForGameOver();
                            selectedPawn = null;
                            board.repaint();
                        } else {
                            selectedPawn = pawns[row][col];
                        }
                    }
                    if(pawns[row][col]!=null)
                        checkForKing(pawns[row][col]);
                    board.repaint();
                    checkForGameOver();
                    checkForDraw();
                    switchPlayer();
                    checkforBlock();
                    checkForAvaibileCaptures(currentPlayer);
                    board.repaint();
                }
            }
        });
    }

    public void setTiles() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Color color = (row + col) % 2 == 0 ? Color.WHITE : Color.GRAY;
                tiles[row][col] = new Tile(col, row, color);
            }
        }
    }

    public void setPawns() {
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
        //WHAAAT
        // pawns[0][5] = new Pawn(5, 0, Color.BLACK);
        // pawns[1][6] = new Pawn(6, 1, Color.WHITE);
        // pawns[3][6] = new Pawn(6, 3, Color.WHITE);
        // pawns[5][6] = new Pawn(6, 5, Color.WHITE);
        // pawns[5][4] = new Pawn(4, 5, Color.WHITE);
        // pawns[5][2] = new Pawn(2, 5, Color.WHITE);
        // pawns[3][2] = new Pawn(2, 3, Color.WHITE);
        // pawns[3][0] = new Pawn(0, 3, Color.WHITE);
        //WHAAAT
        // pawns[0][5] = new Pawn(5, 0, Color.BLACK);
        // pawns[1][6] = new Pawn(6, 1, Color.WHITE);
        // pawns[1][4] = new Pawn(4, 1, Color.WHITE);


        // pawns[2][3] = new Pawn(3, 2, Color.BLACK);
        // pawns[4][5] = new Pawn(5, 4, Color.WHITE);


        // pawns[6][1] = new Pawn(1, 6, Color.BLACK);
        // pawns[5][4] = new Pawn(4, 5, Color.WHITE);
        // pawns[4][5] = new Pawn(5, 4, Color.WHITE);
        // pawns[3][2] = new Pawn(2, 3, Color.WHITE);
        // pawns[1][6] = new Pawn(6, 1, Color.WHITE);

        // Pawn temp = new Pawn(7, 6, Color.BLACK);
        // //temp.makeKing();
        // pawns[6][7] = temp;
        // pawns[5][4] = new Pawn(4, 5, Color.WHITE);
        // pawns[3][2] = new Pawn(2, 3, Color.WHITE);
        // pawns[1][2] = new Pawn(2, 1, Color.WHITE);
        // pawns[7][2] = new Pawn(2, 7, Color.WHITE);
        // pawns[1][4] = new Pawn(4, 1, Color.WHITE);


        // pawns[4][5] = new Pawn(5, 4, Color.WHITE);
        // pawns[6][3] = new Pawn(3, 6, Color.WHITE);

        // Pawn temp = new Pawn(6, 7, Color.BLACK);
        // temp.makeKing();
        // pawns[7][6] = temp;
        // pawns[4][3] = new Pawn(3, 4, Color.WHITE);
        // pawns[4][1] = new Pawn(1, 4, Color.WHITE);
        // pawns[2][5] = new Pawn(5, 2, Color.WHITE);
        // pawns[1][6] = new Pawn(6,1, Color.WHITE);

        // Pawn temp = new Pawn(6, 7, Color.BLACK);
        // temp.makeKing();
        // pawns[7][6] = temp;
        // pawns[5][4] = new Pawn(4, 5, Color.WHITE);

        // pawns[4][1] = new Pawn(1, 4, Color.WHITE);
        // pawns[6][1] = new Pawn(1, 6, Color.WHITE);
        // pawns[1][6] = new Pawn(6,1, Color.WHITE);

        // pawns[4][7] = new Pawn(7, 4, Color.WHITE);
        // pawns[3][6] = new Pawn(6, 3, Color.BLACK);
        // pawns[0][5] = new Pawn(5, 0, Color.BLACK);
        // pawns[1][2] = new Pawn(2,1, Color.BLACK);

        // Pawn temp =  new Pawn(6, 7, Color.BLACK);
        // temp.makeKing();
        // pawns[7][6] = temp;
        // pawns[4][5] = new Pawn(5, 4, Color.WHITE);

        // pawns[4][3] = new Pawn(3, 4, Color.WHITE);
        // pawns[4][1] = new Pawn(1, 4, Color.WHITE);
        // pawns[1][2] = new Pawn(2,1, Color.BLACK);


        // Pawn temp =  new Pawn(6, 7, Color.BLACK);
        // temp.makeKing();
        // pawns[7][6] = temp;
        // pawns[5][4] = new Pawn(4, 5, Color.WHITE);
        // pawns[6][1] = new Pawn(1, 6, Color.WHITE);
        // pawns[3][2] = new Pawn(2, 3, Color.WHITE);

        //pawns[1][2] = new Pawn(2, 1, Color.WHITE);



        // Pawn temp =  new Pawn(0, 7, Color.BLACK);
        // temp.makeKing();
        // pawns[7][0] = temp;
        // pawns[5][2] = new Pawn(2, 5, Color.WHITE);
        // pawns[3][4] = new Pawn(4, 3, Color.WHITE);
        // pawns[1][6] = new Pawn(6, 1, Color.WHITE);
        // pawns[5][4] = new Pawn(4, 5, Color.WHITE);
        // pawns[5][6] = new Pawn(6, 5, Color.WHITE);

        // Pawn temp =  new Pawn(0, 7, Color.BLACK);
        // temp.makeKing();
        // pawns[7][0] = temp;
        // pawns[3][4] = new Pawn(4, 3, Color.WHITE);
        // pawns[1][0] = new Pawn(0, 1, Color.WHITE);


        // Pawn temp =  new Pawn(3, 0, Color.WHITE);
        // temp.makeKing();
        // pawns[0][3] = temp;
        // pawns[1][2] = new Pawn(2, 1, Color.BLACK);

        // pawns[2][1] = new Pawn(1, 2, Color.WHITE);
        // pawns[0][3] = new Pawn(3, 0, Color.BLACK);
        // pawns[5][6] = new Pawn(6, 5, Color.WHITE);
        // pawns[6][1] = new Pawn(1, 6, Color.BLACK);

        // pawns[2][1] = new Pawn(1, 2, Color.WHITE);
        // pawns[0][3] = new Pawn(3, 0, Color.BLACK);
        // pawns[5][6] = new Pawn(6, 5, Color.WHITE);
        // pawns[6][1] = new Pawn(1, 6, Color.BLACK);

        // pawns[2][1] = new Pawn(1, 2, Color.BLACK);
        // pawns[5][0] = new Pawn(0, 5, Color.WHITE);
        // pawns[7][0] = new Pawn(0, 7, Color.WHITE);
        // pawns[6][1] = new Pawn(1, 6, Color.WHITE);
        // pawns[6][5] = new Pawn(5, 6, Color.WHITE);

        // pawns[1][6] = new Pawn(6, 1, Color.WHITE);
        // Pawn temp = new Pawn(6, 7, Color.BLACK);
        // temp.makeKing();
        // pawns[7][6] = temp;
        // pawns[5][4] = new Pawn(4, 5, Color.WHITE);
        // pawns[3][2] = new Pawn(2, 3, Color.WHITE);
        // pawns[3][0] = new Pawn(0, 3, Color.WHITE);
        // pawns[7][1] = new Pawn(1, 7, Color.WHITE);
        // pawns[1][0] = new Pawn(0, 1, Color.BLACK);
        // pawns[0][1] = new Pawn(1, 0, Color.BLACK);

        // pawns[3][2] = new Pawn(2, 3, Color.WHITE);
        // pawns[5][0] = new Pawn(0, 5, Color.WHITE);

        //DRAW CHECK
        // Pawn temp = new Pawn(4, 1, Color.BLACK);
        // temp.makeKing();
        // pawns[1][4] = temp;

        // Pawn temp2 = new Pawn(6, 1, Color.WHITE);
        // temp2.makeKing();
        // pawns[1][6] = temp2;
        //DRAW CHECK

        //BLOCK CHECK
        // pawns[0][7] = new Pawn(7, 0, Color.WHITE);
        // pawns[0][5] = new Pawn(5, 0, Color.BLACK);
        // pawns[2][5] = new Pawn(5, 2, Color.BLACK);
        //BLOCK CHECK
    }

    public boolean isValidMove(Pawn pawn, int new_X, int new_Y) {
        if (new_X < 0 || new_X >= 8 ||
                new_Y < 0 || new_Y >= 8) {
            return false;
        }
        if(pawns[new_X][new_Y] != null) return false;
        if (pawn.getColor() != currentPlayer)
            return false;

        int current_X = pawn.getY();
        int current_Y = pawn.getX();

        int rowDiff = new_X - current_X;
        int colDiff = new_Y - current_Y;

        int rowDirection = rowDiff >= 0 ? 1 : -1;
        int colDirection = colDiff >= 0 ? 1 : -1;

        if (!pawn.isKing()) {
            if (pawn.getColor() == Color.BLACK && rowDiff < 0) {
                return false;
            }
            if (pawn.getColor() == Color.WHITE && rowDiff > 0) {
                return false;
            }
        }else{

            if(Math.abs(rowDiff) == Math.abs(colDiff)){
                int check_new_X = new_X-rowDirection;
                int check_new_Y = new_Y-colDirection;
                int pawn_counter = 0;
                while(check_new_X!=current_X&&check_new_Y!=current_Y){
                    if((check_new_X>7) || (check_new_X<0) || check_new_Y>7 || check_new_Y<0) return false;
                    if(pawns[check_new_X][check_new_Y] != null)
                        pawn_counter++;
                    check_new_X -= rowDirection;
                    check_new_Y -= colDirection;
                }
                if(pawn_counter == 0) return true;
                else return false;
            }
        }

        if (Math.abs(rowDiff) == 1 && Math.abs(colDiff) == 1) {
            return true;
        }

        if(pawn.isKing()) return false;

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
        if (king.getColor() == currentPlayer && king.isKing()) {
            kingMultiCaptures=false;
            int currentCapture = getMaxCaptureForKing(king, king, king.getX(), king.getY(), capturePath, capturedPawns);
            if (currentCapture > maxCapture) {
                maxCapture = currentCapture;
                board.repaint();
            }
        }
        subKings.clear();
        return maxCapture;
    }
    //private boolean endFlag = false;
    List<Pawn> replacedPawns = new ArrayList<>();
    List<Pawn> subKings = new ArrayList<>();
    List<Pawn> capturedPawnsByKing = new ArrayList<>();
    List<int[]> capturePathForKing = new ArrayList<>();
    List<List<Pawn>> maxCapturedPawnsByKing = new ArrayList<>();
    List<List<int[]>> maxCapturePathForKing = new ArrayList<>();
    boolean endOfPathFlag = false;
    boolean kingMultiCaptures = false;


    private int getMaxCaptureForKing(Pawn king, Pawn orgKing, int row, int col, List<int[]> capturePath,
    List<Pawn> capturedPawns) {
        boolean endFlag = false;
        int maxCapture = 0;

        for (int directionRow = -1; directionRow <= 1; directionRow += 2) {
            for (int directionCol = -1; directionCol <= 1; directionCol += 2) {

                int currentRow = row+directionRow;
                int currentCol = col+directionCol;
                while(currentRow>=0 && currentRow<=7 && currentCol>=0 && currentCol<=7){
                    if(pawns[currentCol][currentRow] != null && currentCol+directionCol <= 7 && currentCol+directionCol>=0 && currentRow+directionRow<=7 && currentRow+directionRow>=0&& pawns[currentCol+directionCol][currentRow+directionRow] == null){
                        int capturedRow = currentRow;
                        int capturedCol = currentCol;
                        int targetRow = currentRow+directionRow;
                        int targetCol = currentCol+directionCol;

                        while(targetRow>=0 && targetRow<=7 && targetCol>=0 && targetCol<=7){
                            if(isValidCaptureForKing(king,directionRow,directionCol,row, col, targetRow, targetCol, capturedRow, capturedCol,subKings)){
                                Pawn subKing = new Pawn(targetRow, targetCol, currentPlayer);
                                subKing.makeKing();
                                if(subKing != null)
                                    subKings.add(subKing);
                                Pawn capturedPawn = new Pawn(capturedRow,capturedCol, pawns[capturedCol][capturedRow].getColor());
                                if(pawns[capturedCol][capturedRow].isKing())
                                    capturedPawn.makeKing();

                                if(pawns[targetCol][targetRow] != null)
                                    replacedPawns.add(pawns[targetCol][targetRow]);
                                pawns[capturedCol][capturedRow] = null;
                                List<Pawn> subCapturedPawns = new ArrayList<>();
                                List<int[]> subCapturePath = new ArrayList<>();
                                int currentCapture = 1;

                                currentCapture += getMaxCaptureForKing(subKing, orgKing, targetRow, targetCol, subCapturePath,
                                subCapturedPawns);

                                if (currentCapture > maxCapture) {
                                    maxCapture = currentCapture;
                                    if(kingMultiCaptures){
                                        Pawn captured = new Pawn(capturedRow, capturedCol, king.getEnemyColor(currentPlayer));
                                        if(pawns[capturedCol][capturedRow] != null&&pawns[capturedCol][capturedRow].isKing())
                                            captured.makeKing();
                                        (maxCapturedPawnsByKing.get(0)).add( captured);
                                        (maxCapturePathForKing.get(0)).add( new int[] { targetRow, targetCol });

                                        (maxCapturedPawnsByKing.get(1)).add(0,captured);
                                        (maxCapturePathForKing.get(1)).add(0,new int[] { targetRow, targetCol });
                                    }
                                    capturePath.clear();
                                    capturedPawns.clear();
                                    capturePath.add(new int[] { targetRow, targetCol });
                                    capturePath.addAll(subCapturePath);
                                    maxCapturedPawns = capturedPawns;
                                    capturedPawns.clear();
                                    Pawn captured = new Pawn(capturedRow, capturedCol, king.getEnemyColor(currentPlayer));
                                    if(pawns[capturedCol][capturedRow] != null&&pawns[capturedCol][capturedRow].isKing())
                                        captured.makeKing();
                                    capturedPawns.add(captured);
                                    capturedPawns.addAll(subCapturedPawns); // dodajemy do listy wlasciwej
                                }
                                else if(currentCapture == maxCapture&&currentCapture!=0){
                                    kingMultiCaptures= true;
                                    Pawn captured = new Pawn(capturedRow, capturedCol, king.getEnemyColor(currentPlayer));
                                    if(pawns[capturedCol][capturedRow] != null&&pawns[capturedCol][capturedRow].isKing())
                                        captured.makeKing();
                                    subCapturedPawns.add(captured);
                                    subCapturePath.add(new int[] { targetRow, targetCol });

                                    maxCapturedPawnsByKing.add(subCapturedPawns);
                                    maxCapturePathForKing.add(subCapturePath);
                                    maxCapturedPawnsByKing.add(capturedPawns);
                                    maxCapturePathForKing.add(capturePath);
                                    System.out.println(" ");
                                }
                                if(!replacedPawns.isEmpty()){
                                    for(Pawn replacedPawn : replacedPawns)
                                        pawns[replacedPawn.getY()][replacedPawn.getX()] = replacedPawn;
                                }
                                pawns[capturedCol][capturedRow] = capturedPawn;
                                if(capturedPawn.isKing())
                                    pawns[capturedCol][capturedRow].makeKing();
                                if(endFlag){
                                    break;
                                }
                            }
                            targetRow += directionRow;
                            targetCol += directionCol;
                            if(endFlag)
                                break;
                        }
                        if(endFlag)
                            break;
                    }
                    currentRow += directionRow;
                    currentCol += directionCol;
                    if(endFlag)
                        break;
                }
                if(endFlag)
                    break;
            }
            if(endFlag)
                break;
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
        for(Pawn subKing : subKings){
            if(captured_X==subKing.getX()&&captured_Y==subKing.getY()){
                return false;
            }
        }
        int X_Diff = new_X - current_X;
        int Y_Diff = new_Y - current_Y;

        int current_X_t = current_X+(X_Diff/Math.abs(X_Diff));
        int current_Y_t = current_Y+(Y_Diff/Math.abs(Y_Diff));
        int pawn_counter = 0;
        while(current_X_t != new_X && current_Y_t != new_Y){
            if(pawns[current_Y_t][current_X_t] != null)
                pawn_counter++;
            current_X_t += (X_Diff/Math.abs(X_Diff));
            current_Y_t += (Y_Diff/Math.abs(Y_Diff));
        }
        if(pawn_counter > 1) return false;

        return Math.abs(X_Diff) == Math.abs(Y_Diff);
    }

    public void checkForAvaibileCaptures(Color currentPlayer){
        int maxCapture = 0;

        clearArrays();
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
                        if(kingMultiCaptures){
                            Collections.reverse(maxCapturePathForKing.get(0));
                            for(List<int[]> captures : maxCapturePathForKing)
                                maxMaxCapturePath.add(captures);
                            for(List<Pawn> pawnsToCapture : maxCapturedPawnsByKing)
                                maxMaxCapturedPawns.add(pawnsToCapture);
                            kingMultiCaptures=false;
                        } else{
                            List<int[]> temp = new ArrayList<>();
                            for(int[] coords : maxCapturePath){
                                temp.add(coords);
                            }
                            maxMaxCapturePath.add(temp);
                            maxMaxCapturedPawns.add(maxCapturedPawns);
                        }

                    }
                    else if(subMaxCapture == maxCapture && maxCapture != 0){
                        pawnsWithAvaibileCaptures.add(pawns[j][i]);
                        if(kingMultiCaptures){
                            for(List<int[]> captures : maxCapturePathForKing)
                                maxMaxCapturePath.add(captures);
                            for(List<Pawn> pawnsToCapture : maxCapturedPawnsByKing)
                                maxMaxCapturedPawns.add(pawnsToCapture);
                            kingMultiCaptures=false;
                        } else{
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
        }
    }

    public int getMaxCapture(Pawn currentPawn) {
        int maxCapture = 0;
        if(!currentPawn.isKing()){
            int currentCapture = getMaxCaptureForPiece(currentPawn, currentPawn.getX(), currentPawn.getY(), capturePath,
            capturedPawns);
            maxCapture = currentCapture;
            maxCapturePath = capturePath;
            maxCapturedPawns = capturedPawns;
        } else{
            int currentCapture = getMaxKingCapture(currentPawn);
            maxCapture = currentCapture;
            if(capturePath!=null)
                maxCapturePath = capturePath;
            if(capturedPawns!=null)
                maxCapturedPawns = capturedPawns;
        }
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
        for (int directionRow = -1; directionRow <= 1; directionRow += 2) {
            for (int directionCol = -1; directionCol <= 1; directionCol += 2) {

                int capturedRow = row + directionRow;
                int capturedCol = col + directionCol;
                int targetRow = row + 2 * directionRow;
                int targetCol = col + 2 * directionCol;

                if (isValidCapture(currentPawn, row, col, targetRow, targetCol, capturedRow, capturedCol,
                        capturedPawns)) {
                    Pawn capturedPiece = new Pawn(capturedRow, capturedCol, pawns[capturedCol][capturedRow].getColor());
                    if (pawns[capturedCol][capturedRow].isKing())
                        capturedPiece.makeKing();
                    pawns[capturedCol][capturedRow] = null;
                    pawns[targetCol][targetRow] = currentPawn;
                    capturedPawns.add(capturedPiece); // dodajemy
                    List<Pawn> subCapturedPawns = new ArrayList<>();
                    Pawn localPawn = new Pawn(targetRow, targetCol, currentPawn.getColor());
                    List<int[]> subCapturePath = new ArrayList<>();

                    int currentCapture = 1 + getMaxCaptureForPiece(localPawn, targetRow, targetCol, subCapturePath, subCapturedPawns);
                    if (currentCapture > maxCapture) {
                        maxCapture = currentCapture;
                        capturePath.clear();
                        capturePath.add(new int[] { targetRow, targetCol });
                        capturePath.addAll(subCapturePath);
                        maxCapturedPawns = capturedPawns;
                        capturedPawns.clear();
                        capturedPawns.add(new Pawn(capturedRow, capturedCol,localPawn.getEnemyColor(currentPlayer)));
                        capturedPawns.addAll(subCapturedPawns);
                    }
                    pawns[targetCol][targetRow] = null;
                    pawns[capturedCol][capturedRow] = capturedPiece;
                    capturedPawns.remove(capturedPiece);
                    if (capturedPiece.isKing())
                        pawns[capturedCol][capturedRow].makeKing();
                }
            }
        }

        return maxCapture;
    }

    private void clearArrays(){
        maxCapturePath.clear();
        maxCapturedPawns.clear();
        maxMaxCapturePath.clear();
        maxMaxCapturedPawns.clear();
        pawnsWithAvaibileCaptures.clear();
        capturePathForKing.clear();
        capturedPawnsByKing.clear();
        maxCapturePathForKing.clear();
        maxCapturedPawnsByKing.clear();
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
    }

    private boolean checkforBlock(){
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if(pawns[col][row] != null) {
                    Pawn pawn = pawns[col][row];
                    if(pawns[col][row].isKing())
                        pawn.makeKing();
                    if (pawn != null && pawn.getColor() == currentPlayer) {
                        if (canPawnMove(pawn)) {
                            return false;
                        }
                    }
                }
            }
        }
        gameOver = true;
        if(currentPlayer==Color.WHITE)
            winner = Color.BLACK;
        else
            winner = Color.WHITE;
        return true;
    }
    private boolean checkForDraw(){
        if(movesWithKings == 30){
            gameDraw = true;
            winner = null;
            return true;
        }
        return false;
    }


    private boolean canPawnMove(Pawn pawn) {
        int direction = (pawn.getColor() == Color.BLACK) ? 1 : -1;
        int row = pawn.getX();
        int col = pawn.getY();

        if (isValidMove(pawn, col - direction, row + direction) || isValidMove(pawn, col + direction, row - direction)) {
            return true;
        }
        if (pawn.isKing()) {
            if (isValidMove(pawn, col - direction, row - direction) || isValidMove(pawn, col + direction, row + direction)) { // col - 1, row - direction) || isValidMove(pawn, col + 1, row - direction
                return true;
            }
        }
        return false;
    }


    private void switchPlayer() {
        currentPlayer = (currentPlayer == Color.BLACK ? Color.WHITE : Color.BLACK);
    }
}
