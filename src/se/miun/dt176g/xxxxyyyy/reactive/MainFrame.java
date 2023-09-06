package se.miun.dt176g.xxxxyyyy.reactive;

import java.awt.*;
import javax.swing.*;

/**
 * <h1>MainFrame</h1> 
 * JFrame to contain the rest
 *
 * @author 	--YOUR NAME HERE--
 * @version 1.0
 * @since 	2022-09-08
 */

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private String header;
	private Menu menu = new Menu(this);
	private DrawingPanel drawingPanel = new DrawingPanel(menu);


	public MainFrame() {

		// default window-size.
		this.setSize(1200, 900);
		// application closes when the "x" in the upper-right corner is clicked.
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.header = "Reactive Paint";
		this.setTitle(header);

		// Changes layout from default to BorderLayout
		this.setLayout(new BorderLayout());

		// Creates all necessary objects and adds them to the MainFrame (just one object right now)
		//drawingPanel = new DrawingPanel();
		drawingPanel.setBounds(0, 0, getWidth(), getHeight());
		this.getContentPane().add(drawingPanel, BorderLayout.CENTER);

		//menu = new Menu(this);
		this.setJMenuBar(menu);

	}

	// vete katten om detta kommer fungera men det får jag testa i framtiden :)
	public void clearFrame() {
//		this.removeAll();
//		this.repaint();
//		this.revalidate();
//
//		drawingPanel = new DrawingPanel();
//		drawingPanel.setBounds(0, 0, getWidth(), getHeight());
//		this.getContentPane().add(drawingPanel, BorderLayout.CENTER);
//		this.setJMenuBar(new Menu(this));
	}


}
