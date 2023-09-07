package se.miun.dt176g.xxxxyyyy.reactive;

import java.awt.*;
import javax.swing.*;

/**
 * <h1>MainFrame</h1> 
 * JFrame for the applications GUI.
 * @author 	Emma Pesjak
 * @version 1.0
 * @since 	2023-09-07
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {
	DrawingPanel drawingPanel;

	/**
	 * Constructor setting the layout and interface.
	 */
	public MainFrame() {
		// Default window-size.
		this.setSize(1200, 900);
		// Application closes when the "x" in the upper-right corner is clicked.
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		String header = "Reactive Paint";
		this.setTitle(header);

		// Changes layout from default to BorderLayout.
		this.setLayout(new BorderLayout());

		// Creates all necessary objects and adds them to the MainFrame.
		Drawing drawing = new Drawing();
		Menu menu = new Menu(this);
		drawingPanel = new DrawingPanel(drawing, menu);
		drawingPanel.setBounds(0, 0, getWidth(), getHeight());
		this.getContentPane().add(drawingPanel, BorderLayout.CENTER);

		//menu = new Menu(this);
		this.setJMenuBar(menu);
	}

	/**
	 * Clears the frame by removing the shapes drawn.
	 */
	public void clearFrame() {
		drawingPanel.clearDrawing();
	}
}
