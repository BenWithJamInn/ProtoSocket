package proto.sockets.server.connection;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import proto.sockets.common.messaging.MessageEventsHandler;
import proto.sockets.server.ServerOptions;
import proto.sockets.server.netty.ServerChannelInitializer;

/*
 * Project: proto.sockets.server.connection | Author: BenWithJamIn#4547
 * Created: 04/09/2023 at 15:15
 */
public class SocketServerListener {
    private final ServerOptions options;
    private final int port;
    private final MessageEventsHandler messageHandler;

    public SocketServerListener(ServerOptions options, int port, MessageEventsHandler messageHandler) {
        this.options = options;
        this.port = port;
        this.messageHandler = messageHandler;

        this.startServer();
    }

    private void startServer() {
        new Thread(() -> {
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();

            try {
                ServerBootstrap bootstrap = new ServerBootstrap()
                        .group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .handler(new LoggingHandler(LogLevel.INFO))
                        .childHandler(new ServerChannelInitializer(this.messageHandler))
                        .option(ChannelOption.SO_BACKLOG, 128)
                        .childOption(ChannelOption.SO_KEEPALIVE, true);

                ChannelFuture future = bootstrap.bind(this.port).sync();
                future.channel().closeFuture().sync().channel();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        }).start();
    }
}
