package HttpTaskServer;

import org.junit.jupiter.api.Test;

import java.io.IOException;

public class HttpTaskServerTest {
    @Test
    void startTaskServer() throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.startServer(8080);

        httpTaskServer.stopServer();
    }
}
