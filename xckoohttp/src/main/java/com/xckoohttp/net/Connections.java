package com.xckoohttp.net;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Connections {
    private Map<String, Socket> socketList;

    public Connections() {
        socketList = new HashMap<>();
    }

    public void addConnection(String url, Socket socket) {
        if (!socketList.containsKey(url)) {
            socketList.put(url, socket);
        }
    }

    void closeConnection(String url){
        if (socketList.containsKey(url)) {
            Socket socket = socketList.get(url);
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
