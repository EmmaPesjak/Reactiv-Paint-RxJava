package se.miun.dt176g.xxxxyyyy.reactive;

import java.io.IOException;
import java.net.ServerSocket;

public class DrawingServer {

    // vet inte riktigt vad jag pysslar med här
    // vad händer om någon DCar?
    // vad händer med clienten om servern DCar?
    // stänga sockets och observables?
    // vad händer vid error?

    //onNext
    //onError
    //onComplete

    // ska man ha någon lista med alla klienter?

    // borde servern ligga i app-start?

    //Incoming connections (receiving drawing events/objects from others over the network) should be represented as Observables.
    //Outgoing connections (sending drawing events/objects to others over the network) should be represented as Observers.


    ServerSocket serverSocket = new ServerSocket(12345);

    public DrawingServer() throws IOException {
    }

    public void startServer(){
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
