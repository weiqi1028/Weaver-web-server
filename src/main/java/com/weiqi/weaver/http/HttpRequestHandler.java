package com.weiqi.weaver.http;

import java.io.IOException;
import java.net.Socket;

public class HttpRequestHandler implements Runnable {
    private Socket socket;
    
    public HttpRequestHandler(Socket socket) {
        this.socket = socket;
    }
    
    public void run() {
        try {
            HttpRequest request = new HttpRequest(socket);
            HttpResponse response = new HttpResponse(request);
            response.send(socket);
            socket.close();
        } catch (IOException e) {
            System.err.println("Error in HttpRequestHandler");
            e.printStackTrace();
        }
    }
}
