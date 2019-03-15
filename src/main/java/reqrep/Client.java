package reqrep;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

/**
 * @autor slonikmak on 13.03.2019.
 */
public class Client {
    public static void main(String[] args)
    {
        try (ZContext context = new ZContext()) {
            //  Socket to talk to server
            System.out.println("Connecting to hello world server");
            while (true){
                ZMQ.Socket socket = context.createSocket(SocketType.REQ);
                socket.bind("tcp://*:5555");
                socket.setReceiveTimeOut(1000);
                while (true) {
                    try {
                        String request = "Hello";
                        System.out.println("Sending Hello ");
                        socket.send(request.getBytes(ZMQ.CHARSET), 0);

                        byte[] reply = socket.recv(0);
                        if (reply == null) {
                            System.out.println("Connection failed!!");
                            //break;
                        }
                        else System.out.println(
                                "Received " + new String(reply, ZMQ.CHARSET)
                        );
                    } catch (org.zeromq.ZMQException e){
                        System.out.println("Exception");
                        socket.close();
                        context.destroySocket(socket);
                        break;
                    }
                }
            }

        }
    }
}
