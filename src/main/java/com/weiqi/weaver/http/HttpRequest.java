package com.weiqi.weaver.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * spec: http://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html#sec5
 */

public class HttpRequest {
    private Method method;
    
    private String requestURI;
    
    private String protocolVersion;
    
    private List<String> headers = new ArrayList<String>();
    
    public HttpRequest(Socket socket) throws IOException {
        
        // read from socket
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String request = br.readLine();
        
        // parse request line
        String[] strs = request.split("\\s+");
        method = Method.valueOf(strs[0]);
        requestURI = strs[1];
        protocolVersion = strs[2];
        
        // parse headers
        while (!(request = br.readLine()).equals("")) {
            headers.add(request);
        }
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
}
