package se.miun.dt176g.xxxxyyyy.reactive;

import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * <h1>ConcreteRectangle</h1> Creates a Freehand-object.
 * Concrete class which extends Shape, representing a freehand drawing.
 * @author 	Emma Pesjak
 * @version 1.0
 * @since 	2023-09-06
 */

public class ConcreteFreehand extends Shape {

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g; // Type-cast the parameter to Graphics2D.

        // Draw using g2.
        // eg g2.fillOval(int x, int y, int width, int height)  //här ska vi ju inte ha oval
    }

}