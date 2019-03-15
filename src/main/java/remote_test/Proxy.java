package remote_test;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZSocket;

import java.util.TreeMap;

/**
 * @autor slonikmak on 11.03.2019.
 */
public class Proxy {
    public static void main(String[] args) {

        new Thread(()->{
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try (ZContext context = new ZContext()){
                long time = System.currentTimeMillis();
                long cTime, diff;

                ZMQ.Socket subscriber = context.createSocket(SocketType.SUB);
                subscriber.connect("ipc://msg");
                subscriber.subscribe("msg2");

                while (true){
                    String result = subscriber.recvStr(0);

                    cTime = System.currentTimeMillis();
                    diff = cTime-time;
                    time = cTime;

                    System.out.println(result+" "+ diff);
                }
            }
        }).start();

        try(ZContext context = new ZContext()) {
            //incoming data
            ZMQ.Socket sub = context.createSocket(SocketType.XSUB);
            sub.bind("tcp://*:5566");

            //outgoing pubs
            ZMQ.Socket pub = context.createSocket(SocketType.XPUB);
            pub.bind("tcp://*:5567");
            pub.bind("ipc://msg");
            ZMQ.proxy(sub, pub, null);
        }
    }
}
