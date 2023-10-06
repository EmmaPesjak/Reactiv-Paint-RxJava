package se.miun.dt176g.xxxxyyyy.reactive;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

/**
 * <h1>DrawingPanel</h1>
 * Creates a Canvas-object for displaying all graphics drawn.
 * @author 	Emma Pesjak
 * @version 1.0
 * @since 	2023-10-06
 */
public class DrawingPanel extends JPanel {
	private final Drawing drawing; // Container for the Shapes.
	private Shape currentShape; // Store the current shape being drawn.
	public Color selectedColor = Color.PINK; // Default color.
	public int selectedThickness = 2; // Default thickness.
	public String selectedShape = "Freehand"; // Default shape type.
	private final ConnectionHandler connectionHandler; // Owner of the drawing panel.
	private final PublishSubject<Shape> shapePublishSubject = PublishSubject.create();

	/**
	 * Constructor which creates the mouse event listeners and subscribes to the menu observables.
	 * @param drawing is the container of drawn shapes.
	 * @param menu is the menu which contains the user's options.
	 */
	public DrawingPanel(Drawing drawing, Menu menu, ConnectionHandler connectionHandler) {
		this.drawing = drawing;
		this.connectionHandler = connectionHandler;

		// Subscribe to Shapes being drawn.
		shapePublishSubject.subscribe(this::sendShapeToConnectionHandler);

		// Create a subject for mouse events.
		PublishSubject<MouseEvent> mouseEventSubject = PublishSubject.create();
		// Share the observable.
		Observable<MouseEvent> mouseEventObservable = mouseEventSubject.share();

		// Add mouse event listeners to handle user input for drawing.
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				mouseEventSubject.onNext(e); // Emit mouse press event.
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// Pass forward the finished shape.
				shapePublishSubject.onNext(currentShape);
			}
		});

		// Handle mouse drag events.
		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				mouseEventSubject.onNext(e); // Emit mouse drag event.
			}
		});

		// Subscribe to the menu Observables to react to changes in menu options.
		menu.shapeObservable()
				.subscribe(this::handleShapeChange);
		menu.thicknessObservable()
				.subscribe(this::handleThicknessChange);
		menu.colorObservable()
				.subscribe(this::handleColorChange);
		menu.clearDrawingObservable()
				.subscribe(clear -> onClear());

		// Subscribe to the mouse event observable to handle drawing.
		mouseEventObservable.subscribe(this::handleMouseEvent);
	}

	/**
	 * Sends a given shape object to the connection handler,
	 * which facilitates communication with remote clients or servers.
	 * @param shape is the shape object to be sent.
	 */
	private void sendShapeToConnectionHandler(Shape shape) {
		connectionHandler.sendShape(shape);
	}

	/**
	 * Handles shape change events by updating the selected shape.
	 * @param shape is the new selected shape.
	 */
	private void handleShapeChange(String shape) {
		selectedShape = shape;
	}

	/**
	 * Handles thickness change events by updating the selected thickness.
	 * @param thickness is the new selected thickness.
	 */
	private void handleThicknessChange(int thickness) {
		selectedThickness = thickness;
	}

	/**
	 * Handles color change events by updating the selected color.
	 * @param color is the new selected color.
	 */
	private void handleColorChange(Color color) {
		selectedColor = color;
	}

	/**
	 * Handles mouse events, creating shapes drawn in the GUI.
	 * @param e is the mouse event.
	 */
	private void handleMouseEvent(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		String selectedShapeType = selectedShape;
		switch (selectedShapeType) {
			case "Rectangle":
				if (e.getID() == MouseEvent.MOUSE_PRESSED) {
					currentShape = new RectangleShape(selectedColor, selectedThickness);
					((RectangleShape) currentShape).setStartPoint(new Point(x, y));
					((RectangleShape) currentShape).setEndPoint(new Point(x, y));
					drawing.addShape(currentShape);
				} else if (e.getID() == MouseEvent.MOUSE_DRAGGED && currentShape instanceof RectangleShape) {
					((RectangleShape) currentShape).setEndPoint(new Point(x, y));
				}
				break;
			case "Oval":
				if (e.getID() == MouseEvent.MOUSE_PRESSED) {
					currentShape = new OvalShape(selectedColor, selectedThickness);
					((OvalShape) currentShape).setStartPoint(new Point(x, y));
					((OvalShape) currentShape).setEndPoint(new Point(x, y));
					drawing.addShape(currentShape);
				} else if (e.getID() == MouseEvent.MOUSE_DRAGGED && currentShape instanceof OvalShape) {
					((OvalShape) currentShape).setEndPoint(new Point(x, y));
				}
				break;
			case "Line":
				if (e.getID() == MouseEvent.MOUSE_PRESSED) {
					currentShape = new LineShape(selectedColor, selectedThickness);
					((LineShape) currentShape).setStartPoint(new Point(x, y));
					((LineShape) currentShape).setEndPoint(new Point(x, y));
					drawing.addShape(currentShape);
				} else if (e.getID() == MouseEvent.MOUSE_DRAGGED && currentShape instanceof LineShape) {
					((LineShape) currentShape).setEndPoint(new Point(x, y));
				}
				break;
			case "Freehand":
				if (e.getID() == MouseEvent.MOUSE_PRESSED) {
					currentShape = new FreehandShape(selectedColor, selectedThickness);
					((FreehandShape) currentShape).addPoint(x, y);
					drawing.addShape(currentShape);
				} else if (e.getID() == MouseEvent.MOUSE_DRAGGED && currentShape instanceof FreehandShape) {
					((FreehandShape) currentShape).addPoint(x, y);
				}
				break;
			default:
				throw new IllegalArgumentException("Invalid selectedShapeType: " + selectedShapeType);
		}
		// Repaint the panel with the updated drawing.
		repaint();
	}

	/**
	 * Clear the drawing and repaints the panel.
	 */
	public void clearDrawing() {
		drawing.clear();
		repaint(); // Redraw the panel to reflect the cleared drawing.
	}

	/**
	 * Clears the drawing and notifies the associated client/server, about the clear event.
	 */
	public void onClear() {
		connectionHandler.clearEvent();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		// Iterate over shapes in the drawing and call their draw method.
		for (Shape shape : drawing.getShapes()) {
			shape.draw(g);
		}
	}
}
