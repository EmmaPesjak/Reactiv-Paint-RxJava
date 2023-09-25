package se.miun.dt176g.xxxxyyyy.reactive;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import se.miun.dt176g.xxxxyyyy.reactive.support.Constants;

import javax.swing.*;
import java.io.IOException;
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
public class DrawingServer implements ConnectionHandler {

    private List<Client> clients = new ArrayList<>();
    private Drawing drawing = new Drawing();

    public void addClient(Client client) {
        clients.add(client);
    }

    public void receiveDrawingUpdate(Client sender, Shape shape) {
        // Handle the drawing update received from a specific client (sender).
        // You can add logic here to validate and process the update.

        // For example, you might want to ensure that the sender is authorized
        // to make changes to the drawing, and then add the shape to the drawing.

        // For simplicity, we assume all received shapes are valid and directly add them.
        drawing.addShape(shape);

        // Broadcast the drawing update to all clients, including the sender.
        broadcastDrawingUpdate(shape);
    }

    public void broadcastDrawingUpdate(Shape shape) {
        // Iterate through connected clients and send the drawing update to each client.
        for (Client client : clients) {
            client.sendDrawingUpdate(shape);
        }
    }

    private MainFrame mainFrame;
    private ServerSocket serverSocket;
    //private final Drawing drawing = new Drawing();

    private PublishSubject<Shape> drawingUpdates = PublishSubject.create();

    private Subject<Socket> connections = PublishSubject.create(); // Initialize the connections Subject


    private Subject<Shape> shapeStream;   //all shapes from all clients fast detta ska in i min drawing?
    // drawingen ska delas på någon vänster?

    // ska man ha någon lista med alla klienter?
    //private List<Client> clients = new ArrayList<>();

    private boolean acceptConnections = true;


    // TODO servern är också en klient typ

    public void drawShape(Shape shape) {
        drawingUpdates.onNext(shape);
    }


    public DrawingServer() {
        try {
            serverSocket = new ServerSocket(Constants.PORT);
//            while (acceptConnections) {
//                Socket socket = serverSocket.accept();
//                Observable.<Socket>create(emitter -> emitter.onNext(socket))
//                        .observeOn(Schedulers.io())
//                        .subscribe(connections);
//            }

        } catch (IOException e) {
            mainFrame.setStatusMessage(Constants.FAIL_HOST_MSG);
            e.printStackTrace();
        }
    }

    public void startServer() {

        Thread serverThread = new Thread(() -> {
            try {
                while (acceptConnections) {
                    Socket socket = serverSocket.accept();
                    Observable.<Socket>create(emitter -> emitter.onNext(socket))  // detta vetekatten för det tog jag från exemplet
                            .observeOn(Schedulers.io())
                            .subscribe(connections);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //TODO här får jag massa fel

        serverThread.start();


        // Subscribe to incoming connections
        connections.subscribe(
                socket -> {
                    // Handle the incoming socket connection here
                    // For example, you can create a new Client instance for each connection
                    // and manage them in your `clients` list.
                    //Client newClient = new Client(socket);
                    //clients.add(newClient);
                    // You can also emit events or data to other Observables or Subjects
                },
                error -> {
                    // Handle any errors that occur while accepting connections
                    error.printStackTrace();
                }
        );
    }

    private void shutdown() {
        acceptConnections = false;
    }

    /**
     * Main starting point of the server side of the application.
     * @param args not applicable here.
     */
    public static void main(String[] args) {

        DrawingServer server = new DrawingServer(); // Create an instance of DrawingServer.

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(server); // Pass the server instance to MainFrame
            frame.setVisible(true);
            server.setMainFrame(frame);
            server.startServer();
        });

        // Make sure GUI is created on the event dispatching thread.
        //SwingUtilities.invokeLater(() -> frame.setVisible(true));

    }

    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    // vet inte riktigt vad jag pysslar med här
    // vad händer om någon DCar?
    // vad händer med clienten om servern DCar?
    // stänga sockets och observables?
    // vad händer vid error?

    //onNext
    //onError
    //onComplete


    // Kap 5 learning rxJava om multicasting tar upp mycket bra om hur man ska kunna skicka så alla observers får all info typ samtidigt och i bra ordning.
    // Kap 6 s189 Using observeOn() for UI event threads
    // "The visual updating of user interfaces is often done by a single dedicated UI
    //thread, and changes to the user interface must be done on that thread. User input events are
    //typically fired on the UI thread as well. If a user input triggers work, and that work is not
    //moved to another thread, that UI thread becomes busy. This is what makes the user
    //interface unresponsive, and today's users expect better than this. They want to continue
    //interacting with the application while work is happening in the background, so
    //concurrency is a must-have.
    //Thankfully, RxJava comes to the rescue! You can use observeOn() to move UI events to a
    //computation or I/O Scheduler to do the work, and when the result is ready, move it back
    //to the UI thread with another observeOn()."



    public Drawing getDrawing() {
        return drawing;
    }
}
