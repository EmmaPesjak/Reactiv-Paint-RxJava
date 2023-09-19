package se.miun.dt176g.xxxxyyyy.reactive;

import io.reactivex.rxjava3.core.Observable;
import se.miun.dt176g.xxxxyyyy.reactive.support.Constants;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;

/**
 * <h1>Client</h1>
 * //Incoming connections(receiving drawing events/objects from others over the network)should be represented as Observables.
 * @author 	Emma Pesjak
 * @version 1.0
 * @since 	2023-09-19
 */
public class Client {
    private Socket socket;
    private final MainFrame mainFrame;

    public Client(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public static void main(String[] args) {

        MainFrame frame = new MainFrame(true);

        // Make sure GUI is created on the event dispatching thread.
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }

    public void connectToServer() {
        try {
            socket = new Socket(Constants.ADDRESS, Constants.PORT);
            mainFrame.setStatusMessage(Constants.CLIENT_CONNECT_MSG);

            // kirra alla gamla drawings, det kan man ju kanske göra i Observablen nedan
            mainFrame.setUpDrawing(new Drawing()); //TODO fixa så denna inte blir ny.

            Observable.just("Alla drawing events");
            //TODO IN MED LOGIK HÄR

        } catch (IOException e) {
            mainFrame.setUpFailedToConnect();
            e.printStackTrace();
        }
    }
}
