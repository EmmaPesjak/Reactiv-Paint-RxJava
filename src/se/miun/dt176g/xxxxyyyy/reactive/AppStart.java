package se.miun.dt176g.xxxxyyyy.reactive;

import javax.swing.SwingUtilities;
import java.io.IOException;

/**
* <h1>AppStart</h1>
* The start of the application
* @author  Emma Pesjak
* @version 1.0
* @since   2023-09-07
*/
public class AppStart {

	/**
	 * Main starting point of the application.
	 * @param args not applicable here.
	 */
	public static void main(String[] args) {
		// Make sure GUI is created on the event dispatching thread.
		SwingUtilities.invokeLater(() -> {
			new MainFrame().setVisible(true);

			// Create and start the server in a separate thread.
			// Tänker att det ska man göra i en egen tråd?? för att inte kludda med Swing??
			Thread serverThread = new Thread(() -> {
				DrawingServer drawingServer = null;
				try {  // behövs denna try/catchen? intelliJ tvingar mig, kolla sen när koden är helt klar
					drawingServer = new DrawingServer();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				drawingServer.startServer();
			});
			serverThread.start();
		});
	}
}