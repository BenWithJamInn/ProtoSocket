package proto.sockets.common.messaging;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This method will be called asynchronously when a message is received.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageHandler {
}
