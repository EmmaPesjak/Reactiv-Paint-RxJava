package se.miun.dt176g.xxxxyyyy.reactive;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 * <h1>Menu</h1> 
 * Creates the menu in the GUI.
 * @author 	Emma Pesjak
 * @version 1.0
 * @since 	2023-09-06
 */
public class Menu extends JMenuBar {

	private static final long serialVersionUID = 1L;  // Vad är denna till?

	/**
	 * Constructor for the options menu, sets the frame.
	 * @param frame is the Swing frame.
	 */
	public Menu(MainFrame frame) {
		init(frame);
	}

	/**
	 * Method which initializes the user's options menu, sets appropriate action listeners on the menu items.
	 * @param frame is the Swing frame.
	 */
	private void init(MainFrame frame) {
		JMenu optionsMenu;
		JMenu toolMenu;
		JMenu thicknessMenu;
		JMenu colorMenu;
		JMenuItem menuItem;

		// General options menu.
		optionsMenu = new JMenu("Options");
		this.add(optionsMenu);
		menuItem = new JMenuItem("Send message"); // behöver jag ha denna?
		menuItem.addActionListener(e -> messageEvent(frame));
		optionsMenu.add(menuItem);
		menuItem = new JMenuItem("Clear canvas");
		menuItem.addActionListener(e ->  clearEvent(frame));
		optionsMenu.add(menuItem);

		// Tools/shapes menu.
		toolMenu = new JMenu("Tools/Shapes");
		this.add(toolMenu);
		menuItem = new JMenuItem("Rectangle");
		menuItem.addActionListener(e -> toolEvent(frame, "Rectangle"));
		toolMenu.add(menuItem);
		menuItem = new JMenuItem("Oval");
		menuItem.addActionListener(e -> toolEvent(frame, "Oval"));
		toolMenu.add(menuItem);
		menuItem = new JMenuItem("Line");
		menuItem.addActionListener(e -> toolEvent(frame, "Line"));
		toolMenu.add(menuItem);
		menuItem = new JMenuItem("Freehand");
		menuItem.addActionListener(e -> toolEvent(frame, "Freehand"));
		toolMenu.add(menuItem);

		// Thickness menu.
		thicknessMenu = new JMenu("Thickness");
		this.add(thicknessMenu);
		menuItem = new JMenuItem("Thin");
		menuItem.addActionListener(e -> thicknessEvent(frame, "Thin"));
		thicknessMenu.add(menuItem);
		menuItem = new JMenuItem("Medium");
		menuItem.addActionListener(e -> thicknessEvent(frame, "Medium"));
		thicknessMenu.add(menuItem);
		menuItem = new JMenuItem("Thick");
		menuItem.addActionListener(e -> thicknessEvent(frame, "Thick"));
		thicknessMenu.add(menuItem);

		// Color menu.
		colorMenu = new JMenu("Color");
		this.add(colorMenu);
		menuItem = new JMenuItem("Pink");
		menuItem.addActionListener(e -> colorEvent(frame, "Pink"));
		colorMenu.add(menuItem);
		menuItem = new JMenuItem("Purple");
		menuItem.addActionListener(e ->  colorEvent(frame, "Purple"));
		colorMenu.add(menuItem);
		menuItem = new JMenuItem("Green");
		menuItem.addActionListener(e ->  colorEvent(frame, "Green"));
		colorMenu.add(menuItem);
	}

	private void messageEvent(MainFrame frame) {
	
		String message = (String) JOptionPane.showInputDialog(frame,
				"Send message to everyone:");
		
		if(message != null && !message.isEmpty()) {
			JOptionPane.showMessageDialog(frame, message);
		}
	}
	
	private void clearEvent(MainFrame frame) {
		
	}

	private void toolEvent(MainFrame frame, String tool) {

	}

	private void thicknessEvent(MainFrame frame, String thickness) {

	}

	private void colorEvent(MainFrame frame, String color) {

	}
}
