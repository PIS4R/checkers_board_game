package com.checkers;
import javax.swing.JFrame;


public class MainWindow extends JFrame {

	public static final int DEFAULT_WIDTH = 400;
	public static final int DEFAULT_HEIGHT = 439;
	public static final String DEFAULT_TITLE = "Java Checkers";

    protected Board board = null;

	public MainWindow() {
		this(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_TITLE);
	}
	public MainWindow(int width, int height, String title) {

		super(title);
		super.setSize(width, height);
		super.setLocationByPlatform(true);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setTitle(DEFAULT_TITLE);
        board = new Board();
        add(board);
	}
}
