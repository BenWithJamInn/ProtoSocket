package proto.sockets.common.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Getter;
import proto.sockets.common.messaging.MessageEventsHandler;
import proto.sockets.transport.Transport;

/*
 * Project: proto.sockets.server.netty | Author: BenWithJamIn#4547
 * Created: 04/09/2023 at 10:59
 */
public class InboundMessageListener extends ChannelInboundHandlerAdapter {
    private final MessageEventsHandler messageHandler;
    @Getter private Connection connection;

    public InboundMessageListener(MessageEventsHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.connection = new Connection(ctx.channel(), this.messageHandler);
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Transport.MessageTransport message = (Transport.MessageTransport) msg;
        this.messageHandler.handleTransportReceive(message, this.connection);
    }
}
