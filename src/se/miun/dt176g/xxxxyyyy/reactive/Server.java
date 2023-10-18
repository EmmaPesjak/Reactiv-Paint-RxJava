package se.miun.dt176g.xxxxyyyy.reactive;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import se.miun.dt176g.xxxxyyyy.reactive.support.Constants;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <h1>Server</h1>
 * Represents the server-side of the application for handling incoming and outgoing connections/drawing events.
 * The server also have it's own GUI.
 * @author 	Emma Pesjak
 * @version 1.0
 * @since 	2023-10-06
 */
public class Server implements ConnectionHandler, Serializable {
    private static final long serialVersionUID = 1L;
    private MainFrame mainFrame;
    public DrawingPanel drawingPanel;
    public static Menu menu = new Menu();
    private final Drawing drawing = new Drawing();
    private boolean acceptConnections = true;
    private ServerSocket serverSocket;
    private final List<Socket> clientSockets = new ArrayList<>();
    private final Map<Socket, ObjectOutputStream> clientOutputStreams = new ConcurrentHashMap<>();
    private final List<Thread> clientThreads = new ArrayList<>();
    private final PublishSubject<Object> outgoingDataObserver = PublishSubject.create();
    private final Map<Socket, Observable<Object>> clientObservables = new ConcurrentHashMap<>();

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
     * Handles an incoming connection from a client.
     * This method sets up communication with the new client, including creating
     * input and output streams, and establishing an observable for incoming
     * drawing events. It also sends existing drawing shapes to the new client.
     * @param socket is the socket representing the client connection.
     */
    private synchronized void handleIncomingConnection(Socket socket) {
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
                    } catch (SocketException | EOFException e) {
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
                        outgoingDataObserver.onNext(sentShape);
                    }
                }

                @Override
                public void onNext(@NonNull Object object) {
                    handleReceivedObject(object);
                    if (object instanceof String && object.equals(Constants.CLIENT_SHUT_DOWN)) {

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
     * {@inheritDoc}
     */
    @Override
    public void sendShape(Shape shape) {
        outgoingDataObserver.onNext(shape);
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
            if (receivedObject instanceof String && receivedObject.equals(Constants.CLEAR)) {
                clearEvent();
            } else if (receivedObject instanceof Shape) {
                drawing.addShape((Shape) receivedObject);
                drawingPanel.repaint();
                sendShape((Shape) receivedObject);
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
        outgoingDataObserver.onNext(Constants.CLEAR);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shutDown() {
        acceptConnections = false;

        // Notify connected clients about server shutdown.
        outgoingDataObserver.onNext(Constants.SERVER_SHUT_DOWN);
        outgoingDataObserver.onComplete();

        // Stop all client threads.
        for (Thread clientThread : clientThreads) {
            clientThread.interrupt();
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
}