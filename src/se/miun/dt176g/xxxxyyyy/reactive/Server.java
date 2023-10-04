package se.miun.dt176g.xxxxyyyy.reactive;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
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
import java.net.SocketException;
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
 * @since 	2023-10-04
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
    private final List<Thread> clientThreads = new ArrayList<>();
    private final PublishSubject<Object> outgoingDataObserver = PublishSubject.create();
    private final Map<Socket, Observable<Object>> clientObservables = new HashMap<>();

    /**
     * Constructor which sets the DrawingPanel and ServerSocket.
     */
    public Server() {
        try {
            drawingPanel = new DrawingPanel(drawing, menu, this);
            serverSocket = new ServerSocket(Constants.PORT);

            // Subscribe outgoingDataObserver to send data to clients
            outgoingDataObserver.subscribe(o -> {
                for (ObjectOutputStream outputStream : clientOutputStreams.values()) {
                    try {
                        outputStream.writeObject(o);
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            handleServerSocketError(e);
        }
    }

    /**
     * Main starting point of the server side of the application.
     * @param args not applicable here.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Server server = new Server(); // Create an instance of Server.
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
        Observable.create(emitter -> {
                    while (acceptConnections) {
                        try {
                            Socket socket = serverSocket.accept();
                            Thread clientThread = new Thread(() -> handleIncomingConnection(socket));
                            clientThreads.add(clientThread);
                            clientThread.start();
                        } catch (SocketException e) {
                            // Ignore this exception when shutting down the server
                            if (!acceptConnections) {
                                return;
                            }
                            // Handle other exceptions as needed
                            emitter.onError(e);
                            break;
                        } catch (IOException e) {
                            // Handle exceptions as needed
                            emitter.onError(e);
                            break;
                        }
                    }
                })
                .subscribeOn(Schedulers.io()) // Ensure this runs on a background thread.
                .subscribe(
                        value -> {
                        },
                        Throwable::printStackTrace
                );
    }

    /**
     * Sends a Shape to all connected clients.
     * @param shape the Shape to send.
     */
    public void sendShapeToClients(Shape shape) {
        outgoingDataObserver.onNext(shape);
    }

    /**
     * Handles an incoming connection from a client.
     * This method sets up communication with the new client, including creating
     * input and output streams, and establishing an observable for incoming
     * drawing events. It also sends existing drawing shapes to the new client.
     * @param socket is the socket representing the client connection.
     */
    private void handleIncomingConnection(Socket socket) {
        try {
            clientSockets.add(socket);
            ObjectOutputStream clientOutputStream = new ObjectOutputStream(socket.getOutputStream());
            clientOutputStreams.put(socket, clientOutputStream);
            clientOutputStream.flush();

            ObjectInputStream clientInputStream = new ObjectInputStream(socket.getInputStream());

            // Create an observable for incoming drawing events.
            Observable<Object> clientDrawingEvents = Observable.create(emitter -> {
                while (!emitter.isDisposed()) {
                    try {
                        Object receivedObject = clientInputStream.readObject();

                        // Emit the received object to subscribers.
                        emitter.onNext(receivedObject);
                    } catch (SocketException e) {
                        handleClientDisconnect(socket);
                        clientInputStream.close();
                        emitter.onComplete();
                        // Break out of the loop to terminate this client thread.
                        break;
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }).subscribeOn(Schedulers.io()); // Offload to the io scheduler.

            // Store the observable in the map for future reference.
            clientObservables.put(socket, clientDrawingEvents);

            // Create an observer for this client.
            Observer<Object> clientObserver = new Observer<>() {
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
                    if (object instanceof String && object.equals("client_shutdown")) {

                        handleClientDisconnect(socket);
                        try {
                            clientInputStream.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
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
                outgoingDataObserver.onNext("clear");
            } else if (receivedObject instanceof Shape) {
                drawing.addShape((Shape) receivedObject);
                drawingPanel.repaint();
                sendShapeToClients((Shape) receivedObject);
            }
        });
    }

    /**
     * Handles the disconnection of a client from the server, cleans up resources.
     * @param socket is the socket representing the disconnected client.
     */
    private void handleClientDisconnect(Socket socket) {
        clientSockets.remove(socket);
        clientOutputStreams.remove(socket);

        Observable<Object> clientObservable = clientObservables.get(socket);
        if (clientObservable != null) {
            clientObservables.remove(socket);
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearEvent() {
        drawingPanel.clearDrawing();
        outgoingDataObserver.onNext("clear");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shutDown() {
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

        outgoingDataObserver.onComplete();

        // Stop all client threads.
        for (Thread clientThread : clientThreads) {
            clientThread.interrupt(); // Signal to the client threads that they should terminate.
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
        acceptConnections = false;
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