package Service;

import Ui.ClientUi;

import java.io.*;
import java.net.Socket;

public class Client implements ClientUi.ClickCallback {
    private static int clientNum = 1;
    private BufferedReader br = null;
    private ClientUi ui;
    private Socket socket;
    private PrintWriter out;

    // -----------------------------------------------------------
    public Client(Socket socket, String userName) {
        this.socket = socket;
        initStreams();
        ui = new ClientUi(userName, 100, 100, this);
        sendMessage("man zendam");
        receiveMessages();
    }

    // -----------------------------------------------------------
    public void initStreams() {
        try {
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
