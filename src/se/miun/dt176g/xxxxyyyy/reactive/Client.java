package se.miun.dt176g.xxxxyyyy.reactive;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import se.miun.dt176g.xxxxyyyy.reactive.support.Constants;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * <h1>Client</h1>
 * Represents the client-side of the application,establishing a connection to the server and
 * communicating with it. Sets up a client GUI.
 * @author 	Emma Pesjak
 * @version 1.0
 * @since 	2023-10-05
 */
public class Client implements ConnectionHandler, Serializable {
    private Socket socket;
    private MainFrame mainFrame;
    private static final Menu menu = new Menu();
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private DrawingPanel drawingPanel;
    private static final long serialVersionUID = 1L;
    private Drawing drawing;
    private Client client;
    private Observable<Object> incomingDataObservable;
    private Observer<Object> outgoingDataObserver;
    private boolean shouldTerminateIncomingDataObservable = false;

    /**
     * Main starting point of the application for a client.
     * @param args not applicable here.
     */
    public static void main(String[] args) {
        Client client = new Client(); // Create an instance of Client.
        MainFrame frame = new MainFrame(client, menu); // Create an instance of MainFrame and pass the client instance.
        client.setMainFrame(frame); // Set the MainFrame for the created client object.
        // Make sure GUI is created on the event dispatching thread.
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMainFrame(MainFrame frame) {
        this.mainFrame = frame;
    }

    /**
     * Establishes a connection to the server and sets up communication with it.
     */
    public void connectToServer() {
        client = this;
        // Create a worker thread to avoid blocking the Swing EDT.
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    socket = new Socket(Constants.ADDRESS, Constants.PORT);

                    // Set up the frame.
                    drawing = new Drawing();
                    drawingPanel = new DrawingPanel(drawing, menu, client);
                    mainFrame.setUpDrawing(drawingPanel);
                    mainFrame.setStatusMessage(Constants.CLIENT_CONNECT_MSG);

                    // Initialize the outputStream
                    outputStream = new ObjectOutputStream(socket.getOutputStream());
                    inputStream = new ObjectInputStream(socket.getInputStream());

                    // Set up observer/observables.
                    incomingDataObservable = createIncomingDataObservable();
                    outgoingDataObserver = createOutgoingDataObserver();
                    subscribeToIncomingData();
                } catch (IOException e) {
                    mainFrame.setUpFailedToConnect();
                    e.printStackTrace();
                }
                return null;
            }
        };
        worker.execute(); // Start the worker thread.
    }

    /**
     * Subscribes to the incoming data observable to asynchronously handle received objects.
     */
    private void subscribeToIncomingData() {
        incomingDataObservable.subscribe(
                this::handleReceivedObject,
                Throwable::printStackTrace
        );
    }

    /**
     * Creates an observable for incoming data from the input stream, that continuously reads objects
     * from the input stream.
     * @return an observable for incoming data.
     */
    private Observable<Object> createIncomingDataObservable() {
        return Observable.create(emitter -> {
                    try {
                        while (!shouldTerminateIncomingDataObservable) {
                            Object receivedObject = inputStream.readObject();
                            emitter.onNext(receivedObject);
                        }
                    } catch (SocketException se) {
                        // Handle the SocketException when the server disconnects.
                        emitter.onComplete();
                    } catch (EOFException eofe) {
                        // Handle the EOFException when the server disconnects.
                        emitter.onComplete();
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        if (!emitter.isDisposed()) {
                            emitter.onComplete(); // Signal completion when the loop terminates.
                        }
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    /**
     * Creates an observer for outgoing data to send objects over the network connection.
     * @return an observer for outgoing data.
     */
    private Observer<Object> createOutgoingDataObserver() {
        return new Observer<>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {}

            @Override
            public void onNext(@NonNull Object object) {
                try {
                    outputStream.writeObject(object);
                    outputStream.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {}
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleReceivedObject(Object receivedObject) {
        SwingUtilities.invokeLater(() -> {
            if (receivedObject instanceof String) {
                String message = (String) receivedObject;
                if (message.equals("clear")) {
                    drawingPanel.clearDrawing();
                } else if (message.equals("server_shutdown")) {
                    mainFrame.removeDrawing();
                    mainFrame.setStatusMessage(Constants.SERVER_DC);

                    try {
                        socket.close();
                        inputStream.close();
                        outputStream.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (receivedObject instanceof Shape) {
                drawing.addShape((Shape) receivedObject);
                drawingPanel.repaint();
            }
        });
    }

    /**
     * Sends a Shape object to the server using the outgoing data observer.
     * @param shape is the Shape to send to the server.
     */
    public void sendShapeToServer(Shape shape) {
        outgoingDataObserver.onNext(shape);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearEvent() {
        drawingPanel.clearDrawing();
        try {
            outputStream.writeObject("clear");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shutDown() {
        try {
            outputStream.writeObject("client_shutdown");
            outputStream.flush();

            // Set the flag to terminate the incoming data observable.
            shouldTerminateIncomingDataObservable = true;

            // Close the socket and the associated streams.
            socket.close();
            outputStream.close();
            inputStream.close();

            // Notify the observer to complete.
            outgoingDataObserver.onComplete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
