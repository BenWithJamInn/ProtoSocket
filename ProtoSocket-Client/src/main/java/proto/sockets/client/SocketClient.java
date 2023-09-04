package proto.sockets.client;

/*
 * Project: proto.sockets.client | Author: BenWithJamIn#4547
 * Created: 04/09/2023 at 11:09
 */

import com.google.protobuf.Any;
import com.google.protobuf.Message;
import proto.sockets.client.connection.SocketClientConnection;
import proto.sockets.common.messaging.MessageEventsHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class SocketClient {
    private final ClientOptions options;
    private final String host;
    private final int port;
    private final List<SocketClientConnection> connections = new ArrayList<>();
    private final AtomicInteger connectionRoundRobin = new AtomicInteger(0);
    private final MessageEventsHandler messageHandler = new MessageEventsHandler();

    public SocketClient(ClientOptions options, String host, int port) {
        this.options = options;
        this.host = host;
        this.port = port;

        for (int i = 0; i < options.connectionPoolSize; i++) {
            this.connections.add(new SocketClientConnection(this.host, this.port, this.messageHandler));
        }
    }

    public void sendMessage(Message message, Consumer<Any> callback) {
        SocketClientConnection connection = this.connections.get(this.connectionRoundRobin.getAndIncrement() % this.connections.size());
        connection.getConnection().sendMessage(message, callback);
    }
}
