package com.stabilit.net.keepalive;

import java.net.*;

public class SocketKeepAlive {

    public static void main(String args[]) throws Exception {

        Socket socket = new Socket();

        //Connects this socket to the server.
        socket.connect(new InetSocketAddress("localhost", 80));

        if (!socket.getKeepAlive()) {
            socket.setKeepAlive(true);
        }
        System.out.println("SO_KEEPALIVE is enabled. " + socket.getKeepAlive());

        socket.close();
    }
}