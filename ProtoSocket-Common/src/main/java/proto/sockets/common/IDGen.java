package proto.sockets.common;

import java.util.Random;

/*
 * Project: proto.sockets.common | Author: BenWithJamIn#4547
 * Created: 04/09/2023 at 15:25
 */
public class IDGen {
    private static final Random random = new Random();
    private static final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String generateID(int len) {
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < len; i++) {
            id.append(chars.charAt(random.nextInt(chars.length())));
        }
        return id.toString();
    }
}
