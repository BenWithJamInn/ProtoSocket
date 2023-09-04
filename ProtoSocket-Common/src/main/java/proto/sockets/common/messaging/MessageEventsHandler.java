package proto.sockets.common.messaging;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import lombok.NoArgsConstructor;
import proto.sockets.common.netty.Connection;
import proto.sockets.transport.Transport;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/*
 * Project: proto.sockets.common | Author: BenWithJamIn#4547
 * Created: 01/09/2023 at 21:29
 */
@NoArgsConstructor
public class MessageEventsHandler {
    private final Map<Class<? extends Message>, RegisteredListener> listeners = new ConcurrentHashMap<>();
    private final Map<String, Consumer<Any>> pendingAcknowledgments = new ConcurrentHashMap<>();

    /**
     * Registers a class to listen for events
     *
     * @param obj The class to register
     */
    public void registerClass(MessageListener obj) {
        for (Method method : obj.getClass().getDeclaredMethods()) {
            // check if method has MessageHandler annotation
            Annotation annotation = method.getAnnotation(MessageHandler.class);
            if (annotation == null) {
                continue;
            }
            // check if method has 2 parameters
            Class<?>[] parameters = method.getParameterTypes();
            if (parameters.length != 2) {
                continue;
            }
            // check if first parameter is a message
            Class<?> messageParam = parameters[0];
            if (!Message.class.isAssignableFrom(messageParam)) {
                continue;
            }
            // check if second parameter is a channel context
            if (!MessageContext.class.isAssignableFrom(parameters[1])) {
                continue;
            }
            // check if message is already registered
            Class<? extends Message> messageClass = messageParam.asSubclass(Message.class);
            if (this.listeners.containsKey(messageClass)) {
                throw new RuntimeException("A listener for " + messageClass.getName() + " is already registered!");
            }
            this.listeners.put(messageClass, new RegisteredListener(messageClass, obj, method));
        }
    }

    /**
     * Unregisters a message from being listened to
     *
     * @param messageClass The message to unregister
     *
     * @return Whether the message was unregistered
     */
    public boolean unregisterMessage(Class<? extends Message> messageClass) {
        return this.listeners.remove(messageClass) != null;
    }

    /**
     * Unregisters a listener from being listened to, will unregister all messages in the listener
     *
     * @param listener The listener to unregister
     *
     * @return Whether the listener was unregistered
     */
    public boolean unregisterListener(MessageListener listener) {
        return this.listeners.values().removeIf(registeredListener -> Objects.equals(registeredListener.obj(), listener));
    }

    /**
     * Registers a callback for a future acknowledgment
     *
     * @param acknowledgementID The acknowledgment ID
     * @param callback The callback to be run on reply
     */
    public void registerPendingResponse(String acknowledgementID, Consumer<Any> callback) {
        this.pendingAcknowledgments.put(acknowledgementID, callback);
    }

    /**
     * Calls the event for the message
     *
     * @param anyMessage The message to call the event for
     * @param ctx The channel context
     */
    public void callMessageEvent(Any anyMessage, MessageContext ctx) {
        for (RegisteredListener listener : this.listeners.values()) {
            if (anyMessage.is(listener.messageClass())) {
                try {
                    Message message = anyMessage.unpack(listener.messageClass());
                    listener.listenerMethod().invoke(listener.obj(), message, ctx);
                } catch (InvalidProtocolBufferException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void handleTransportReceive(Transport.MessageTransport messageTransport, Connection connection) {
        if (messageTransport.hasAcknowledgementID()) {
            String acknowledgementID = messageTransport.getAcknowledgementID();
            if (this.pendingAcknowledgments.containsKey(acknowledgementID)) {
                this.pendingAcknowledgments.remove(acknowledgementID).accept(messageTransport.getPayload());
                return;
            }
        }
        MessageContext ctx = new MessageContext(connection, messageTransport.getAcknowledgementID());
        this.callMessageEvent(messageTransport.getPayload(), ctx);
    }
}
