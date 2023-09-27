package se.miun.dt176g.xxxxyyyy.reactive;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Drawing</h1> 
 * Stores an arbitrary number of AbstractShape-objects in
 * a list container.
 * @author 	Emma Pesjak
 * @version 1.0
 * @since 	2023-09-27
 */
public class Drawing implements Drawable, Serializable {
	private static final long serialVersionUID = 1L;
	private final List<Shape> shapes;

	/**
	 * Constructor that creates the list container.
	 */
	public Drawing() {
		shapes = new ArrayList<>();
	}

	/**
	 * Clear the list of shapes.
	 */
	public void clear() {
		shapes.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addShape(Shape shape) {
		if (shape != null) {
			shapes.add(shape);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Shape> getShapes() {
		return shapes;
	}
}
