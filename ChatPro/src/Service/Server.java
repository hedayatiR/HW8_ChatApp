package Service;

import Ui.ChatUi;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server implements ChatUi.ClickCallback {
    private ChatUi ui;
    private ServerSocket serverSocket = null;
    private Socket currentClintSocket = null;
    private PrintWriter out;
    private HashMap<String, String> clientsUserPass;

    // -----------------------------------------------------------
    public Server() {
        initClientsUserPass();
        int clientNum = 1;
        ui = new ChatUi("Server", 500, 100, this);
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
    public void initClientsUserPass() {
        clientsUserPass = new HashMap<>();
        clientsUserPass.put("reza", "123");
        clientsUserPass.put("ali", "1234");
        clientsUserPass.put("hasan", "321");
    }
    // -----------------------------------------------------------
    @Override
    public void onClick(String str) {
        sendMessage(str);
    }

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
            if (processUserPass())
                receiveMessages();
        }
        // -----------------------------------------------------------
        public boolean processUserPass() {
            String userName = "";
            if (socket.isConnected()) {
                Server.this.currentClintSocket = this.socket;
                try {
                    userName = br.readLine();
                    if (userName != null) {
                        if (clientsUserPass.containsKey(userName)) {
                            String password = br.readLine();
                            if (password != null) {
                                if(clientsUserPass.get(userName).equals(password)) {
                                    Server.this.sendMessage("OK.");
                                    this.clientName = userName;
                                    return true;
                                }
                                else
                                    Server.this.sendMessage("Password is wrong!");
                            }

                        } else {
                            Server.this.sendMessage("There is no such user name!");
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return false;
        }

        // -----------------------------------------------------------
        public void initStreams() {
            try {
                // stream reader for read from server
                InputStreamReader isr = new InputStreamReader(socket.getInputStream());
                br = new BufferedReader(isr);
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
    public synchronized void sendMessage(String message) {
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