package com.weiqi.weaver.http;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * spec: http://www.w3.org/Protocols/rfc2616/rfc2616-sec6.html#sec6
 */

public class HttpResponse {

    private static final String PROTOCOL_VERSION = "HTTP/1.1";

    private List<String> headers = new ArrayList<String>();

    private static Logger logger = Logger.getLogger(HttpRequest.class);

    private byte[] messageBody;

    public HttpResponse(HttpRequest request) throws IOException {
        if (request.getMethod() == Method.HEAD) {
            addHeader(StatusCode._200);
        } else if (request.getMethod() == Method.GET) {
            File file = new File(request.getRequestURI());
            addHeader(StatusCode._200);
            if (file.isDirectory()) {
                // generate web page, simply list the files in the directory
                StringBuilder sb = new StringBuilder();
                sb.append("<html>" + "<head>" + "<title>" + "Index of ");
                sb.append(request.getRequestURI());
                sb.append("</title>" + "</head>" + "<body>" + "<h1>"
                        + "Index of " + request.getRequestURI() + "</h1>"
                        + "<hr>" + "<pre>");
                File[] files = file.listFiles();
                for (File f : files) {
                    sb.append("<a href =\"" + f.getPath() + "\">" + f.getPath()
                            + "</a>\n");
                }
                sb.append("<hr>" + "</pre>" + "</body>" + "</html>");
                messageBody = sb.toString().getBytes();
            }
            else if (file.exists()) {
                messageBody = fileToBytes(file);
                
            }
            else {
                headers.clear();
                addHeader(StatusCode._404);
                messageBody = StatusCode._404.getValue().getBytes();
                logger.info("Requested source not found: " + request.getRequestURI());
            }
        }
    }

    private void addHeader(StatusCode code) {
        headers.add(PROTOCOL_VERSION + " " + code.getValue());
        headers.add("Server: weaver");
        headers.add("Connection: close");
        headers.add("\r");
    }

    private byte[] fileToBytes(File file) throws IOException {
        byte[] b = new byte[(int) file.length()];
        InputStream fi = new FileInputStream(file);
        fi.read(b);
        fi.close();
        return b;
    }
    
    public void send(Socket socket) throws IOException {
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        for (String h : headers)
            dos.writeBytes(h + "\n");
        if (messageBody != null)
            dos.write(messageBody);
        dos.flush();
        System.out.println("Send response to " + socket.getRemoteSocketAddress() + ":\n" + this.toString());
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String header : headers) {
            sb.append(header + "\n");
        }
        return sb.toString();
    }
}
