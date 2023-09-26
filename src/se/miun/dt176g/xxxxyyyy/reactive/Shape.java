package se.miun.dt176g.xxxxyyyy.reactive;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Shape</h1> Abstract class which derived classes builds on.
 * This class consists of the attributes common to all geometric shapes.
 * Specific shapes are based on this class.
 * @author 	Emma Pesjak
 * @version 1.0
 * @since 	2023-09-07
 */
public abstract class Shape implements Drawable, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Method for drawing the Shape.
     * @param g is the graphics.
     */
    public abstract void draw(Graphics g);

    /**
     * {@inheritDoc}
     */
    @Override
    public void addShape(Shape shape) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Shape> getShapes() {
        return new ArrayList<>();
    }
}
