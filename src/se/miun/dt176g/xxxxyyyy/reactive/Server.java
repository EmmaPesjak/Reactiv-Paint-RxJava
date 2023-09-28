package se.miun.dt176g.xxxxyyyy.reactive;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import se.miun.dt176g.xxxxyyyy.reactive.support.Constants;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h1>Server</h1>
 * Represents the server-side of the application for handling incoming and outgoing connections/drawing events.
 * @author 	Emma Pesjak
 * @version 1.0
 * @since 	2023-09-28
 */
public class Server implements ConnectionHandler, Serializable {

    private final Drawing drawing = new Drawing();
    private MainFrame mainFrame;
    private ServerSocket serverSocket;
    private boolean acceptConnections = true;
    public DrawingPanel drawingPanel;
    public static Menu menu = new Menu();
    private static final long serialVersionUID = 1L;
    private final List<Socket> clientSockets = new ArrayList<>();
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final Map<Socket, ObjectOutputStream> clientOutputStreams = new HashMap<>();

    /**
     * Constructor which sets the DrawingPanel and ServerSocket.
     */
    public Server() {
        try {
            drawingPanel = new DrawingPanel(drawing, menu, this);
            serverSocket = new ServerSocket(Constants.PORT);
        } catch (IOException e) {
            handleServerSocketError(e);
        }
    }

    /**
     * Main starting point of the server side of the application.
     * @param args not applicable here.
     */
    public static void main(String[] args) {
        Server server = new Server(); // Create an instance of Server.
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(server, menu); // Pass the server instance to MainFrame
            frame.setVisible(true);
            server.setMainFrame(frame);
            server.startServer();
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    /**
     * Getter for the server's DrawingPanel.
     * @return the DrawingPanel.
     */
    public DrawingPanel getDrawingPanel() {
        return drawingPanel;
    }

    /**
     * Sends a Shape to all connected clients, used when the Server is drawing.
     * @param shape the Shape to send.
     */
    public void sendShapeToClients(Shape shape) {
        for (ObjectOutputStream outputStream : clientOutputStreams.values()) {
            try {
                outputStream.writeObject(shape);
                outputStream.flush(); // Essential to ensure that data is delivered promptly and consistently to its destination.
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * Handle server socket errors, logs and displays an error message
     * @param e is the exception
     */
    private void handleServerSocketError(IOException e) {
        e.printStackTrace();
        mainFrame.setStatusMessage(Constants.FAIL_HOST_MSG);
    }

    //TODO oj jag har ju glömt att klienter som connectar måste få allt som redan finns i drawingen! Läste något bra i boken om det någonstans

    private void handleIncomingConnection(Socket socket) {

        try {
            clientSockets.add(socket);
            ObjectOutputStream clientOutputStream = new ObjectOutputStream(socket.getOutputStream());
            clientOutputStreams.put(socket, clientOutputStream); // Store the stream for this client
            clientOutputStream.flush(); // Flush the output stream


            // Create an ObjectInputStream to read objects from the client
            ObjectInputStream clientInputStream = new ObjectInputStream(socket.getInputStream());

            // Create an Observable for incoming drawing events
            Observable<Shape> clientDrawingEvents = Observable.create(emitter -> {
                while (!emitter.isDisposed()) {
                    Shape receivedShape = (Shape) clientInputStream.readObject();

                    // Emit the received shape to subscribers
                    emitter.onNext(receivedShape);
                }
            });

            // Create an observer for this client
            Observer<Shape> clientObserver = new Observer<Shape>() {
                @Override
                public void onSubscribe(Disposable d) {
                    // Nothing needed here
                }

                @Override
                public void onNext(Shape shape) {
                    drawReceivedShape(shape); // Draw the received shape.
                    // detta är ju nästan samma som sendShapeToClients(Shape shape) men jag behöver socket :(
                    for (Socket clientSocket : clientSockets) {
                        if (clientSocket != socket) {
                            ObjectOutputStream objectOutputStream = clientOutputStreams.get(clientSocket);
                            if (objectOutputStream != null) {
                                try {
                                    objectOutputStream.writeObject(shape);
                                    objectOutputStream.flush();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

                @Override
                public void onError(Throwable e) {
                    // Handle errors, if any
                    e.printStackTrace();
                }

                @Override
                public void onComplete() {
                    // Handle completion, if needed
                }
            };

            // Subscribe this client's observer to the observable.
            clientDrawingEvents
                    .observeOn(Schedulers.io())
                    .subscribe(clientObserver);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawReceivedShape(Shape shape) {
        SwingUtilities.invokeLater(() -> {
            drawing.addShape(shape);
            drawingPanel.repaint();
        });
    }

    /**
     * Starts the server by accepting incoming connections in a separate thread.
     */
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


    //TODO måste använda dessa någonstans
    /**
     * Shuts down the server, stopping it from accepting new connections.
     */
    private void shutdown() {
        acceptConnections = false;
    }

    /**
     * Stops the server, disposing of all resources including RxJava disposables.
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearEvent() {
        drawingPanel.clearDrawing();

        System.out.println("should clear");

        // TODO skicka till clienten att vi ska cleara
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