package se.miun.dt176g.xxxxyyyy.reactive;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1>FreehandShape</h1> Creates a Freehand-object.
 * Concrete class which extends Shape, representing a freehand drawing.
 * @author 	Emma Pesjak
 * @version 1.0
 * @since 	2023-09-07
 */
public class FreehandShape extends Shape implements Serializable {
    private final List<Point> path; // Store the points for drawing the freehand shape.
    private final Color color;
    private final int thickness;
    private static final long serialVersionUID = 1L;

    /**
     * Constructor that sets the color and thickness of the rectangle.
     * It also creates the list of points of the path.
     * @param color is the color.
     * @param thickness is the thickness.
     */
    public FreehandShape(Color color, int thickness) {
        this.color = color;
        this.thickness = thickness;
        path = new ArrayList<>();
    }

    /**
     * Adds a point to the path.
     * @param x is the x coordinate.
     * @param y is the y coordinate.
     */
    public void addPoint(int x, int y) {
        path.add(new Point(x, y));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw(Graphics g) {
        // Cast to Graphics2D to be able to set the stroke size.
        Graphics2D g2d = (Graphics2D) g;

        // Set the color and stroke (thickness) for drawing.
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(thickness));

        // Traverse the path, drawing it.
        if (path.size() >= 2) {
            Point prevPoint = path.get(0);
            for (int i = 1; i < path.size(); i++) {
                Point currentPoint = path.get(i);
                g2d.drawLine(prevPoint.x, prevPoint.y, currentPoint.x, currentPoint.y);
                prevPoint = currentPoint;
            }
        }
    }
}
