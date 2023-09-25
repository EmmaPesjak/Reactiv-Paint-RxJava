package se.miun.dt176g.xxxxyyyy.reactive;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import se.miun.dt176g.xxxxyyyy.reactive.support.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

/**
 * <h1>Client</h1>
 * //Incoming connections(receiving drawing events/objects from others over the network)should be represented as Observables.
 * @author 	Emma Pesjak
 * @version 1.0
 * @since 	2023-09-25
 */
public class Client implements ConnectionHandler {
    private Socket socket;
    private MainFrame mainFrame;
    private DrawingServer server;

    private PublishSubject<Shape> drawingUpdates = PublishSubject.create();

    public void drawShape(Shape shape) {
        drawingUpdates.onNext(shape);
    }

    public static void main(String[] args) {
        // Create an instance of Client.
        Client client = new Client();
        // Create an instance of MainFrame and pass the client instance.
        MainFrame frame = new MainFrame(client);
        // Set the client instance for the created client object.
        client.setMainFrame(frame);
        // Make sure GUI is created on the event dispatching thread.
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }

    public void sendDrawingUpdate(Shape shape) {
        // Send the drawing update to the server.
        server.receiveDrawingUpdate(this, shape);
    }

    public void setMainFrame(MainFrame frame) {
        this.mainFrame = frame;
    }

    public void connectToServer() {
        try {
            socket = new Socket(Constants.ADDRESS, Constants.PORT);


            // kirra alla gamla drawings, det kan man ju kanske göra i Observablen nedan
            mainFrame.setUpDrawing(new Drawing()); //TODO fixa så denna inte blir ny.

            mainFrame.setStatusMessage(Constants.CLIENT_CONNECT_MSG);


            Observable.just("Alla drawing events");
            //TODO IN MED LOGIK HÄR



        } catch (IOException e) {
            mainFrame.setUpFailedToConnect();
            e.printStackTrace();
        }
    }
}
