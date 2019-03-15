package remote_test;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

/**
 * @autor slonikmak on 11.03.2019.
 */
public class Server {
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
                subscriber.connect("ipc://msg3");
                subscriber.subscribe("");

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
            ZMQ.Socket publisher = context.createSocket(SocketType.PUB);
            publisher.bind("ipc://msg3");
            publisher.connect("tcp://localhost:5566");


            for (int i = 0; i < 10000; i++) {
                String msg = String.format("%d", i);
                System.out.println("Send "+msg);
                publisher.sendMore("msg");
                publisher.send(msg, 0);
                Thread.sleep(20);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
