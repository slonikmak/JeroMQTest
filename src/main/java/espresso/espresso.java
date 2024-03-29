package espresso;

import org.zeromq.*;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZThread.IAttachedRunnable;

import java.util.Random;

public class espresso
{
    //  The subscriber thread requests messages starting with
    //  A and B, then reads and counts incoming messages.
    private static class Subscriber implements IAttachedRunnable
    {

        @Override
        public void run(Object[] args, ZContext ctx, Socket pipe)
        {
            //  Subscribe to "A" and "B"
            Socket subscriber = ctx.createSocket(SocketType.SUB);
            subscriber.connect("tcp://localhost:6001");
            subscriber.subscribe("A".getBytes(ZMQ.CHARSET));
            subscriber.subscribe("B".getBytes(ZMQ.CHARSET));

            int count = 0;
            while (count < 5) {
                String string = subscriber.recvStr();

                System.out.println("Recived: "+string);

                if (string == null)
                    break; //  Interrupted
                count++;
            }
            ctx.destroySocket(subscriber);
        }
    }

    //  The publisher sends random messages starting with A-J:
    private static class Publisher implements IAttachedRunnable
    {
        @Override
        public void run(Object[] args, ZContext ctx, Socket pipe)
        {
            Socket publisher = ctx.createSocket(SocketType.PUB);
            publisher.bind("tcp://*:6000");
            Random rand = new Random(System.currentTimeMillis());

            while (!Thread.currentThread().isInterrupted()) {
                String string = String.format("%c-%05d", 'A' + rand.nextInt(10), rand.nextInt(100000));
                if (!publisher.send(string))
                    break; //  Interrupted
                try {
                    Thread.sleep(100); //  Wait for 1/10th second
                }
                catch (InterruptedException e) {
                }
            }
            ctx.destroySocket(publisher);
        }
    }

    //  The listener receives all messages flowing through the proxy, on its
    //  pipe. In CZMQ, the pipe is a pair of ZMQ_PAIR sockets that connect
    //  attached child threads. In other languages your mileage may vary:
    private static class Listener implements IAttachedRunnable
    {
        @Override
        public void run(Object[] args, ZContext ctx, Socket pipe)
        {
            //  Print everything that arrives on pipe
            while (true) {
                ZFrame frame = ZFrame.recvFrame(pipe);
                if (frame == null)
                    break; //  Interrupted
                System.out.println("Frame: ");
                frame.print(null);
                frame.destroy();
            }
        }
    }

    //  The main task starts the subscriber and publisher, and then sets
    //  itself up as a listening proxy. The listener runs as a child thread:
    public static void main(String[] argv)
    {
        try (ZContext ctx = new ZContext()) {
            //  Start child threads
            ZThread.fork(ctx, new Publisher());
            ZThread.fork(ctx, new Subscriber());

            ZMQ.Socket subscriber = ctx.createSocket(SocketType.XSUB);
            subscriber.connect("tcp://localhost:6000");
            ZMQ.Socket publisher = ctx.createSocket(SocketType.XPUB);
            publisher.bind("tcp://*:6001");
            ZMQ.Socket listener = ZThread.fork(ctx, new Listener());
            ZMQ.proxy(subscriber, publisher, listener);

            System.out.println(" interrupted");

            // NB: child threads exit here when the context is closed
        }
    }
}