package remote_test;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.DataInputStream;

/**
 * @autor slonikmak on 11.03.2019.
 */
public class Client {
    public static void main(String[] args) {
        try (ZContext context = new ZContext()){
            long time = System.currentTimeMillis();
            long cTime, diff;

            ZMQ.Socket subscriber = context.createSocket(SocketType.SUB);
            subscriber.connect("tcp://localhost:5567");
            subscriber.subscribe("");

            while (true){
                String result = subscriber.recvStr(0);

                cTime = System.currentTimeMillis();
                diff = cTime-time;
                time = cTime;

                System.out.println(result+" "+ diff);
            }
        }
    }
}
