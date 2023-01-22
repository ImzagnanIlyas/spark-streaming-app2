package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Sensor {
    public static void main(String[] args) {
        String hostName = "localhost";
        int portNumber = 9999;

        try {
                Socket echoSocket = new Socket(hostName, portNumber);        // 1st statement
                PrintWriter out =                                            // 2nd statement
                        new PrintWriter(echoSocket.getOutputStream(), true);
                BufferedReader in =                                          // 3rd statement
                        new BufferedReader(
                                new InputStreamReader(echoSocket.getInputStream()));
                BufferedReader stdIn =                                       // 4th statement
                        new BufferedReader(
                                new InputStreamReader(System.in));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
