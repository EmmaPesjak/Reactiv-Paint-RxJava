package se.miun.dt176g.xxxxyyyy.reactive;

import se.miun.dt176g.xxxxyyyy.reactive.support.Constants;

import java.awt.*;
import javax.swing.*;

/**
 * <h1>MainFrame</h1> 
 * JFrame for the applications GUI.
 * @author 	Emma Pesjak
 * @version 1.0
 * @since 	2023-09-19
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
		this.setTitle(Constants.TITLE);
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
		statusLabel = new JLabel();
		this.add(statusLabel, BorderLayout.SOUTH);

	// TODO jag ska ju inte skapa nya här?? varför gör jag sånt dumt??
		if (isClient) {
			client = new Client(this); // Pass the MainFrame instance to the Client
			setUpConnectButton(); // Set up the Connect button
			setStatusMessage(Constants.CLIENT_START_MSG);
		} else {
			server = new DrawingServer(this); // Pass the MainFrame instance to the Server
			//server.startServer();
			setUpDrawing(server.getDrawing()); // Start the server
			setStatusMessage(Constants.SERVER);
		}

	}

	public void setUpDrawing(Drawing drawing) {
		drawingPanel = new DrawingPanel(drawing, menu);
		contentPanel.add(drawingPanel, BorderLayout.CENTER);
	}

	public void setUpFailedToConnect() {
		JPanel panel = new JPanel();

		JLabel failText = new JLabel(Constants.FAIL_CONNECT_MSG);
		failText.setFont(new Font("Arial", Font.PLAIN, 24));
		panel.add(failText);
		contentPanel.add(panel, BorderLayout.CENTER);

		//TODO kopierat från setUpConnectButton() men med borderlayout south, gör en snyggare lösning
		connectButton = new JButton(Constants.CONNECT_BTN);
		// Add an ActionListener to the button for handling the connection to the server.
		connectButton.addActionListener(e -> connectToServerAndGetDrawing());
		contentPanel.add(connectButton, BorderLayout.SOUTH);
	}

	private void setUpConnectButton() {
		connectButton = new JButton(Constants.CONNECT_BTN);

		// Add an ActionListener to the button for handling the connection to the server.
		connectButton.addActionListener(e -> {
			SwingUtilities.invokeLater(this::connectToServerAndGetDrawing);  //Make sure it's executed on the EDT.
		});

		contentPanel.add(connectButton, BorderLayout.CENTER);
	}

	private void connectToServerAndGetDrawing() {
		// Remove the button and possible error text from the content panel.
		contentPanel.removeAll();

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
		statusLabel.setText(Constants.STATUS_MSG + message);
	}

	/**
	 * Clears the frame by removing the shapes drawn.
	 */
	public void clearFrame() {
		drawingPanel.clearDrawing();
	}
}
