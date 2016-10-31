/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.Till.till;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 *
 * @author David
 */
public class ServerConnection {
    
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private ObjectInputStream obIn;
    private ObjectOutputStream obOut;
    
    private boolean isConnected;
    
    public ServerConnection(){
        isConnected = false;
    }
    
    public void connect(String IP, int PORT) throws IOException{
        socket = new Socket();
        
        socket.connect(new InetSocketAddress(IP, PORT), 2000);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        obIn = new ObjectInputStream(socket.getInputStream());
        obOut = new ObjectOutputStream(socket.getOutputStream());
        obOut.flush();
        isConnected = true;
    }
}
