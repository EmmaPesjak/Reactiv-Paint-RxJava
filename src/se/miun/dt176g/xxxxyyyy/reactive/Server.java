package se.miun.dt176g.xxxxyyyy.reactive;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import se.miun.dt176g.xxxxyyyy.reactive.support.Constants;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
 * The server also have it's own GUI.
 * @author 	Emma Pesjak
 * @version 1.0
 * @since 	2023-10-02
 */
public class Server implements ConnectionHandler, Serializable, WindowListener {

    private final Drawing drawing = new Drawing();
    private MainFrame mainFrame;
    private ServerSocket serverSocket;
    private boolean acceptConnections = true;
    public DrawingPanel drawingPanel;
    public static Menu menu = new Menu();
    private static final long serialVersionUID = 1L;
    private final List<Socket> clientSockets = new ArrayList<>();
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
            MainFrame frame = new MainFrame(server, menu); // Pass the server instance to MainFrame.
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
     * Starts the server by accepting incoming connections in a separate thread.
     */
    public void startServer() {
        mainFrame.addWindowListener(this);
        Thread serverThread = new Thread(() -> {
            try {
                while (acceptConnections) {
                    Socket socket = serverSocket.accept();

                    // Create a new thread to handle the incoming client.
                    Thread clientThread = new Thread(() -> handleIncomingConnection(socket));
                    clientThread.start();
                }
            } catch (IOException e) {
                handleServerSocketError(e);
            }
        });
        serverThread.start();
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

    /**
     * Handles an incoming connection from a client.
     * This method sets up communication with the new client, including creating
     * input and output streams, and establishing an observable for incoming
     * drawing events. It also sends existing drawing shapes to the new client.
     * @param socket is the socket representing the client connection.
     */
    private void handleIncomingConnection(Socket socket) {
        Thread clientThread = new Thread(() -> {
            try {
                clientSockets.add(socket);
                ObjectOutputStream clientOutputStream = new ObjectOutputStream(socket.getOutputStream());
                clientOutputStreams.put(socket, clientOutputStream);
                clientOutputStream.flush();

                ObjectInputStream clientInputStream = new ObjectInputStream(socket.getInputStream());

                // Create an observable for incoming drawing events.
                Observable<Object> clientDrawingEvents = Observable.create(emitter -> {
                    while (!emitter.isDisposed()) {
                        Object receivedObject = clientInputStream.readObject();

                        // Emit the received object to subscribers.
                        emitter.onNext(receivedObject);
                    }
                });

                // Create an observer for this client.
                Observer<Object> clientObserver = new Observer<Object>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        // Iterate through the already drawn Shapes in the drawing and send them to the new client.
                        for (Shape sentShape : drawing.getShapes()) {
                            try {
                                clientOutputStream.writeObject(sentShape);
                                clientOutputStream.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onNext(@NonNull Object object) {
                        handleReceivedObject(object);
                        // Iterate over the clients and send.
                        for (Socket clientSocket : clientSockets) {
                            if (clientSocket != socket) {
                                ObjectOutputStream objectOutputStream = clientOutputStreams.get(clientSocket);
                                if (objectOutputStream != null) {
                                    try {
                                        objectOutputStream.writeObject(object);
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
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                };

                // Subscribe this client's observer to the observable.
                clientDrawingEvents
                        .observeOn(Schedulers.io())
                        .subscribe(clientObserver);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        clientThread.start();
    }

    /**
     * Getter for the server's DrawingPanel.
     * @return the DrawingPanel.
     */
    public DrawingPanel getDrawingPanel() {
        return drawingPanel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleReceivedObject(Object receivedObject) {
        SwingUtilities.invokeLater(() -> {
            if (receivedObject instanceof String && receivedObject.equals("clear")) {
                drawingPanel.clearDrawing();
                sendClearEventToClients();
            } else if (receivedObject instanceof Shape) {
                drawing.addShape((Shape) receivedObject);
                drawingPanel.repaint();
            }
        });
    }

    /***
     * Handle server socket errors, logs and displays an error message
     * @param e is the exception
     */
    private void handleServerSocketError(IOException e) {
        e.printStackTrace();
        mainFrame.setStatusMessage(Constants.FAIL_HOST_MSG);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearEvent() {
        drawingPanel.clearDrawing();
        sendClearEventToClients();
    }

    /**
     * Method to notify all connected clients that the drawing
     * should be cleared.
     */
    public void sendClearEventToClients() {
        for (ObjectOutputStream outputStream : clientOutputStreams.values()) {
            try {
                outputStream.writeObject("clear");
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Stops the server, disposing of all resources.
     */
    public void shutDown() {
        acceptConnections = false;

        // Notify connected clients about server shutdown.
        for (ObjectOutputStream outputStream : clientOutputStreams.values()) {
            try {
                outputStream.writeObject("server_shutdown");
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Close all client sockets.
        for (Socket clientSocket : clientSockets) {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Close the serverSocket.
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
    public void windowOpened(WindowEvent e) {
        // Not needed.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowClosing(WindowEvent e) {
        shutDown();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowClosed(WindowEvent e) {
        // Not needed.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowIconified(WindowEvent e) {
        // Not needed.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowDeiconified(WindowEvent e) {
        // Not needed.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowActivated(WindowEvent e) {
        // Not needed.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowDeactivated(WindowEvent e) {
        // Not needed.
    }
}