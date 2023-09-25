package se.miun.dt176g.xxxxyyyy.reactive;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;

import javax.swing.*;
import java.awt.*;

/**
 * <h1>Menu</h1> 
 * Creates the menu in the GUI.
 * @author 	Emma Pesjak
 * @version 1.0
 * @since 	2023-09-18
 */
public class Menu extends JMenuBar {
	private final PublishSubject<Boolean> clearDrawingSubject = PublishSubject.create();
	private final PublishSubject<String> shapeSubject = PublishSubject.create();
	private final PublishSubject<Integer> thicknessSubject = PublishSubject.create();
	private final PublishSubject<Color> colorSubject = PublishSubject.create();

	/**
	 * Constructor for the options' menu, initializes the menu.
	 */
	public Menu() {
		init();
	}

	/**
	 * Method which initializes the user's options menu, sets appropriate action listeners on the menu items.
	 */
	private void init() {
		JMenu optionsMenu;
		JMenu shapeMenu;
		JMenu thicknessMenu;
		JMenu colorMenu;
		JMenuItem menuItem;

		// General options menu.
		optionsMenu = new JMenu("Options");
		this.add(optionsMenu);
		menuItem = new JMenuItem("Clear canvas");
		menuItem.addActionListener(e ->  clearEvent());
		optionsMenu.add(menuItem);

		// Tools/shapes menu.
		shapeMenu = new JMenu("Tools/Shapes");
		this.add(shapeMenu);
		menuItem = new JMenuItem("Rectangle");
		menuItem.addActionListener(e -> shapeEvent("Rectangle"));
		shapeMenu.add(menuItem);
		menuItem = new JMenuItem("Oval");
		menuItem.addActionListener(e -> shapeEvent("Oval"));
		shapeMenu.add(menuItem);
		menuItem = new JMenuItem("Line");
		menuItem.addActionListener(e -> shapeEvent("Line"));
		shapeMenu.add(menuItem);
		menuItem = new JMenuItem("Freehand");
		menuItem.addActionListener(e -> shapeEvent("Freehand"));
		shapeMenu.add(menuItem);

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
	 * Creates and returns an Observable for clearing the drawing.
	 * @return an Observable<Boolean> for clearing the drawing.
	 */
	public Observable<Boolean> clearDrawingObservable() {
		return clearDrawingSubject;
	}

	/**
	 * Creates and returns an Observable for shape selection.
	 * @return an Observable<String> for shape selection.
	 */
	public Observable<String> shapeObservable() {
		return shapeSubject;
	}

	/**
	 * Creates and returns an Observable for thickness selection.
	 * @return an Observable<Integer> for thickness selection.
	 */
	public Observable<Integer> thicknessObservable() {
		return thicknessSubject;
	}

	/**
	 * Creates and returns an Observable for color selection.
	 * @return an Observable<Color> for color selection.
	 */
	public Observable<Color> colorObservable() {
		return colorSubject;
	}

	/**
	 * Emits an event to indicate the drawing should be cleared.
	 */
	private void clearEvent() {
		clearDrawingSubject.onNext(true);
	}

	/**
	 * Emits an event to indicate a change in the selected shape.
	 * @param shape is the selected shape.
	 */
	private void shapeEvent(String shape) {
		shapeSubject.onNext(shape);
	}

	/**
	 * Emits an event to indicate a change in the selected thickness.
	 * @param thickness is the selected thickness.
	 */
	private void thicknessEvent(int thickness) {
		thicknessSubject.onNext(thickness);
	}

	/**
	 * Emits an event to indicate a change in the selected color.
	 * @param color is the selected color.
	 */
	private void colorEvent(Color color) {
		colorSubject.onNext(color);
	}
}
