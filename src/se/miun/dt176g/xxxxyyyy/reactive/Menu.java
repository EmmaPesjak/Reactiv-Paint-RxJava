package se.miun.dt176g.xxxxyyyy.reactive;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import java.awt.*;

/**
 * <h1>Menu</h1> 
 * Creates the menu in the GUI.
 * @author 	Emma Pesjak
 * @version 1.0
 * @since 	2023-09-07
 */
public class Menu extends JMenuBar {
	private static final long serialVersionUID = 1L;  // Ta bort denna?
	public Color selectedColor = Color.PINK; // Default color.
	public int selectedThickness = 2; // Default thickness.
	public String selectedTool = "Freehand"; // Default tool type.

	/**
	 * Constructor for the options' menu, sets the frame.
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
		menuItem.addActionListener(e -> toolEvent("Rectangle"));
		toolMenu.add(menuItem);
		menuItem = new JMenuItem("Oval");
		menuItem.addActionListener(e -> toolEvent("Oval"));
		toolMenu.add(menuItem);
		menuItem = new JMenuItem("Line");
		menuItem.addActionListener(e -> toolEvent("Line"));
		toolMenu.add(menuItem);
		menuItem = new JMenuItem("Freehand");
		menuItem.addActionListener(e -> toolEvent("Freehand"));
		toolMenu.add(menuItem);

		// Thickness menu.
		thicknessMenu = new JMenu("Thickness");
		this.add(thicknessMenu);
		menuItem = new JMenuItem("Thin");
		menuItem.addActionListener(e -> thicknessEvent(2));
		thicknessMenu.add(menuItem);
		menuItem = new JMenuItem("Medium");
		menuItem.addActionListener(e -> thicknessEvent(4));
		thicknessMenu.add(menuItem);
		menuItem = new JMenuItem("Thick");
		menuItem.addActionListener(e -> thicknessEvent(6));
		thicknessMenu.add(menuItem);

		// Color menu.
		colorMenu = new JMenu("Color");
		this.add(colorMenu);
		menuItem = new JMenuItem("Pink");
		menuItem.addActionListener(e -> colorEvent(Color.PINK));
		colorMenu.add(menuItem);
		menuItem = new JMenuItem("Cyan");
		menuItem.addActionListener(e ->  colorEvent(Color.CYAN));
		colorMenu.add(menuItem);
		menuItem = new JMenuItem("Green");
		menuItem.addActionListener(e ->  colorEvent(Color.GREEN));
		colorMenu.add(menuItem);
		menuItem = new JMenuItem("Red");
		menuItem.addActionListener(e ->  colorEvent(Color.RED));
		colorMenu.add(menuItem);
		menuItem = new JMenuItem("Blue");
		menuItem.addActionListener(e ->  colorEvent(Color.BLUE));
		colorMenu.add(menuItem);
		menuItem = new JMenuItem("Magenta");
		menuItem.addActionListener(e ->  colorEvent(Color.MAGENTA));
		colorMenu.add(menuItem);
		menuItem = new JMenuItem("Yellow");
		menuItem.addActionListener(e ->  colorEvent(Color.YELLOW));
		colorMenu.add(menuItem);
		menuItem = new JMenuItem("Black");
		menuItem.addActionListener(e ->  colorEvent(Color.BLACK));
		colorMenu.add(menuItem);
	}

	/**
	 * Ska jag ha med denna? Får se.
	 * @param frame
	 */
	private void messageEvent(MainFrame frame) {
	
		String message = (String) JOptionPane.showInputDialog(frame,
				"Send message to everyone:");
		
		if(message != null && !message.isEmpty()) {
			JOptionPane.showMessageDialog(frame, message);
		}
	}

	/**
	 * Event for clearing the frame.
	 * @param frame is the frame.
	 */
	private void clearEvent(MainFrame frame) {
		frame.clearFrame();
	}

	/**
	 * Event for when the user changes tool/shape.
	 * @param tool is the chosen tool.
	 */
	private void toolEvent(String tool) {
		selectedTool = tool; // Update the selected tool.
	}

	/**
	 * Event for when the user changes thickness.
	 * @param thickness is the chosen thickness.
	 */
	private void thicknessEvent(int thickness) {
		selectedThickness = thickness; // Update the selected thickness.
	}

	/**
	 * Event for when the user changes color.
	 * @param color is the chosen color.
	 */
	private void colorEvent(Color color) {
		selectedColor = color; // Update the selected color.
	}
}
