package proto.sockets.common.netty;

import com.google.protobuf.Any;
import com.google.protobuf.Message;
import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import proto.sockets.common.IDGen;
import proto.sockets.common.messaging.MessageEventsHandler;
import proto.sockets.transport.Transport;

import java.util.function.Consumer;

/*
 * Project: proto.sockets.common | Author: BenWithJamIn#4547
 * Created: 04/09/2023 at 15:20
 */
@AllArgsConstructor
@EqualsAndHashCode
public class Connection {
    private final Channel channel;
    private final MessageEventsHandler messageHandler;

    /**
     * Sends a reply to the client, the client must be expecting a reply
     *
     * @param message The message to send
     * @param acknowledgementId The acknowledgement ID to send
     */
    public void sendReply(Message message, String acknowledgementId) {
        Transport.MessageTransport.Builder builder = Transport.MessageTransport.newBuilder();
        builder.setPayload(Any.pack(message));
        builder.setAcknowledgementID(acknowledgementId);
        this.channel.writeAndFlush(builder.build());
    }

    /**
     * Sends a message to the client
     *
     * @param message The message to send
     */
    public void sendMessage(Message message) {
        this.sendMessage(message, null);
    }

    /**
     * Sends a message to the client and expects a reply
     *
     * @param message The message to send
     * @param callback The callback to run when the client replies
     */
    public void sendMessage(Message message, Consumer<Any> callback) {
        Transport.MessageTransport.Builder builder = Transport.MessageTransport.newBuilder();
        builder.setPayload(Any.pack(message));

        if (callback != null) {
            String id = IDGen.generateID(5);
            builder.setAcknowledgementID(id);
            this.messageHandler.registerPendingResponse(id, callback);
        }

        this.channel.writeAndFlush(builder.build());
    }
}
