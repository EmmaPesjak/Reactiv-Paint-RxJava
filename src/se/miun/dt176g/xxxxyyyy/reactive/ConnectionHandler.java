package se.miun.dt176g.xxxxyyyy.reactive;

/**
 * <h1>ConnectionHandler</h1>
 * Interface for server/clients.
 * @author 	Emma Pesjak
 * @version 1.0
 * @since 	2023-09-28
 */
public interface ConnectionHandler {

    /**
     * For emitting that the Drawing should be cleared.
     */
    void clearEvent();

    /**
     * Draws a received shape on the canvas.
     * @param shape is the Shape to be drawn.
     */
    void drawReceivedShape(Shape shape);

    /**
     * Sets the MainFrame.
     * @param mainFrame is the MainFrame.
     */
    void setMainFrame(MainFrame mainFrame);
}
