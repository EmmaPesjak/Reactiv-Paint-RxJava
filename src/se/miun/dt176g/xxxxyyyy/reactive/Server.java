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
import java.util.List;

/**
 * <h1>Server</h1>
 * //Incoming connections (receiving drawing events/objects from others over the network) should be represented as Observables.
 *     //Outgoing connections (sending drawing events/objects to others over the network) should be represented as Observers.
 * @author 	Emma Pesjak
 * @version 1.0
 * @since 	2023-09-27
 */
public class Server implements ConnectionHandler, Serializable {

    private final Drawing drawing = new Drawing();
    private MainFrame mainFrame;
    private ServerSocket serverSocket;
    private boolean acceptConnections = true;
    public DrawingPanel drawingPanel;
    public static Menu menu = new Menu();
    private static final long serialVersionUID = 1L;
    private List<Socket> clientSockets = new ArrayList<>();
    private CompositeDisposable disposables = new CompositeDisposable();

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

    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public DrawingPanel getDrawingPanel() {
        return drawingPanel;
    }

    public void sendShapeToClients(Shape shape) {  // här får jag ju inte skicka till baka dit det kom från


        for (Socket clientSocket : clientSockets) {
            try {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                objectOutputStream.writeObject(shape);
                objectOutputStream.flush();
                System.out.println("Sent shape to the client: " + shape);

                //objectOutputStream.reset();


            } catch (IOException e) {
                e.printStackTrace();
            }
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
                    // Handle the received shape here
                    // For example, add it to the drawing and broadcast it to other clients
                    System.out.println("Received shape from client: " + shape);

                    drawReceivedShape(shape); // Draw the received shape.

                    // Send the shape to all clients (excluding the sender)
                    for (Socket clientSocket : clientSockets) {
                        if (clientSocket != socket) {
                            try {
                                ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                                objectOutputStream.writeObject(shape);
                                objectOutputStream.flush();
                                System.out.println("Sent shape to client: " + shape);
                            } catch (IOException e) {
                                e.printStackTrace();
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

            // Subscribe this client's observer to the observable
            clientDrawingEvents
                    .observeOn(Schedulers.io())
                    .subscribe(clientObserver);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void drawReceivedShape(Shape shape) {
        SwingUtilities.invokeLater(() -> {
            drawing.addShape(shape);
            drawingPanel.repaint();
        });
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