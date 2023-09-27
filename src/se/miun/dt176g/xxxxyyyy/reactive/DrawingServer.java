package se.miun.dt176g.xxxxyyyy.reactive;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import se.miun.dt176g.xxxxyyyy.reactive.support.Constants;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1>DrawingServer</h1>
 * //Incoming connections (receiving drawing events/objects from others over the network) should be represented as Observables.
 *     //Outgoing connections (sending drawing events/objects to others over the network) should be represented as Observers.
 * @author 	Emma Pesjak
 * @version 1.0
 * @since 	2023-09-25
 */
public class DrawingServer implements ConnectionHandler, Serializable {

    private final Drawing drawing = new Drawing();
    private MainFrame mainFrame;
    private ServerSocket serverSocket;
    private boolean acceptConnections = true;
    public DrawingPanel drawingPanel;
    public static Menu menu = new Menu();
    private static final long serialVersionUID = 1L;
    private List<Socket> clientSockets = new ArrayList<>();

    

    private CompositeDisposable disposables = new CompositeDisposable();

    /**
     * Main starting point of the server side of the application.
     * @param args not applicable here.
     */
    public static void main(String[] args) {
        DrawingServer server = new DrawingServer(); // Create an instance of DrawingServer.
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(server, menu); // Pass the server instance to MainFrame
            frame.setVisible(true);
            server.setMainFrame(frame);
            server.startServer();
        });
    }
    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public DrawingPanel getDrawingPanel() {
        return drawingPanel;
    }

    public void sendShapeToClients(Shape shape) {
        try {
            // Send the shape to all connected clients
            for (Socket clientSocket : clientSockets) {

                // here is where I potentially want to send the shape!

                System.out.println("Sent shape to the client: " + shape);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DrawingServer() {
        try {
            drawingPanel = new DrawingPanel(drawing, menu);
            drawingPanel.setServer(this);
            serverSocket = new ServerSocket(Constants.PORT);

        } catch (IOException e) {
            handleServerSocketError(e);
        }
    }

    private void handleServerSocketError(IOException e) {
        // Handle server socket errors, e.g., log and display an error message
        e.printStackTrace();
        mainFrame.setStatusMessage(Constants.FAIL_HOST_MSG);
    }

    private void handleIncomingConnection(Socket socket) {

        try {
            ObjectOutputStream clientOutputStream = new ObjectOutputStream(socket.getOutputStream());
            clientSockets.add(socket);
            clientOutputStream.flush(); // Flush the output stream
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // Create an ObjectInputStream to read objects from the client
            ObjectInputStream clientInputStream = new ObjectInputStream(socket.getInputStream());

            // Create an Observable for incoming drawing events
            Observable<Shape> clientDrawingEvents = Observable.create(emitter -> {
                while (!emitter.isDisposed()) {
                    Shape receivedShape = (Shape) clientInputStream.readObject();

                    // Emit the received shape to subscribers
                    System.out.println("Emitting received shape to subscribers: " + receivedShape); // Debug statement
                    emitter.onNext(receivedShape);
                }
            });
            System.out.println("Subscribing to clientDrawingEvents observable");
            // Subscribe to the Observable on the io() scheduler for blocking I/O
            clientDrawingEvents
                    .observeOn(Schedulers.io())
                    .subscribe(
                            shape -> {
                                // Handle the received shape here
                                // For example, add it to the drawing and broadcast it to other clients
                                //receiveDrawingUpdate(shape);
                                // Print the received shape to the console
                                System.out.println("Received shape from client: " + shape);

                                // TODO herrejävlar här kom det en shape
                            },
                            error -> {
                                // Handle errors, e.g., communication or deserialization errors
                                error.printStackTrace();
                            }
                    );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startServer() {
        Thread serverThread = new Thread(() -> {
            try {
                while (acceptConnections) {
                    Socket socket = serverSocket.accept();
                    handleIncomingConnection(socket);
                }
            } catch (IOException e) {
                //e.printStackTrace();
                handleServerSocketError(e);
            }
        });
        serverThread.start();

    }

    private void shutdown() {
        acceptConnections = false;
    }

    public void stop() {
        acceptConnections = false;
        disposables.dispose(); // Dispose of all RxJava resources

        // Close serverSocket and other resources
        try {
            serverSocket.close();
        } catch (IOException e) {
            handleServerSocketError(e);
        }
    }
}