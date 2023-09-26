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
    //public PublishSubject<Shape> drawingUpdates = PublishSubject.create();
    //private PublishSubject<Shape> serverDrawingUpdates = PublishSubject.create();
    //private Subject<Socket> connections = PublishSubject.create();
    private boolean acceptConnections = true;
    public DrawingPanel drawingPanel;
    public static Menu menu = new Menu();
    private static final long serialVersionUID = 1L;
    private List<Socket> clientSockets = new ArrayList<>();

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
    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public DrawingPanel getDrawingPanel() {
        return drawingPanel;
    }


    //TODO HÄR FÅR JAG FAN IN SHAPEN
    public void sendShapeToClients(Shape shape) {
        try {
            // Send the shape to all connected clients
            for (Socket clientSocket : clientSockets) {
//                ObjectOutputStream clientOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
//                clientOutputStream.writeObject(shape);
//                clientOutputStream.flush();
//                System.out.println("Sent shape to client: " + shape);

                //outputStream.writeObject(shape);
                System.out.println("Sent shape to the client: " + shape);
            }

            // Broadcast the shape to other clients
            // broadcastDrawingUpdate(shape);

            // Emit the drawing update to the serverDrawingUpdates subject
            //serverDrawingUpdates.onNext(shape);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //System.out.println("Received new shape: " + shape);
        //drawingUpdates.onNext(shape); //huh?
//        broadcastDrawingUpdate(shape);
//        serverDrawingUpdates.onNext(shape);
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



//    public Observable<Void> startHandling() {
//        return Observable.create(emitter -> {
//            try {
//                // Read and handle incoming drawing events from clients
//                while (!emitter.isDisposed()) {
//                    Shape receivedShape = (Shape) inputStream.readObject();
//                    receiveDrawingUpdate(receivedShape);
//                }
//            } catch (IOException | ClassNotFoundException e) {
//                emitter.onError(e);
//            }
//        });
//    }



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

//        // Subscribe to incoming connections
//        connections.subscribe(
//                socket -> {
//                    // Handle the incoming socket connection here
//                    // For example, you can create a new Client instance for each connection
//                    // and manage them in your `clients` list.
//                    //Client newClient = new Client(socket);
//                    //clients.add(newClient);
//                    // You can also emit events or data to other Observables or Subjects
//                    //clientSockets.add(socket);
//                },
//                error -> {
//                    // Handle any errors that occur while accepting connections
//                    error.printStackTrace();
//                }
//        );
    }

//    public void addClient(Client client) {
//        clients.add(client);
//    }
//
//    public void sendDrawingUpdate(Shape shape) {
//        try {
//            // Send a Shape object to the client
//            outputStream.writeObject(shape);
//        } catch (IOException e) {
//            // Handle sending error
//            e.printStackTrace();
//        }
//    }

//    public void receiveDrawingUpdate(Shape shape) {
//        drawing.addShape(shape);
//
//        // Emit the drawing update to the main drawingUpdates subject
//        //drawingUpdates.onNext(shape);
//
//        System.out.println("haaaallo");
//        // Emit the drawing update to the serverDrawingUpdates subject
//        serverDrawingUpdates.onNext(shape);
//    }

//    public void receiveDrawingUpdate(Shape shape) {
//        //drawing.addShape(shape);
//
//        System.out.println("server got a shape from the client: " + shape);
//        // Emit the drawing update to the serverDrawingUpdates subject
//        //serverDrawingUpdates.onNext(shape);
//    }




    // Modify the broadcastDrawingUpdate method to send drawing events to clients
//    public void broadcastDrawingUpdate(Shape shape) {
//        // Iterate through connected clients and send the drawing event to each client.
//
//
////        for (Client client : clients) {
////            client.sendDrawingUpdate(shape);
////            System.out.println("nix?");
////        }
//
//        for (Socket clientSocket : clientSockets) {
//            //sendDrawingUpdate(clientSocket, shape);
//            sendDrawingEvents();
//            System.out.println("sending shape:" + shape);
//            // Emit the drawing update to the serverDrawingUpdates subject
//            serverDrawingUpdates.onNext(shape);
//        }
//    }

    // Define a method to send a drawing update to a specific client socket
//    public void sendDrawingUpdate(Socket clientSocket, Shape shape) {
//        try {
//            ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
//            outputStream.writeObject(shape);
//        } catch (IOException e) {
//            // Handle the exception (e.g., log it or take appropriate action)
//            e.printStackTrace();
//        }
//    }

//    // Define a method to handle drawing updates received from clients
//    public void handleDrawingUpdate(Shape shape) {
//        // Handle the received drawing event here
//        // For example, you can add it to the drawing and broadcast it to other clients.
//        drawing.addShape(shape);
//        //broadcastDrawingUpdate(shape); vet ej????
//    }

    // Create an Observer to represent sending drawing events to clients
//    public Observer<Shape> sendDrawingEvents() {
//
//        System.out.println("haaaallo2");
//
//        System.out.println(serverDrawingUpdates);
//        return serverDrawingUpdates;
//    }

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



//    // Create an Observable to represent receiving drawing events from clients
//    public Observable<Shape> receiveDrawingEvents() {
//        return drawingUpdates;
//    }

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