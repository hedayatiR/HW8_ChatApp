package Service;

import Ui.ChatUi;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
    private ChatUi ui;
    private ServerSocket serverSocket = null;
    private HashMap<String, String> clientsUserPass;
    private HashMap<String, ClientThread> onlineClients;

    // -----------------------------------------------------------
    public Server() {
        initClientsUserPass();
        onlineClients = new HashMap<>();
        int clientNum = 1;
        ui = new ChatUi("Server", 500, 100, null);
        // init server
        try {
            serverSocket = new ServerSocket(6666);
            ui.addTextToTextArea("Waiting for a client ...\n");
            while (true) {
                ClientThread clientThreadObj = new ClientThread(serverSocket.accept(), clientNum++);
                clientThreadObj.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -----------------------------------------------------------
    public void initClientsUserPass() {
        clientsUserPass = new HashMap<>();
        clientsUserPass.put("reza", "123");
        clientsUserPass.put("ali", "123");
        clientsUserPass.put("hasan", "123");
    }

    // -----------------------------------------------------------
    public void addToOnlineClients(String key, ClientThread value) {
        synchronized (this.onlineClients) {
            this.onlineClients.put(key, value);
        }
    }

    // -----------------------------------------------------------
    public void removeFromOnlineClients(String key) {
        synchronized (this.onlineClients) {
            this.onlineClients.remove(key);
        }
    }

    // -----------------------------------------------------------

    public class ClientThread extends Thread {
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        private Socket socket;
        private String clientName;

        // ***************************************
        public ClientThread(Socket socket, int clientNum) {
            this.socket = socket;
            this.clientName = "Client " + clientNum;
            initStreams();
            ui.addTextToTextArea(this.clientName + " connected!\n");
        }

        // ***************************************
        public void initStreams() {
            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                System.out.println("Exception creating new Input/output Streams: " + e);
            }
        }

        // ***************************************
        @Override
        public void run() {
            if (processUserPass())
                receiveMessages();
        }

        // ***************************************
        public boolean processUserPass() {
            String userName = "";
            if (socket.isConnected()) {
                try {
                    userName = (String) sInput.readObject();
                    if (userName != null) {
                        if (clientsUserPass.containsKey(userName)) {
                            String password = (String) sInput.readObject();
                            if (password != null) {
                                if (clientsUserPass.get(userName).equals(password)) {
                                    sendMessage("OK.");
                                    this.clientName = userName;
                                    sendOnlineClients();
                                    addToOnlineClients(this.clientName, this);
                                    return true;
                                } else
                                    sendMessage("Password is wrong!");
                            }

                        } else {
                            sendMessage("There is no such user name!");
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
            return false;
        }

        // ***************************************
        private void sendOnlineClients() {
//            ArrayList<String> onlineUsersList = new ArrayList<>();
            String onlineUsersListStr = "";
            synchronized (Server.this.onlineClients) {
                if (!Server.this.onlineClients.isEmpty()) {

                    for (String key :
                            Server.this.onlineClients.keySet()) {
//                        onlineUsersList.add(key);
                        onlineUsersListStr += key + ",";
                    }
                    System.out.println(onlineUsersListStr);
//                    sendMessageObj(onlineUsersList);
                    sendMessage(onlineUsersListStr);
                } else
                    sendMessage("No online user!\n");
            }
        }

        // ***************************************
        public void receiveMessages() {
            String line = "";

            try {
                while (true) {
                    if (socket.isConnected()) {
                        line = ((Message) sInput.readObject()).getMessage();
                        if (line != null) {
                            ui.addTextToTextArea(this.clientName + " : " + line + "\n");
                        }
                    }
                }
            } catch (java.net.SocketException e) {
                ui.addTextToTextArea("connection to " + this.clientName + " lost!\n");
                removeFromOnlineClients(this.clientName);
                try {
                    sInput.close();
                    sOutput.close();
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

        // ***************************************
        public void sendMessage(String message) {
            try {
                sOutput.writeObject(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    // -----------------------------------------------------------
}