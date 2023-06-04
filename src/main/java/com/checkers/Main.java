package com.checkers;

import javax.swing.UIManager;

public class Main {

	public static void main(String[] args) {

		//Set the look and feel to the OS look and feel
		try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Create a window to display the checkers game
		MainWindow window = new MainWindow();
		window.setDefaultCloseOperation(MainWindow.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
		window.setVisible(true);
	}
}
