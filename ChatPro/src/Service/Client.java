package Service;

import Ui.ChatUi;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Client implements ChatUi.ClickCallback {
    private static int clientNum = 1;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader br = null;
//    private ObjectInputStream inStream;

    private ChatUi ui;

    // -----------------------------------------------------------
    public Client(Socket socket, String userName) {
        this.socket = socket;
        initStreams();
        ui = new ChatUi(userName, 100, 100, this);
        sendMessage("man zendam");

        receiveListOfUsers();

        receiveMessages();
    }

    // -----------------------------------------------------------
    private void receiveListOfUsers() {
        String line;
        try {
            while (!br.ready()) ;
            line = br.readLine();
            ui.addTextToTextArea(line);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // -----------------------------------------------------------
//    private void receiveListOfUsersObj() {
//        try {
//            Thread.sleep(50);
//            inStream = new ObjectInputStream(socket.getInputStream());
//            ArrayList<String> usersList = (ArrayList<String>) inStream.readObject();
//            if (usersList == null)
//                System.out.println("Null users");
//            if (usersList.isEmpty())
//                System.out.println("Empty users");
//            System.out.println(usersList);
//            ui.addTextToTextArea(usersList.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

    // -----------------------------------------------------------
    public void initStreams() {
        try {
            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            br = new BufferedReader(isr);
            out = new PrintWriter(socket.getOutputStream(), true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -----------------------------------------------------------

    public void receiveMessages() {
        String line;
        while (true) {
            try {
                line = br.readLine();
                if (line != null)
                    ui.addTextToTextArea("Server : " + line + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // -----------------------------------------------------------
    public void sendMessage(String message) {
        out.println(message);
    }

    // -----------------------------------------------------------
    @Override
    public void onClick(String str) {
        sendMessage(str);
    }
    // -----------------------------------------------------------
}
