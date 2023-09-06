package se.miun.dt176g.xxxxyyyy.reactive;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * <h1>ConcreteRectangle</h1> Creates a Line-object.
 * Concrete class which extends Shape, representing a line.
 * @author 	Emma Pesjak
 * @version 1.0
 * @since 	2023-09-06
 */

public class ConcreteLine extends Shape {

    public ConcreteLine(Color color, int thickness) {
        super(color, thickness);
    }

    @Override
    public void handleMouseEvent(MouseEvent e) {

    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g; // Type-cast the parameter to Graphics2D.

        // Draw using g2.
        // eg g2.fillOval(int x, int y, int width, int height)  //här ska vi ju inte ha oval
    }

}