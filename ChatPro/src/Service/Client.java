package Service;

import Ui.ChatUi;

import java.io.*;
import java.net.Socket;

//TODO
// add log of all messages
// save them to a file
public class Client implements ChatUi.ClickCallback {
    private Socket socket;
    private ObjectInputStream sInput;		// to read from the socket
    private ObjectOutputStream sOutput;		// to write on the socket
    private String clientName;

    private ChatUi ui;

    // -----------------------------------------------------------
    public Client(Socket socket, String userName, ObjectInputStream sInput, ObjectOutputStream sOutput) {
        this.socket = socket;
        this.sInput = sInput;
        this.sOutput = sOutput;
        this.clientName = userName;
        ui = new ChatUi(userName, 100, 100, this);
        sendMessage(new Message("man zendam"));

        // Listen to messages from server
        new ListenFromServer().start();
    }

    // -----------------------------------------------------------
    public void sendMessage(Message message) {
        try {
            sOutput.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -----------------------------------------------------------
    @Override
    public void onClick(String message, String receiver) {
        Message messageObj = new Message(this.clientName, receiver, message);
        sendMessage(messageObj);
    }
    // -----------------------------------------------------------
    class ListenFromServer extends Thread {
        public void run() {
            receiveListOfUsers();
            receiveMessages();
        }
        // -----------------
        private void receiveListOfUsers() {
            String line;
            try {
                while (true) {
                    line = (String) sInput.readObject();
                    if (!line.isEmpty()) {
                        ui.addTextToTextArea(line + "\n");
                        break;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        // -----------------
        public void receiveMessages() {
            Message message;
            while (true) {
                try {
                    message = (Message)sInput.readObject();
                    if (message != null)
                        ui.addTextToTextArea(message.getSender() + " : " + message.getMessage() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

    } // end of ListenFromServer

} // end of Client Class
