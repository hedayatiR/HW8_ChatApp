package Service;

import Ui.ChatUi;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class Client implements ChatUi.ClickCallback {
    private static String logString = "";
    private Socket socket;
    private ObjectInputStream sInput;        // to read from the socket
    private ObjectOutputStream sOutput;        // to write on the socket
    private String clientName;

    private ChatUi ui;

    // -----------------------------------------------------------
    public Client(Socket socket, String userName, ObjectInputStream sInput, ObjectOutputStream sOutput) {
        this.socket = socket;
        this.sInput = sInput;
        this.sOutput = sOutput;
        this.clientName = userName;
        ui = new ChatUi(userName, 100, 100, this);

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
    public void onSendBtnClick(String message, String receiver) {
        Message messageObj = new Message(this.clientName, receiver, message);
        sendMessage(messageObj);
        logString += "Me to " + receiver + " : " + message + "\n";
    }

    // -----------------------------------------------------------
    @Override
    public void onSaveLogBtnClick() {
        BufferedWriter writer = null;
        try {
            File file = new File("Log/" + this.clientName + "_Log.txt");
            file.getParentFile().mkdirs();
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(logString);
            writer.close();
            JOptionPane.showMessageDialog(null, "Log file saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Sorry. Failed to save file!");
        }
    }

    // -----------------------------------------------------------
    class ListenFromServer extends Thread {
        public void run() {
            receiveListOfUsers();
            receiveMessages();
        }

        // -----------------
        private void receiveListOfUsers() {
            String tmp = "List of online users in startup time:\n";
            String line;
            try {
                while (true) {
                    line = (String) sInput.readObject();
                    if (!line.isEmpty()) {
                        ui.addTextToTextArea(tmp);
                        logString += tmp;
                        ui.addTextToTextArea(line + "\n");
                        logString += line + "\n";
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
            try {
                while (true) {
                    message = (Message) sInput.readObject();
                    if (message != null) {
                        String txt = message.getSender() + " : " + message.getMessage() + "\n";
                        ui.addTextToTextArea(txt);
                        logString += txt;
                    }
                }

            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Server is shutdown");
                ui.dispose();
                try {
                    sInput.close();
                    sOutput.close();
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return;

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    } // end of ListenFromServer

} // end of Client Class
