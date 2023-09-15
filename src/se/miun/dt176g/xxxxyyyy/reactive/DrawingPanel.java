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
 * @since 	2023-09-07
 */
public class DrawingPanel extends JPanel {
	private final Drawing drawing; // Container for the Shapes.
	private final Menu menu; // For getting the menu options. I know, ugly solution, would be fancier with interfaces :).
	private Shape currentShape; // Store the current shape being drawn.

	/**
	 * Constructor which creates the mouse event listeners.
	 * @param drawing is the container of drawn shapes.
	 * @param menu is the menu which contains the user's options.
	 */
	public DrawingPanel(Drawing drawing, Menu menu) {
		this.drawing = drawing;
		this.menu = menu;

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
				mouseEventSubject.onNext(e); // Emit mouse release event.
			}
		});

		// Handle mouse drag events.
		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				mouseEventSubject.onNext(e); // Emit mouse drag event.
			}
		});
		// Subscribe to the mouse event observable to handle drawing.
		mouseEventObservable.subscribe(this::handleMouseEvent);


		// TODO LÄSTE I BOKEN ATT MAN SKA UNDVIKA SUBJEKT SÅ NEDANSTÅENDE PROVISORISKA KOD FUNGERAR MEN NU VET JAG INTE
		// TODO OM REQUIREMENT 2 ÄR OK, FÅR NOG JOBBA PÅ DET.

//		this.drawing = drawing;
//		this.menu = menu;
//
//		// Create an observable directly from mousePressed, mouseReleased, and mouseDragged events.
//		Observable<MouseEvent> mouseEventObservable = Observable.create(emitter -> {
//			addMouseListener(new MouseAdapter() {
//				@Override
//				public void mousePressed(MouseEvent e) {
//					emitter.onNext(e); // Emit mouse press event.
//				}
//
//				@Override
//				public void mouseReleased(MouseEvent e) {
//					emitter.onNext(e); // Emit mouse release event.
//				}
//			});
//
//			addMouseMotionListener(new MouseAdapter() {
//				@Override
//				public void mouseDragged(MouseEvent e) {
//					emitter.onNext(e); // Emit mouse drag event.
//				}
//			});
//		});
//
//		// Subscribe to the mouse event observable to handle drawing.
//		mouseEventObservable.subscribe(this::handleMouseEvent);

	}

	/**
	 * Handles mouse events, creating shapes drawn in the GUI.
	 * @param e is the mouse event.
	 */
	private void handleMouseEvent(MouseEvent e) {	//TODO kolla upp thread safety.
		int x = e.getX();
		int y = e.getY();
		String selectedShapeType = menu.selectedTool;
		switch (selectedShapeType) {
			case "Rectangle":
				if (e.getID() == MouseEvent.MOUSE_PRESSED) {
					currentShape = new RectangleShape(menu.selectedColor, menu.selectedThickness);
					((RectangleShape) currentShape).setStartPoint(new Point(x, y));
					((RectangleShape) currentShape).setEndPoint(new Point(x, y));
					drawing.addShape(currentShape);
				} else if (e.getID() == MouseEvent.MOUSE_DRAGGED && currentShape instanceof RectangleShape) {
					((RectangleShape) currentShape).setEndPoint(new Point(x, y));
				}
				break;
			case "Oval":
				if (e.getID() == MouseEvent.MOUSE_PRESSED) {
					currentShape = new OvalShape(menu.selectedColor, menu.selectedThickness);
					((OvalShape) currentShape).setStartPoint(new Point(x, y));
					((OvalShape) currentShape).setEndPoint(new Point(x, y));
					drawing.addShape(currentShape);
				} else if (e.getID() == MouseEvent.MOUSE_DRAGGED && currentShape instanceof OvalShape) {
					((OvalShape) currentShape).setEndPoint(new Point(x, y));
				}
				break;
			case "Line":
				if (e.getID() == MouseEvent.MOUSE_PRESSED) {
					currentShape = new LineShape(menu.selectedColor, menu.selectedThickness);
					((LineShape) currentShape).setStartPoint(new Point(x, y));
					((LineShape) currentShape).setEndPoint(new Point(x, y));
					drawing.addShape(currentShape);
				} else if (e.getID() == MouseEvent.MOUSE_DRAGGED && currentShape instanceof LineShape) {
					((LineShape) currentShape).setEndPoint(new Point(x, y));
				}
				break;
			case "Freehand":
				if (e.getID() == MouseEvent.MOUSE_PRESSED) {
					currentShape = new FreehandShape(menu.selectedColor, menu.selectedThickness);
					((FreehandShape) currentShape).addPoint(x, y);
					drawing.addShape(currentShape);
				} else if (e.getID() == MouseEvent.MOUSE_DRAGGED && currentShape instanceof FreehandShape) {
					((FreehandShape) currentShape).addPoint(x, y);
				}
				break;

				//TODO lägg till default med error handling?
		}
		// Repaint the panel with the updated drawing.
		repaint();
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

	/**
	 * Clear the drawing.
	 */
	public void clearDrawing() {
		drawing.clear();
		repaint(); // Redraw the panel to reflect the cleared drawing
	}
}
