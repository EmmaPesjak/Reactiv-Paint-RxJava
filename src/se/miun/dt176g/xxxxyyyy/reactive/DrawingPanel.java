package se.miun.dt176g.xxxxyyyy.reactive;

import io.reactivex.rxjava3.subjects.PublishSubject;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 * <h1>DrawingPanel</h1> Creates a Canvas-object for displaying all graphics
 * already drawn.
 *
 * @author 	--YOUR NAME HERE--
 * @version 1.0
 * @since 	2022-09-08
 */

@SuppressWarnings("serial")
public class DrawingPanel extends JPanel {

	private Drawing drawing;


	public DrawingPanel(Menu menu) {
		drawing = new Drawing();
	}

	public void redraw() {
		repaint();
	}

	public void setDrawing(Drawing d) {
		drawing = d;
		repaint();
	}

	public Drawing getDrawing() {
		return drawing;
	}

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);
		drawing.draw(g);
	}

}
