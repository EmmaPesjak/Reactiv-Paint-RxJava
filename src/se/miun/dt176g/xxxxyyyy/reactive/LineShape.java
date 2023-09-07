package se.miun.dt176g.xxxxyyyy.reactive;

import java.awt.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 * <h1>LineShape</h1> Creates a Line-object.
 * Concrete class which extends Shape, representing a line.
 * @author 	Emma Pesjak
 * @version 1.0
 * @since 	2023-09-07
 */
public class LineShape extends Shape {
    private Point startPoint;
    private Point endPoint;
    private final Color color;
    private final int thickness;

    /**
     * Constructor that sets the color and thickness of the line.
     * @param color is the color.
     * @param thickness is the thickness.
     */
    public LineShape(Color color, int thickness) {
        this.color = color;
        this.thickness = thickness;
    }

    /**
     * Accessor for setting the start point.
     * @param startPoint is the start point.
     */
    public void setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
    }

    /**
     *  Accessor for setting the end point.
     * @param endPoint is the end point.
     */
    public void setEndPoint(Point endPoint) {
        this.endPoint = endPoint;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw(Graphics g) {
        if (startPoint != null && endPoint != null) {
            // Cast to Graphics2D to be able to set the stroke size.
            Graphics2D g2d = (Graphics2D) g;

            // Set the color and stroke (thickness) for drawing.
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(thickness));

            // Draw the line using the start and end points.
            g2d.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
        }
    }
}
