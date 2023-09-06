package se.miun.dt176g.xxxxyyyy.reactive;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * <h1>ConcreteOval</h1> Creates a Circle-object.
 * Concrete class which extends Shape.
 * In other words, this class represents ONE type of shape
 * i.e. a circle, rectangle, n-sided regular polygon (if that's your thing)
 *
 * @author 	--YOUR NAME HERE--
 * @version 1.0
 * @since 	2022-09-08
 */

public class ConcreteOval extends Shape {

	public ConcreteOval(Color color, int thickness) {
		super(color, thickness);
	}

	@Override
	public void handleMouseEvent(MouseEvent e) {

	}

	@Override
	public void draw(Graphics g) {
		Graphics2D g2 = (Graphics2D) g; // Type-cast the parameter to Graphics2D.
		   
		// Draw using g2.
		// eg g2.fillOval(int x, int y, int width, int height)
	}

}
