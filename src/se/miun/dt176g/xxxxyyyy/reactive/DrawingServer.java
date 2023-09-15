package se.miun.dt176g.xxxxyyyy.reactive;

import io.reactivex.rxjava3.disposables.Disposable;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1>DrawingServer</h1>
 *
 * @author 	Emma Pesjak
 * @version 1.0
 * @since 	2023-09-08
 */
public class DrawingServer {

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

    private ServerSocket serverSocket;

    // ska man ha någon lista med alla klienter?
    private List<Client> clients = new ArrayList<>();


    public DrawingServer() {
        try {
            serverSocket = new ServerSocket(12345); // Annan port?
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startServer() {
        //?? eller i constructorn?

    }

    // INKOPIERAT från moodles exempel, ska ju inte ligga här.
    // Accept an arbitrary number of client connections (at any time).
// Read from all of them and print the text messages they send.
// (Accepts new connections and reads/prints messages from connected clients concurrently.)
//
//    public static void main(String[] args) throws Exception {
//        ServerSocket ssock = new ServerSocket(12345);
//        System.out.println("creating stream of clients");
//        Observable.create(e -> {
//                    while (true) {
//                        e.onNext(ssock.accept());
//                    }
//                })
//                // Doing subscribeOn here only determines which thread accepts, but does not
//                // prevent reading input to block additional connections from being accepted.
//                .map(Socket::getInputStream)
//                .map(InputStreamReader::new)
//                .map(BufferedReader::new)
//                .map(BufferedReader::lines)
//                .flatMap(stream -> Observable.fromIterable(() -> stream.iterator()).subscribeOn(Schedulers.io()))
//                // This subscribeOn (in the flatMap) is necessary to allow new connections while one is being processed.
//                .subscribe(System.out::println);
//    }

    // INKOPIERAT från moodles exempel på en client för testning:
//    public static void main(String[] args) throws Exception {
//        Socket con = new Socket("localhost", 12345);
//
//        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//        PrintWriter printer = new PrintWriter(new OutputStreamWriter(con.getOutputStream()), true);
//        reader.lines().forEach(printer::println);
//    }
}
