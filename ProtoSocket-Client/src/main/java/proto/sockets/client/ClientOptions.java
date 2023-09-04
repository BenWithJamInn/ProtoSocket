package proto.sockets.client;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * Project: proto.sockets.client | Author: BenWithJamIn#4547
 * Created: 04/09/2023 at 11:09
 */
@Getter
@Setter
@NoArgsConstructor
public class ClientOptions {
    int connectionPoolSize = 1;
}
