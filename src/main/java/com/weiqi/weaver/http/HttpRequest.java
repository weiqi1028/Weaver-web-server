package com.weiqi.weaver.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * spec: http://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html#sec5
 */

public class HttpRequest {
    private Method method;
    
    private String requestURI;
    
    private String protocolVersion;
    
    private List<String> headers = new ArrayList<String>();
    
    private static Logger logger = Logger.getLogger(HttpRequest.class);
    
    public HttpRequest(Socket socket) throws IOException {
        
        // read from socket
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String request = br.readLine();
        
        // parse request line
        logger.info(request);
        String[] strs = request.split("\\s+");
        method = Method.valueOf(strs[0]);
        requestURI = strs[1];
        protocolVersion = strs[2];
        
        // parse headers
        while (!(request = br.readLine()).equals("")) {
            headers.add(request);
        }
        System.out.println("Request from " + socket.getRemoteSocketAddress() + ":\n" + this.toString());
    }
    
    public Method getMethod() {
        return method;
    }
    
    public String getRequestURI() {
        return requestURI;
    }
    
    public String getProtocolVersion() {
        return protocolVersion;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getMethod().getValue() + " " + this.getRequestURI() + " " + this.getProtocolVersion() + "\n");
        for (String header : headers) {
            sb.append(header + "\n");
        }
        return sb.toString();
    }
}
