package com.weiqi.weaver.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeaverServer {
    
    private static final int DEFAULT_PORT_NUMBER = 80;
    private static final int NUMBER_OF_THREADS = 10;
    
    private int port;
    
    public void start() throws IOException {
        ServerSocket ss = new ServerSocket(port);
        System.out.println("Weaver server started on port: " + port);
        ExecutorService service = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        while (!Thread.interrupted()) {
            Socket client = ss.accept();
            service.submit(new HttpRequestHandler(client));
        }
        ss.close();
    }
    
    private void setPortNumber(String[] args) {
        if (args.length > 0)
            port = Integer.parseInt(args[0]);
        else
            port = DEFAULT_PORT_NUMBER;
    }
    
    public static void main(String[] args) {
        WeaverServer server = new WeaverServer();
        server.setPortNumber(args);
        try {
            server.start();
        } catch (IOException e) {
            System.err.println("Weaver server failed to start.");
            e.printStackTrace();
        }
    }
}
