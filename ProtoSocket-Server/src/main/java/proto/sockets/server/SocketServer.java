package proto.sockets.server;

import lombok.Getter;
import proto.sockets.common.messaging.MessageEventsHandler;
import proto.sockets.server.connection.SocketServerListener;

/*
 * Project: proto.sockets.server | Author: BenWithJamIn#4547
 * Created: 01/09/2023 at 20:54
 */
public class SocketServer {
    @Getter private final ServerOptions options;
    @Getter private final int port;
    @Getter private final MessageEventsHandler messageHandler = new MessageEventsHandler();
    private SocketServerListener listener;

    public SocketServer(ServerOptions options, int port) throws InterruptedException {
        this.options = options;
        this.port = port;
        this.startServer();
    }

    private void startServer() {
        this.listener = new SocketServerListener(this.options, this.port, this.messageHandler);
    }
}
