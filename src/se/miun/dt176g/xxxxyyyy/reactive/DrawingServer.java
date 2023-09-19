package se.miun.dt176g.xxxxyyyy.reactive;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.Subject;
import se.miun.dt176g.xxxxyyyy.reactive.support.Constants;

import javax.swing.*;
import java.io.IOException;
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
 * @since 	2023-09-19
 */
public class DrawingServer implements ConnectionHandler {

    private MainFrame mainFrame;
    private ServerSocket serverSocket;
    private final Drawing drawing = new Drawing();

    private Subject<Socket> connections;

    private Subject<Shape> shapeStream;   //all shapes from all clients fast detta ska in i min drawing?
    // drawingen ska delas på någon vänster?

    // ska man ha någon lista med alla klienter?
    private List<Client> clients = new ArrayList<>();

    private boolean acceptConnections = true;


    // TODO servern är också en klient typ


    public DrawingServer() {
        try {
            serverSocket = new ServerSocket(Constants.PORT);
//            while (acceptConnections) {
//                Socket socket = serverSocket.accept();
//                Observable.<Socket>create(emitter -> emitter.onNext(socket))
//                        .observeOn(Schedulers.io())
//                        .subscribe(connections);
//            }

        } catch (IOException e) {
            mainFrame.setStatusMessage(Constants.FAIL_HOST_MSG);
            e.printStackTrace();
        }
    }

    public void startServer() {

        Thread serverThread = new Thread(() -> {
            try {
                while (acceptConnections) {
                    Socket socket = serverSocket.accept();
                    Observable.<Socket>create(emitter -> emitter.onNext(socket))  // detta vetekatten för det tog jag från exemplet
                            .observeOn(Schedulers.io())
                            .subscribe(connections);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        serverThread.start();
//        try {
//            while (acceptConnections) {
//                Socket socket = serverSocket.accept();
//                Observable.<Socket>create(emitter -> emitter.onNext(socket))
//                        .observeOn(Schedulers.io())
//                        .subscribe(connections);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void shutdown() {
        acceptConnections = false;
    }

    /**
     * Main starting point of the server side of the application.
     * @param args not applicable here.
     */
    public static void main(String[] args) {

        DrawingServer server = new DrawingServer(); // Create an instance of DrawingServer.

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(server); // Pass the server instance to MainFrame
            frame.setVisible(true);
            server.setMainFrame(frame);
            server.startServer();
        });

        // Make sure GUI is created on the event dispatching thread.
        //SwingUtilities.invokeLater(() -> frame.setVisible(true));

    }

    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    // vet inte riktigt vad jag pysslar med här
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



    public Drawing getDrawing() {
        return drawing;
    }
}
