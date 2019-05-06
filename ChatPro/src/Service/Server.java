package Service;

import Ui.ServerUi;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerUi ui;
    private ServerSocket serverSocket = null;
    private Socket socket = null;
    private PrintWriter out;
    BufferedReader br = null;

    // -----------------------------------------------------------
    public Server() {
        ui = new ServerUi("Server", 500, 100, this);
        // init server
        try {
            serverSocket = new ServerSocket(6666);
        } catch (IOException e) {
            e.printStackTrace();
        }

        initConnection();
        receiveMessages();
    }
    // -----------------------------------------------------------
    public void initConnection() {
        try {
            ui.addTextToTextArea("Waiting for a client ...\n");
            socket = serverSocket.accept();
            ui.addTextToTextArea("Client accepted!\n");

            // stream reader for read from server
            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            br = new BufferedReader(isr);
            // output stream for send message to server
            out = new PrintWriter(socket.getOutputStream(), true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -----------------------------------------------------------
    public void receiveMessages() {
        String line = "";

        try {
            while (true) {
                if (socket.isConnected()) {
                    line = br.readLine();
                    if (line != null)
                        ui.addTextToTextArea("Client : " + line + "\n");
                }
            }
        } catch (java.net.SocketException e) {
            ui.addTextToTextArea("connection to client lost!\n");
            try { socket.close(); } catch (IOException e1) { e1.printStackTrace(); }
            initConnection();
            receiveMessages();
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    // -----------------------------------------------------------
    public void sendMessage(String message) {
        out.println(message);
    }
    // -----------------------------------------------------------
}