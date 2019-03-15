package remote_test;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

/**
 * @autor slonikmak on 11.03.2019.
 */
public class Server2 {
    public static void main(String[] args) {
        try(ZContext context = new ZContext()) {
            ZMQ.Socket publisher = context.createSocket(SocketType.PUB);
            publisher.connect("tcp://localhost:5566");

            for (int i = 0; i < 10000; i++) {
                String msg = String.format("msg2 %d", i);
                System.out.println("Send "+msg);
                publisher.send(msg, 0);
                Thread.sleep(20);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
