package Service;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
    private ServerSocket serverSocket = null;
    private HashMap<String, String> clientsUserPass;
    private HashMap<String, ClientThread> onlineClients;

    // -----------------------------------------------------------
    public Server() {
        initClientsUserPass();
        onlineClients = new HashMap<>();
        // init server
        try {
            serverSocket = new ServerSocket(6666);
            System.out.println("Waiting for a client ...");
            while (true) {
                ClientThread clientThreadObj = new ClientThread(serverSocket.accept());
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
    private synchronized void forwardMessage(Message message) {
        if(onlineClients.containsKey(message.getReceiver())){
            onlineClients.get(message.getReceiver()).sendMessage(message);
        }
    }

    // -----------------------------------------------------------
    private void broadcastMessage(Message message) {
        message.setSender("Server");
        for (ClientThread client:
             onlineClients.values()) {
            client.sendMessage(message);
        }
    }

    // -----------------------------------------------------------
    public class ClientThread extends Thread {
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        private Socket socket;
        private String clientName;

        // ***************************************
        public ClientThread(Socket socket) {
            this.socket = socket;
            initStreams();
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
                                    broadcastMessage(new Message(this.clientName + " has joined room!"));
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
            String onlineUsersListStr = "";
            synchronized (Server.this.onlineClients) {
                if (!Server.this.onlineClients.isEmpty()) {

                    for (String key :
                            Server.this.onlineClients.keySet()) {
                        onlineUsersListStr += key + ",";
                    }
                    sendMessage(onlineUsersListStr);
                } else
                    sendMessage("No online user!\n");
            }
        }

        // ***************************************
        public void receiveMessages() {
            Message message;

            try {
                while (true) {
                    if (socket.isConnected()) {
                        message = (Message) sInput.readObject();
                        if (message != null) {
                            forwardMessage(message);
                        }

                    }
                }

            } catch (java.net.SocketException e) {
                removeFromOnlineClients(this.clientName);
                broadcastMessage(new Message(this.clientName + " left room!\n"));
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
        public void sendMessage(Object message) {
            try {
                sOutput.writeObject(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    // -----------------------------------------------------------
}