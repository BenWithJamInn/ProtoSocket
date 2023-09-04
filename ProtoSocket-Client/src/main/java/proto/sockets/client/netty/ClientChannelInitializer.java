package proto.sockets.client.netty;

import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;
import proto.sockets.common.messaging.MessageEventsHandler;
import proto.sockets.common.netty.InboundMessageListener;
import proto.sockets.common.netty.ProtoChannelInitializer;

/*
 * Project: proto.sockets.client.nett | Author: BenWithJamIn#4547
 * Created: 04/09/2023 at 11:42
 */
@AllArgsConstructor
public class ClientChannelInitializer extends ProtoChannelInitializer {
    private final MessageEventsHandler messageHandler;

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        super.initChannel(socketChannel);
        socketChannel.pipeline().addLast("messageListener", new InboundMessageListener(this.messageHandler));
    }
}
