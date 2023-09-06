package se.miun.dt176g.xxxxyyyy.reactive;


import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * <h1>Shape</h1> Abstract class which derived classes builds on.
 * <p>
 * This class consists of the attributes common to all geometric shapes.
 * Specific shapes are based on this class.
 * 
 * @author 	--YOUR NAME HERE--
 * @version 1.0
 * @since 	2022-09-08
 */

public abstract class Shape implements Drawable {

	// private member : some container storing coordinates
    // får fundera på vad det där betyder ^


    protected Color color;
    protected int thickness;

    public Shape(Color color, int thickness) {  // tänker att så här får man göra kanske
        this.color = color;
        this.thickness = thickness;
    }

    public abstract void handleMouseEvent(MouseEvent e);
    public abstract void draw(Graphics g);
	
}
