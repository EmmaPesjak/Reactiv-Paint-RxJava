package se.miun.dt176g.xxxxyyyy.reactive;

import com.sun.tools.javac.Main;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <h1>DrawingServer</h1>
 *
 * @author 	Emma Pesjak
 * @version 1.0
 * @since 	2023-09-18
 */
public class DrawingServer {

    private final MainFrame mainFrame;
    private ServerSocket serverSocket;
    private final Drawing drawing = new Drawing();

    public DrawingServer(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        try {
            serverSocket = new ServerSocket(12345); // Annan port?

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Main starting point of the server side of the application.
     * @param args not applicable here.
     */
    public static void main(String[] args) {

        MainFrame frame = new MainFrame(false);

        // Make sure GUI is created on the event dispatching thread.
        SwingUtilities.invokeLater(() -> frame.setVisible(true));

    }

    // vet inte riktigt vad jag pysslar med här
    // vad händer om någon DCar?
    // vad händer med clienten om servern DCar?
    // stänga sockets och observables?
    // vad händer vid error?

    //onNext
    //onError
    //onComplete

    //Incoming connections (receiving drawing events/objects from others over the network) should be represented as Observables.
    //Outgoing connections (sending drawing events/objects to others over the network) should be represented as Observers.


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



    // ska man ha någon lista med alla klienter?
    //private List<Client> clients = new ArrayList<>();

    //private List<ClientHandler> clients = new ArrayList<>();


//    public DrawingServer() {
//        try {
//            serverSocket = new ServerSocket(12345); // Annan port?
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    public void startServer() {
        //?? eller i constructorn?

    }
}
