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
 * @since 	2023-10-06
 */
public class Client implements ConnectionHandler, Serializable {
    private static final long serialVersionUID = 1L;
    private Socket socket;
    private MainFrame mainFrame;
    private DrawingPanel drawingPanel;
    private Drawing drawing;
    private static final Menu menu = new Menu();
    private Client client;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
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
        Observable.create(emitter -> {
            try {
                socket = new Socket(Constants.ADDRESS, Constants.PORT);

                // Set up the frame.
                drawing = new Drawing();
                drawingPanel = new DrawingPanel(drawing, menu, client);
                mainFrame.setUpDrawing(drawingPanel);
                mainFrame.setStatusMessage(Constants.CLIENT_CONNECT_MSG);

                // Set up streams.
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                inputStream = new ObjectInputStream(socket.getInputStream());

                // Set up observer/observables.
                incomingDataObservable = createIncomingDataObservable();
                outgoingDataObserver = createOutgoingDataObserver();
                subscribeToIncomingData();
            } catch (IOException e) {
                mainFrame.setUpFailedToConnect();
                e.printStackTrace();
                emitter.onError(e);
            }
        })
        .subscribeOn(Schedulers.io()) // Use Schedulers to avoid blocking the Swing EDT.
        .subscribe(
                value -> {},
                Throwable::printStackTrace
        );
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
                if (message.equals(Constants.CLEAR)) {
                    drawingPanel.clearDrawing();
                } else if (message.equals(Constants.SERVER_SHUT_DOWN)) {
                    mainFrame.removeDrawing();
                    mainFrame.setStatusMessage(Constants.SERVER_DC);
                    shutDown();
                }
            } else if (receivedObject instanceof Shape) {
                drawing.addShape((Shape) receivedObject);
                drawingPanel.repaint();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendShape(Shape shape) {
        outgoingDataObserver.onNext(shape);
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
        try {
            // Notify the server.
            outgoingDataObserver.onNext(Constants.CLIENT_SHUT_DOWN);

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
