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

    private final List<Client> clients = new ArrayList<>();
    private final Drawing drawing = new Drawing();
    private MainFrame mainFrame;
    private ServerSocket serverSocket;
    public PublishSubject<Shape> drawingUpdates = PublishSubject.create();
    private PublishSubject<Shape> serverDrawingUpdates = PublishSubject.create();
    private Subject<Socket> connections = PublishSubject.create();
    private boolean acceptConnections = true;
    public DrawingPanel drawingPanel;
    public static Menu menu = new Menu();
    private static final long serialVersionUID = 1L;

    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    private Socket socket;

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

    public DrawingServer() {
        try {
            drawingPanel = new DrawingPanel(drawing, menu);
            drawingPanel.setServer(this);
            serverSocket = new ServerSocket(Constants.PORT);

        } catch (IOException e) {
            handleServerSocketError(e);
        }

//        try {
//            outputStream = new ObjectOutputStream(socket.getOutputStream());
//            inputStream = new ObjectInputStream(socket.getInputStream());
//        } catch (IOException e) {
//            // Handle socket initialization error
//            e.printStackTrace();
//        }
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

    private void handleServerSocketError(IOException e) {
        // Handle server socket errors, e.g., log and display an error message
        e.printStackTrace();
        mainFrame.setStatusMessage(Constants.FAIL_HOST_MSG);
    }

    private void handleIncomingConnection(Socket socket) {
        this.socket = socket; // Initialize the socket first


        try {
            ObjectOutputStream clientOutputStream = new ObjectOutputStream(socket.getOutputStream());
            clientSockets.add(socket);
            //clientOutputStream.writeObject("hello");
            clientOutputStream.flush(); // Flush the output stream
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Observable<Void> startHandling() {
        return Observable.create(emitter -> {
            try {
                // Read and handle incoming drawing events from clients
                while (!emitter.isDisposed()) {
                    Shape receivedShape = (Shape) inputStream.readObject();
                    receiveDrawingUpdate(receivedShape);
                }
            } catch (IOException | ClassNotFoundException e) {
                emitter.onError(e);
            }
        });
    }

    private List<Socket> clientSockets = new ArrayList<>();

    public void startServer() {
        Thread serverThread = new Thread(() -> {
            try {
                while (acceptConnections) {
                    Socket socket = serverSocket.accept();
//                    Observable.<Socket>create(emitter -> emitter.onNext(socket))  // detta vetekatten för det tog jag från exemplet
//                            .observeOn(Schedulers.io())
//                            .subscribe(connections);
                    handleIncomingConnection(socket);
                }
            } catch (IOException e) {
                //e.printStackTrace();
                handleServerSocketError(e);
            }
        });
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
                    //clientSockets.add(socket);
                },
                error -> {
                    // Handle any errors that occur while accepting connections
                    error.printStackTrace();
                }
        );
    }

    public void addClient(Client client) {
        clients.add(client);
    }

    public void sendDrawingUpdate(Shape shape) {
        try {
            // Send a Shape object to the client
            outputStream.writeObject(shape);
        } catch (IOException e) {
            // Handle sending error
            e.printStackTrace();
        }
    }

    public void receiveDrawingUpdate(Shape shape) {
        drawing.addShape(shape);

        // Emit the drawing update to the main drawingUpdates subject
        drawingUpdates.onNext(shape);

        // Emit the drawing update to the serverDrawingUpdates subject
        serverDrawingUpdates.onNext(shape);
    }

    //TODO HÄR FÅR JAG FAN IN SHAPEN
    public void drawShape(Shape shape) {
        System.out.println("Received new shape: " + shape);
        //drawingUpdates.onNext(shape); huh?
        broadcastDrawingUpdate(shape);
    }

    // Modify the broadcastDrawingUpdate method to send drawing events to clients
    public void broadcastDrawingUpdate(Shape shape) {
        // Iterate through connected clients and send the drawing event to each client.

        System.out.println("client len: " + clientSockets.size());

//        for (Client client : clients) {
//            client.sendDrawingUpdate(shape);
//            System.out.println("nix?");
//        }

        for (Socket clientSocket : clientSockets) {
            sendDrawingUpdate(clientSocket, shape);
            System.out.println("nix?");
        }
    }

    // Define a method to send a drawing update to a specific client socket
    public void sendDrawingUpdate(Socket clientSocket, Shape shape) {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            outputStream.writeObject(shape);
        } catch (IOException e) {
            // Handle the exception (e.g., log it or take appropriate action)
            e.printStackTrace();
        }
    }

    // Define a method to handle drawing updates received from clients
    public void handleDrawingUpdate(Shape shape) {
        // Handle the received drawing event here
        // For example, you can add it to the drawing and broadcast it to other clients.
        drawing.addShape(shape);
        //broadcastDrawingUpdate(shape); vet ej????
    }

    // Create an Observer to represent sending drawing events to clients
    public Observer<Shape> sendDrawingEvents() {
        return serverDrawingUpdates;
    }

    private void shutdown() {
        acceptConnections = false;
    }

    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public DrawingPanel getDrawingPanel() {
        return drawingPanel;
    }

    // Create an Observable to represent receiving drawing events from clients
    public Observable<Shape> receiveDrawingEvents() {
        return drawingUpdates;
    }

}

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