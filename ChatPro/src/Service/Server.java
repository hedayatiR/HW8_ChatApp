package Service;

import Ui.ServerUi;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerUi ui;
    private ServerSocket serverSocket = null;
    private Socket currentClintSocket = null;
    private PrintWriter out;

    // -----------------------------------------------------------
    public Server() {
        int clientNum = 1;
        ui = new ServerUi("Server", 500, 100, this);
        // init server
        try {
            serverSocket = new ServerSocket(6666);
            ui.addTextToTextArea("Waiting for a client ...\n");
            while (true) {
                new Thread(new clientTask(serverSocket.accept(), clientNum++))
                        .start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // -----------------------------------------------------------

    // -----------------------------------------------------------
    public class clientTask implements Runnable {
        private Socket socket;
        private BufferedReader br = null;
        private String clientName;

        public clientTask(Socket socket, int clientNum) {
            this.socket = socket;
            this.clientName = "Client " + clientNum;
            ui.addTextToTextArea(this.clientName + " connected!\n");
        }

        @Override
        public void run() {
            initStreams();
            receiveMessages();
        }

        // -----------------------------------------------------------
        public void initStreams() {
            try {
                // stream reader for read from server
                InputStreamReader isr = new InputStreamReader(socket.getInputStream());
                br = new BufferedReader(isr);


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
                        if (line != null) {
                            currentClintSocket = socket;
                            ui.addTextToTextArea(this.clientName + " : " + line + "\n");
                        }
                    }
                }
            } catch (java.net.SocketException e) {
                ui.addTextToTextArea("connection to " + this.clientName + " lost!\n");
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    // -----------------------------------------------------------
    public void sendMessage(String message) {
        // output stream for send message to server
        try {
            out = new PrintWriter(currentClintSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.println(message);
    }

    // -----------------------------------------------------------
}