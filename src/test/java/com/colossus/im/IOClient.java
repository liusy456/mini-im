package com.colossus.im;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * @author Tlsy1
 * @since 2019-09-23 16:43
 **/
public class IOClient {
    public static void main(String[] args) {
        new Thread(() -> {
            try {
                Socket socket = new Socket("127.0.0.1", 8000);
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String message = br.readLine();
                while (!message.equals("q")) {
                    try {
                        socket.getOutputStream().write(message.getBytes());
                        message = br.readLine();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
