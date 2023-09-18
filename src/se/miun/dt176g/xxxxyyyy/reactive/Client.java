package se.miun.dt176g.xxxxyyyy.reactive;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * <h1>Client</h1>
 *
 * @author 	Emma Pesjak
 * @version 1.0
 * @since 	2023-09-18
 */
public class Client {

    //Incoming connections (receiving drawing events/objects from others over the network) should be represented as Observables.
    //Outgoing connections (sending drawing events/objects to others over the network) should be represented as Observers.

    private Socket socket;
    private ObjectOutputStream outputStream;
    private MainFrame mainFrame;

    public Client(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public static void main(String[] args) {

        MainFrame frame = new MainFrame(true);
        frame.setStatusMessage("You are a client, please connect to a server");
        frame.setUpConnectButton();
        // Make sure GUI is created on the event dispatching thread.
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }

    public void connectToServer() {
        try {
            socket = new Socket("localhost", 12345);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            System.out.println(" hej client");
            mainFrame.setStatusMessage("Connected to the server as a client");


        } catch (IOException e) {
            mainFrame.setUpFailedToConnect();
            e.printStackTrace();
        }
    }
}
