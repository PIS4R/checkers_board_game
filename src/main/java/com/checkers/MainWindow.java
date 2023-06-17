package com.checkers;
//package ui;
import com.checkers.Tile;


import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

// import model.Player;
// import network.CheckersNetworkHandler;
// import network.ConnectionListener;
// import network.Session;

/**
 * The {@code CheckersWindow} class is responsible for managing a window. This
 * window contains a game of checkers and also options to change the settings
 * of the game with an {@link OptionPanel}.
 */
public class MainWindow extends JFrame {

	private static final long serialVersionUID = 8782122389400590079L;


    public static final int TILE_SIZE = 20;




    private boolean light = true;
	/** The default width for the checkers window. */
	public static final int DEFAULT_WIDTH = 500;

	/** The default height for the checkers window. */
	public static final int DEFAULT_HEIGHT = 600;

	/** The default title for the checkers window. */
	public static final String DEFAULT_TITLE = "Java Checkers";

    protected Board board = null;
	/** The checker board component playing the updatable game. */
	//private Board board;
    //private Tile[][] board = new Tile[8][8];
    private Tile tile;
	// private OptionPanel opts;

	// private Session session1;

	// private Session session2;

	public MainWindow() {
		this(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_TITLE);
	}

	// public MainWindow(Player player1, Player player2) {
	// 	this();
	// 	setPlayer1(player1);
	// 	setPlayer2(player2);
	// }

	public MainWindow(int width, int height, String title) {

		// Setup the window
		super(title);
		super.setSize(width, height); //1920, 1080
		super.setLocationByPlatform(true);
        //     board = ImageIO.read(new File("../assets/board.jpg"));
        //Container container = getContentPane();
		// Setup the components
		//JPanel layout = new JPanel(new BorderLayout());
        //layout.setLayout(null); //paint
        //setLayout(new GridLayout(8,8));
        setSize( 400, 439 );
        setTitle( "Checkers" );
        board = new Board(this, width, height);
        add( board );
        //pack();
        //setVisible(true);
        //this.board = new Board(this, width, height);

        //this.board.setBounds((1920-200)/2, (1080-400)/2,200,200); //paint()

        //this.add(layout);



		//this.board = //new MainWindow(this);
		// this.opts = new OptionPanel(this);

        //this.board.setBounds(800, 1200, width, height);
        //does nothing...

		//layout.add(this.board, BorderLayout.CENTER);
        //layout.add(this.tile, BorderLayout.CENTER);

		// layout.add(opts, BorderLayout.SOUTH);
	}




	// public MainWindow getBoard() {
	// 	return board;
	// }

	/**
	 * Updates the type of player that is being used for player 1.
	 *
	 * @param player1	the new player instance to control player 1.
	 */
	// public void setPlayer1(Player player1) {
	// 	this.board.setPlayer1(player1);
	// 	this.board.update();
	// }

	/**
	 * Updates the type of player that is being used for player 2.
	 *
	 * @param player2	the new player instance to control player 2.
	 */
	// public void setPlayer2(Player player2) {
	// 	this.board.setPlayer2(player2);
	// 	this.board.update();
	// }

	/**
	 * Resets the game of checkers in the window.
	 */
	// public void restart() {
	// 	this.board.getGame().restart();
	// 	this.board.update();
	// }

	// public void setGameState(String state) {
	// 	this.board.getGame().setGameState(state);
	// }

	// public Session getSession1() {
	// 	return session1;
	// }

	// public Session getSession2() {
	// 	return session2;
	// }
}
