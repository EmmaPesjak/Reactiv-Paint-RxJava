package se.miun.dt176g.xxxxyyyy.reactive;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import se.miun.dt176g.xxxxyyyy.reactive.support.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

/**
 * <h1>Client</h1>
 * //Incoming connections(receiving drawing events/objects from others over the network)should be represented as Observables.
 * @author 	Emma Pesjak
 * @version 1.0
 * @since 	2023-09-25
 */
public class Client implements ConnectionHandler, Serializable {
    private Socket socket;
    private MainFrame mainFrame;
    private ObjectOutputStream outputStream;

    private ObjectInputStream inputStream;
    private PublishSubject<Shape> drawingUpdates = PublishSubject.create();
    private DrawingPanel drawingPanel;

    // Define a PublishSubject to receive drawing updates from the server
    private PublishSubject<Shape> serverDrawingUpdates = PublishSubject.create();



    static Menu menu = new Menu();

    private static final long serialVersionUID = 1L; // Change this value when the class structure changes

//    public void drawShape(Shape shape) {
//        drawingUpdates.onNext(shape);
//    }

    public static void main(String[] args) {



        // Create an instance of Client.
        Client client = new Client();
        // Create an instance of MainFrame and pass the client instance.
        MainFrame frame = new MainFrame(client, menu);
        // Set the client instance for the created client object.
        client.setMainFrame(frame);
        // Make sure GUI is created on the event dispatching thread.
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }

    // Create an Observable to represent receiving drawing events from the server
    public Observable<Shape> receiveDrawingEvents() {
        return serverDrawingUpdates;
    }

    // Create an Observer to represent sending drawing events to the server
    public Observer<Shape> sendDrawingEvents() {
        return drawingUpdates;
    }

    public void sendDrawingUpdate(Shape shape) {
        try {
            // Send the drawing update to the server
            outputStream.writeObject(shape);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleDrawingUpdate(Shape shape) {
        // Handle the received drawing update draw it on the client's canvas
        drawingUpdates.onNext(shape);

        System.out.println("händer detta??");

        //drawingPanel.addShape(shape);?????????
    }


//    private void subscribeToServerDrawingUpdates() {
//        serverDrawingUpdates.observeOn(Schedulers.io())
//                .subscribe(shape -> {
//                    // Handle the received drawing update (e.g., draw it on the client's canvas)
//                    // You can update the GUI or perform any other actions here.
//                    handleDrawingUpdate(shape);
//                });
//    }
    public void setMainFrame(MainFrame frame) {
        this.mainFrame = frame;
    }

    public void connectToServer() {
        try {
            socket = new Socket(Constants.ADDRESS, Constants.PORT);

            Drawing drawing = new Drawing(); //TODO Inte ny drawing ju
            drawingPanel = new DrawingPanel(drawing, menu);
            // Set the client reference in the DrawingPanel
            drawingPanel.setClient(this);

            mainFrame.setUpDrawing(drawingPanel);
            mainFrame.setStatusMessage(Constants.CLIENT_CONNECT_MSG);

            // Initialize the outputStream
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());

            System.out.println("tjena");

            // Create an Observable to receive messages from the server
//            Observable<Object> serverMessages = Observable.create(emitter -> {
//                // Run this code on the IO scheduler
//                Disposable disposable = Schedulers.io().scheduleDirect(() -> {  //Viktigt annars fryser programmet för inputstreamen fastnar i en loop
//                    while (!emitter.isDisposed()) {
//                        try {
//                            // Read a message from the server
//                            Object receivedMessage = inputStream.readObject();
//
//                            // Emit the received message to subscribers
//                            emitter.onNext(receivedMessage);
//
//                            // Ensure the output stream is flushed to send data immediately
//                            outputStream.flush();
//                        } catch (IOException | ClassNotFoundException e) {
//                            // Handle communication or deserialization error
//                            emitter.onError(e);
//                            break;
//                        }
//                    }
//                });
//
//                // Dispose the disposable when the subscriber is disposed
//                emitter.setCancellable(disposable::dispose);
//            });
//
//            // Subscribe to messages from the server (including "hello" messages)
//            serverMessages.subscribe(message -> {
//
//                    // Handle drawing updates if received
//                    handleDrawingUpdate((Shape) message);
//
//            });

            // Create an Observable to receive drawing events from the server
//            Observable<Shape> serverDrawingEvents = Observable.create(emitter -> {
//                while (true) {
//                    try {
//                        // Read a Shape object from the server
//                        Shape receivedShape = (Shape) inputStream.readObject();
//
//                        // Emit the received shape to subscribers
//                        emitter.onNext(receivedShape);
//                    } catch (IOException | ClassNotFoundException e) {
//                        // Handle communication or deserialization error
//                        emitter.onError(e);
//                        break;
//                    }
//                }
//            });
//
//            // Subscribe to drawing events from the server
//            serverDrawingEvents.subscribe(this::handleDrawingUpdate);
//
//            // Create an Observable to send drawing events to the server
//            sendDrawingEvents().observeOn(Schedulers.io())
//                    .subscribe(shape -> {
//                        try {
//                            // Send the drawing event to the server
//                            outputStream.writeObject(shape);
//                            outputStream.flush();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    });

            Observable<Shape> serverDrawingEvents = Observable.create((ObservableEmitter<Shape> emitter) -> {
                while (true) {
                    try {
                        // Read a Shape object from the server
                        Shape receivedShape = (Shape) inputStream.readObject();


                        System.out.println("detta då?");
                        // Emit the received shape to subscribers
                        emitter.onNext(receivedShape);
                    } catch (IOException | ClassNotFoundException e) {
                        // Handle communication or deserialization error
                        emitter.onError(e);
                        break;
                    }
                }
            }).subscribeOn(Schedulers.io()); // Perform blocking I/O in a background thread

// Subscribe to drawing events from the server
            serverDrawingEvents.subscribe(
                    shape -> {
                        // Handle the received shape
                        handleDrawingUpdate(shape);

                        System.out.println("nepp?");
                    },
                    error -> {
                        // Handle errors, e.g., communication or deserialization errors
                        error.printStackTrace();
                    }
            );

            // Handle other communication or tasks with the server here


        } catch (IOException e) {
            mainFrame.setUpFailedToConnect();
            e.printStackTrace();
        }
    }

    //TODO HÄR FÅR JAG FAN IN SHAPEN
    // Implement the drawShape method to handle new shapes received from DrawingPanel
    public void drawShape(Shape shape) {

        // vad gör jag sen=?

        System.out.println("Received new shape: " + shape);
    }
}
