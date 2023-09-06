package se.miun.dt176g.xxxxyyyy.reactive;

import javax.swing.SwingUtilities;

/**
* <h1>AppStart</h1>
* The start of the application
* @author  Emma Pesjak
* @version 1.0
* @since   2023-09-06
*/
public class AppStart {

	public static void main(String[] args) {
		
		// Make sure GUI is created on the event dispatching thread.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MainFrame().setVisible(true);
			}
		});
	}
}