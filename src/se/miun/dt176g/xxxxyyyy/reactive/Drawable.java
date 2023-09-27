package se.miun.dt176g.xxxxyyyy.reactive;

import java.util.List;

/**
 * <h1>Drawable</h1>
 * Drawable interface.
 * @author 	Emma Pesjak
 * @version 1.0
 * @since 	2022-09-27
 */
public interface Drawable {

	/**
	 * Adds a shape to the shapes list.
	 * @param shape a {@link Shape} object.
	 */
	void addShape(Shape shape);

	/**
	 * Getter for the list of shapes.
	 * @return the list of Shapes.
	 */
	List<Shape> getShapes();
}
