package proto.sockets.common.messaging;

import com.google.protobuf.Message;

import java.lang.reflect.Method;

public record RegisteredListener(Class<? extends Message> messageClass, MessageListener obj, Method listenerMethod) {
}
