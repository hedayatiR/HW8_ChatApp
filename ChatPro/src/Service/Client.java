package Service;

import Ui.ClientUi;

import java.io.*;
import java.net.Socket;

public class Client {
    private static int clientNum=1;
    private ClientUi ui;
    private Socket socket = null;
    private PrintWriter out;
    BufferedReader br = null;
    // -----------------------------------------------------------
    public Client(){
        ui = new ClientUi("Client "+clientNum++ , 100,100, this);
        initClient();
        receiveMessages();
    }
    // -----------------------------------------------------------
    public void initClient(){
        try {
            socket = new Socket("localhost", 6666);
            //if(socket.isConnected()) {
            if(socket != null) {
                // stream reader for read from server
                ui.addTextToTextArea("Connection to server established!\n");
                InputStreamReader isr = new InputStreamReader(socket.getInputStream());
                br = new BufferedReader(isr);
                // output stream for send message to server
                out = new PrintWriter(socket.getOutputStream(), true);
            }
            else
                System.out.println("client socket is null");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // -----------------------------------------------------------
    public void receiveMessages() {
        String line = "";

        try {
            while (true) {
                line = br.readLine();
                if (line != null)
                    ui.addTextToTextArea("Server : " + line + "\n");
            }
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
