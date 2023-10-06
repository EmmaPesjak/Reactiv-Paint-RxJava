package se.miun.dt176g.xxxxyyyy.reactive.support;

/**
 * <h1>Constants</h1>
 * Interface that define constant values.
 * @author 	Emma Pesjak
 * @version 1.0
 * @since 	2023-10-05
 */
public interface Constants {
    String TITLE = "Reactive Paint";

    String CLIENT_START_MSG = "You are a client, please connect to a server";
    String CLIENT_CONNECT_MSG = "Connected to the server as a client";
    String STATUS_MSG = "Status: ";
    String CONNECT_BTN ="Connect to server";
    String FAIL_CONNECT_MSG = "Failed to connect to a server, make sure one is running.";
    String FAIL_HOST_MSG = "Failed to host the server. Try again";
    String SERVER = "You are running the server";
    String SERVER_DC = "The server has disconnected, make sure a server is running and then restart.";
    String CLEAR = "clear";
    String SERVER_SHUT_DOWN = "server_shutdown";
    String CLIENT_SHUT_DOWN = "client_shutdown";

    String ADDRESS = "localhost";
    int PORT = 12345;
}
