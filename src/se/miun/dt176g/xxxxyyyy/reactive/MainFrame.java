package se.miun.dt176g.xxxxyyyy.reactive;

import java.awt.*;
import javax.swing.*;

/**
 * <h1>MainFrame</h1> 
 * JFrame for the applications GUI.
 * @author 	Emma Pesjak
 * @version 1.0
 * @since 	2023-09-18
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {
	private DrawingPanel drawingPanel;
	private final JLabel statusLabel;  // Label for the status message
	//private Drawing drawing;
	private final Menu menu;
	private final JPanel contentPanel = new JPanel();
	private JButton connectButton;
	private Client client;
	private DrawingServer server;
//
	/**
	 * Constructor setting the layout and interface.
	 */
	public MainFrame(boolean isClient) {
		this.setSize(1200, 900);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Reactive Paint");
		this.setLayout(new BorderLayout());

		// Create the menu and add it to the top of the frame.
		menu = new Menu(this);
		this.add(menu, BorderLayout.NORTH);

		// Create all necessary objects and adds them to the content panel.

		contentPanel.setLayout(new BorderLayout());

//		Drawing drawing = new Drawing(); // TODO detta gör ju att det blir olika drawings
//		drawingPanel = new DrawingPanel(drawing, menu);
//		contentPanel.add(drawingPanel, BorderLayout.CENTER);

		// Add the content panel to the center of the frame.
		this.add(contentPanel, BorderLayout.CENTER);

		// Create the server status label and add it to the bottom of the frame.
		statusLabel = new JLabel("Status: borde aldrig synas");
		this.add(statusLabel, BorderLayout.SOUTH);


		if (isClient) {
			client = new Client(this); // Pass the MainFrame instance to the Client
			setUpConnectButton(); // Set up the Connect button
			setStatusMessage("You are a client");
		} else {
			server = new DrawingServer(this); // Pass the MainFrame instance to the Server
			setUpDrawing(server.getDrawing()); // Start the server
			setStatusMessage("You are running the server");
		}

	}

	public void setUpDrawing(Drawing drawing) {
		drawingPanel = new DrawingPanel(drawing, menu);
		contentPanel.add(drawingPanel, BorderLayout.CENTER);
	}

	public void setUpFailedToConnect() {
		JPanel panel = new JPanel();
		JLabel failText = new JLabel("Failed to connect to a server, make sure one is running.");
		failText.setFont(new Font("Arial", Font.PLAIN, 24));
		panel.add(failText);
		contentPanel.add(panel, BorderLayout.CENTER);

		//TODO kopierat från setUpConnectButton() men med borderlayout south, gör en snyggare lösning
		connectButton = new JButton("Connect to server");

		// Add an ActionListener to the button for handling the connection to the server.
		connectButton.addActionListener(e -> connectToServerAndGetDrawing());

		contentPanel.add(connectButton, BorderLayout.SOUTH);
	}

	public void setUpConnectButton() {
		connectButton = new JButton("Connect to server");

		// Add an ActionListener to the button for handling the connection to the server.
		connectButton.addActionListener(e -> connectToServerAndGetDrawing());

		contentPanel.add(connectButton, BorderLayout.CENTER);
	}

	public void connectToServerAndGetDrawing() {
		System.out.println("connecting");

		// Remove the button and possible error text from the content panel.
		contentPanel.removeAll();

		// TODO här ska det connecta och så få drawingen på någon vänster så man kan
		//setUpDrawing(drawing);
		client.connectToServer();

		// Repaint the content panel to reflect the changes
		contentPanel.revalidate();
		contentPanel.repaint();
	}

	/**
	 * Changes the server status message on the screen,
	 * @param message is the message to be displayed.
	 */
	public void setStatusMessage(String message) {
		statusLabel.setText("Status: " + message);
	}

	/**
	 * Clears the frame by removing the shapes drawn.
	 */
	public void clearFrame() {
		drawingPanel.clearDrawing();
	}
}
