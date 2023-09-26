package se.miun.dt176g.xxxxyyyy.reactive;

import se.miun.dt176g.xxxxyyyy.reactive.support.Constants;

import java.awt.*;
import javax.swing.*;

/**
 * <h1>MainFrame</h1>
 * JFrame for the application's GUI.
 * @author 	Emma Pesjak
 * @version 1.0
 * @since 	2023-09-25
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {
	private final JLabel statusLabel;  // Label for the status message.
	private final JPanel contentPanel = new JPanel();
	private JButton connectButton;
	private Client client;

	/**
	 * Constructor setting the layout and interface.
	 */
	public MainFrame(ConnectionHandler connectionHandler, Menu menu) {
		this.setSize(1200, 900);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle(Constants.TITLE);
		this.setLayout(new BorderLayout());
		// Create all necessary objects and adds them to the content panel.
		contentPanel.setLayout(new BorderLayout());
		this.add(contentPanel, BorderLayout.CENTER);
		// Create the status label and add it to the bottom of the frame.
		statusLabel = new JLabel();
		this.add(statusLabel, BorderLayout.SOUTH);

		initializeUI(connectionHandler, menu);
	}

	/**
	 * Initializes the user interface based on the given type (client/server).
	 * @param connectionHandler is the connection handler to initialize the UI for.
	 */
	private void initializeUI(ConnectionHandler connectionHandler, Menu menu) {
		// Add a menu.
		this.add(menu, BorderLayout.NORTH);
		if (connectionHandler instanceof Client) {
			initializeClientUI((Client) connectionHandler);
		} else if (connectionHandler instanceof DrawingServer) {
			initializeServerUI((DrawingServer) connectionHandler);
		}
	}

	/**
	 * Initializes the client user interface.
	 * @param client is the client instance for which to set up the UI.
	 */
	private void initializeClientUI(Client client) {
		this.client = client;
		setUpConnectButton();
		setStatusMessage(Constants.CLIENT_START_MSG);
	}

	/**
	 * Initializes the server user interface.
	 * @param server is the server instance for which to set up the UI.
	 */
	private void initializeServerUI(DrawingServer server) {
		setUpDrawing(server.getDrawingPanel());
		setStatusMessage(Constants.SERVER);
	}

	/**
	 * Sets up the drawing panel within the user interface.
	 * @param //drawing is the drawing canvas/container to display in the panel.
	 */
	public void setUpDrawing(DrawingPanel drawingPanel) {
		contentPanel.add(drawingPanel, BorderLayout.CENTER);
	}

	/**
	 * Sets up the connect button within the user interface.
	 */
	public void setUpConnectButton() {
		connectButton = new JButton(Constants.CONNECT_BTN);
		// Add an ActionListener to the button for handling the connection to the server.
		connectButton.addActionListener(e -> {
			SwingUtilities.invokeLater(this::connectToServerAndGetDrawing);  //Make sure it's executed on the EDT.
		});

		contentPanel.add(connectButton, BorderLayout.CENTER);
	}

	/**
	 * Initiates the process of connecting to the server and obtaining the drawing.
	 * Removes the connect button and any possible error text from the content panel.
	 */
	private void connectToServerAndGetDrawing() {
		// Remove the button and possible error text from the content panel.
		contentPanel.removeAll();

		client.connectToServer();
		contentPanel.revalidate();
		contentPanel.repaint();
	}

	/**
	 * Changes the server status message on the screen,
	 * @param message is the message to be displayed.
	 */
	public void setStatusMessage(String message) {
		statusLabel.setText(Constants.STATUS_MSG + message);
	}

	/**
	 * Sets up the user interface to display a "Failed to Connect" message.
	 * This method is typically called when a connection attempt fails.
	 */
	public void setUpFailedToConnect() {
		JPanel panel = new JPanel();
		JLabel failText = new JLabel(Constants.FAIL_CONNECT_MSG);
		failText.setFont(new Font("Arial", Font.PLAIN, 24));
		panel.add(failText);
		contentPanel.add(panel, BorderLayout.CENTER);

		connectButton = new JButton(Constants.CONNECT_BTN);
		// Add an ActionListener to the button for handling the connection to the server.
		connectButton.addActionListener(e -> connectToServerAndGetDrawing());
		contentPanel.add(connectButton, BorderLayout.SOUTH);
	}
}
