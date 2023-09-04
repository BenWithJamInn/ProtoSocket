package proto.sockets.server.netty;

import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import proto.sockets.common.netty.InboundMessageListener;
import proto.sockets.common.netty.ProtoChannelInitializer;
import proto.sockets.common.messaging.MessageEventsHandler;

/*
 * Project: proto.sockets.server.netty | Author: BenWithJamIn#4547
 * Created: 04/09/2023 at 11:37
 */
@AllArgsConstructor
public class ServerChannelInitializer extends ProtoChannelInitializer {
    private MessageEventsHandler messageHandler;

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        super.initChannel(socketChannel);

        // Listener
        socketChannel.pipeline().addLast("messageListener", new InboundMessageListener(this.messageHandler));
    }
}
