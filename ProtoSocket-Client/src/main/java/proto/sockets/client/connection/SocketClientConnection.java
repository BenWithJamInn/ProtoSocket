package proto.sockets.client.connection;

import com.google.protobuf.Any;
import com.google.protobuf.Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.Getter;
import proto.sockets.client.netty.ClientChannelInitializer;
import proto.sockets.common.IDGen;
import proto.sockets.common.messaging.MessageEventsHandler;
import proto.sockets.common.netty.Connection;
import proto.sockets.common.netty.InboundMessageListener;
import proto.sockets.transport.Transport;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/*
 * Project: proto.sockets.client | Author: BenWithJamIn#4547
 * Created: 04/09/2023 at 11:50
 */
public class SocketClientConnection {
    // data
    @Getter private final String socketID;
    @Getter private ConnectionStatus status = ConnectionStatus.CONNECTING;
    @Getter private Connection connection;

    private final MessageEventsHandler messageHandler;
    private Channel channel;

    // vars
    private final String host;
    private final int port;

    public SocketClientConnection(String host, int port, MessageEventsHandler messageHandler) {
        this.socketID = IDGen.generateID(5);
        this.host = host;
        this.port = port;
        this.messageHandler = messageHandler;

        this.startClient();
    }

    private void startClient() {
        new Thread(() -> {
            EventLoopGroup workerGroup = new NioEventLoopGroup();

            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(workerGroup);
                bootstrap.channel(NioSocketChannel.class);
                bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
                bootstrap.handler(new LoggingHandler(LogLevel.INFO));
                bootstrap.handler(new ClientChannelInitializer(this.messageHandler));
                status = ConnectionStatus.CONNECTED;

                // retrieve the channel and connection
                ChannelFuture future = bootstrap.connect(this.host, this.port).sync();
                channel = future.channel();
                connection = new Connection(channel, this.messageHandler);

                // block until the channel closes
                channel.closeFuture().sync();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                status = ConnectionStatus.DISCONNECTED;
                workerGroup.shutdownGracefully();
            }
        }).start();
    }
}
