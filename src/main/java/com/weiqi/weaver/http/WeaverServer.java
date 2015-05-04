package com.weiqi.weaver.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeaverServer {

    private static final int DEFAULT_PORT_NUMBER = 80;
    private static final int NUMBER_OF_TREHAD = 10;

    private SelectorListener acceptListener;
    private SelectorListener readListener;
    private boolean isReadListenerRunning;
    
    private ExecutorService executorService;

    private int port;

    public void start() throws IOException {
        executorService = Executors.newFixedThreadPool(NUMBER_OF_TREHAD);
        
        acceptListener = new SelectorListener();
        readListener = new SelectorListener();

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress("localhost", port));
        System.out.println("Weaver server started on port: " + port);

        serverSocketChannel.register(acceptListener.getSelector(),
                SelectionKey.OP_ACCEPT);
        Thread acceptThread = new Thread(acceptListener);
        acceptThread.start();

    }

    private void setPortNumber(String[] args) {
        if (args.length > 0)
            port = Integer.parseInt(args[0]);
        else
            port = DEFAULT_PORT_NUMBER;
    }
    
    public boolean isReadThreadRunning() {
        return isReadListenerRunning;
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

    class SelectorListener implements Runnable {
        private Selector selector;

        public SelectorListener() throws IOException {
            this.selector = Selector.open();
        }

        public Selector getSelector() {
            return this.selector;
        }

        public void run() {
            while (!Thread.interrupted()) {
                try {
                    int n = selector.select();
                    if (n == 0)
                        continue;

                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> iter = keys.iterator();
                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        iter.remove();
                        dispatch(key);
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        private void dispatch(SelectionKey key) throws IOException {
            if (key.isAcceptable()) {
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept();
                //sc.configureBlocking(false);
                
                Socket sock = sc.socket();
                executorService.submit(new HttpRequestHandler(sock));
                //sc.register(readListener.getSelector(), SelectionKey.OP_READ);
                
                
                if (!isReadThreadRunning()) {
                    isReadListenerRunning = true;
                    Thread readThread = new Thread(readListener);
                    readThread.start();
                }
            }
            else if (key.isReadable()) {
                SocketChannel sc = (SocketChannel) key.channel();
                Socket sock = sc.socket();
                executorService.submit(new HttpRequestHandler(sock));
            }
        }

    }

}
