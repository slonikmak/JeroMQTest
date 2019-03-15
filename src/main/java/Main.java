import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @autor slonikmak on 15.03.2019.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(2);

        ZContext context = new ZContext();

        service.submit(()->{
                ZMQ.Socket socket = context.createSocket(SocketType.PUB);
                socket.bind("inproc://qqq");

                while (!Thread.currentThread().isInterrupted()){
                    System.out.println("Send");
                    socket.send("data");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

        });

        Thread.sleep(2000);

        service.submit(()->{
                ZMQ.Socket socket = context.createSocket(SocketType.SUB);
                socket.connect("inproc://qqq");
                socket.subscribe("");

                while (!Thread.currentThread().isInterrupted()){
                    String str = socket.recvStr();
                    //Thread.sleep(100);
                    System.out.println("RECEIVED "+str);
                }

        });

    }
}
