package se.miun.dt176g.xxxxyyyy.reactive;

import java.util.List;

/**
 * <h1>Drawable</h1>
 * Drawable interface.
 * @author 	Emma Pesjak
 * @version 1.0
 * @since 	2022-09-07
 */
public interface Drawable {
	//void draw(java.awt.Graphics g); // detta hade peter men jag gör ju litta annat så kan man ta bort?

	/**
	 * Adds a shape to the shapes list.
	 * @param shape a {@link Shape} object.
	 */
	void addShape(Shape shape);

	/**
	 * Removes a shape from the shapes list.
	 * @param shape a {@link Shape} object.
	 */
	void removeShape(Shape shape);  // behöver jag detta när jag ska cleara??

	/**
	 * Getter for the list of shapes.
	 * @return the list of Shapes.
	 */
	List<Shape> getShapes();
}
