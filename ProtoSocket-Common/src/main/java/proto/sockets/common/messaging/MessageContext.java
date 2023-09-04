package proto.sockets.common.messaging;

import com.google.protobuf.Message;
import lombok.AllArgsConstructor;
import proto.sockets.common.netty.Connection;

/*
 * Project: proto.sockets.common | Author: BenWithJamIn#4547
 * Created: 04/09/2023 at 10:39
 */
@AllArgsConstructor
public class MessageContext {
    private final Connection connection;
    private final String acknowledgementID;

    /**
     * Sends a reply to the client, the client must be expecting a reply
     *
     * @param message The message to send
     */
    public void sendReply(Message message) {
        if (this.acknowledgementID == null) {
            throw new RuntimeException("Cannot send a reply to a client that is not expecting one");
        }
        this.connection.sendReply(message, this.acknowledgementID);
    }

    /**
     * Sends a message to the client
     *
     * @param message The message to send
     */
    public void sendMessage(Message message) {
        this.connection.sendMessage(message);
    }

    /**
     * Sends a message to the client and expects a reply
     *
     * @param message The message to send
     * @param callback The callback to run when the client replies
     */
    public void sendMessage(Message message, Runnable callback) {
        this.connection.sendMessage(message, any -> callback.run());
    }
}
