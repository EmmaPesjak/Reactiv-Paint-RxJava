package se.miun.dt176g.xxxxyyyy.reactive;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import se.miun.dt176g.xxxxyyyy.reactive.support.Constants;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

/**
 * <h1>Client</h1>
 * //Incoming connections(receiving drawing events/objects from others over the network)should be represented as Observables.
 * @author 	Emma Pesjak
 * @version 1.0
 * @since 	2023-09-27
 */
public class Client implements ConnectionHandler, Serializable {
    private Socket socket;
    private MainFrame mainFrame;
    private static final Menu menu = new Menu();
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private DrawingPanel drawingPanel;
    private static final long serialVersionUID = 1L;

    public static void main(String[] args) {
        // Create an instance of Client.
        Client client = new Client();
        // Create an instance of MainFrame and pass the client instance.
        MainFrame frame = new MainFrame(client, menu);
        // Set the client instance for the created client object.
        client.setMainFrame(frame);
        // Make sure GUI is created on the event dispatching thread.
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }

    public void setMainFrame(MainFrame frame) {
        this.mainFrame = frame;
    }

    public void connectToServer() {
        try {
            socket = new Socket(Constants.ADDRESS, Constants.PORT);

            Drawing drawing = new Drawing(); //TODO Inte ny drawing ju
            drawingPanel = new DrawingPanel(drawing, menu, this);

            mainFrame.setUpDrawing(drawingPanel);
            mainFrame.setStatusMessage(Constants.CLIENT_CONNECT_MSG);

            // Initialize the outputStream
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());

        } catch (IOException e) {
            mainFrame.setUpFailedToConnect();
            e.printStackTrace();
        }
    }

    // HÄR FÅR JAG FAN IN SHAPEN NÄR DEN RITAS!!
    // method to handle new shapes received from DrawingPanel
    public void sendShapeToServer(Shape shape) {
        try {
            outputStream.writeObject(shape);
            System.out.println("Sent shape to the server: " + shape);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearEvent() {
        drawingPanel.clearDrawing();

        System.out.println("should clear");

        //TODO skicka till servern att vi ska cleara
    }
}
