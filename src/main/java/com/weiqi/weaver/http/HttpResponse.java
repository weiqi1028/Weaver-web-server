package com.weiqi.weaver.http;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * spec: http://www.w3.org/Protocols/rfc2616/rfc2616-sec6.html#sec6
 */

public class HttpResponse {

    private static final String PROTOCOL_VERSION = "HTTP/1.1";
    
    private List<String> headers = new ArrayList<String>();
    private byte[] messageBody;
    
    public HttpResponse(HttpRequest request) {
        if (request.getMethod() == Method.HEAD) {
            addHeader(StatusCode._200);
        }
        else if (request.getMethod() == Method.GET) {
            File file = new File("." + request.getRequestURI());
            
            if (file.isDirectory()) {
                addHeader(StatusCode._200);
                // generate web page, simply list the files in the directory
                StringBuilder sb = new StringBuilder();
                sb.append("<html>"
                        + "<head>"
                        + "<title>"
                        + "Index of ");
                sb.append(request.getRequestURI());
                sb.append("</title>"
                        + "</head>"
                        + "<body>"
                        + "<h1>"
                        + "Index of "
                        + request.getRequestURI()
                        + "</h1>"
                        + "<hr>"
                        + "<pre>");
                File[] files = file.listFiles();
                for (File f : files) {
                    sb.append("<a href =\"" + f.getPath() + "\">" + f.getPath() + "</a>\n");
                }
                sb.append("<hr>"
                        + "</pre>"
                        + "</body>"
                        + "</html>");
                messageBody = sb.toString().getBytes();
            }
            else if (file.isFile()) {
                try {
                    @SuppressWarnings("resource")
                    RandomAccessFile raf = new RandomAccessFile(file, "r");
                    raf.read(messageBody);
                    addHeader(StatusCode._200);
                } catch (FileNotFoundException e) {
                    addHeader(StatusCode._404);
                    messageBody = StatusCode._404.toString().getBytes();
                } catch (IOException e) {
                    System.err.println("IOException in HttpResponse");
                }
            }
            else {
                addHeader(StatusCode._404);
                messageBody = StatusCode._404.getValue().getBytes();
            }
        }
    }
    
    private void addHeader(StatusCode code) {
        headers.add(PROTOCOL_VERSION + " " + code.getValue());
        headers.add("Server: weaver");
        headers.add("Connection: close");
    }
    
    public void send(Socket socket) throws IOException {
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        for (String h : headers)
            dos.writeBytes(h + "\n");
        dos.writeBytes("\n");
        if (messageBody != null)
            dos.write(messageBody);
        dos.flush();
    }
}
