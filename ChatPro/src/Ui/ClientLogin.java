package Ui;

import Service.Client;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientLogin extends JFrame {
    BufferedReader br = null;
    //    private JFrame frame;
    private JPanel panel;
    private JTextField tfIP;
    private JTextField tfPort;
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JButton loginButton;
    private String userName;
    private PrintWriter out;
    private Socket socket = null;


    // server reponses
    private String OK = "OK.";
    private String wrongPass = "Password is wrong!";
    private String wrongUserName = "There is no such user name!";

    // -----------------------------------------------------------
    public ClientLogin() {
        initLoginForm();

        // Action listeners
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });

    }

    // -----------------------------------------------------------
    public boolean initClient(String ip, int port) {
        boolean connectionReady = false;
        try {
            socket = new Socket(ip, port);
            if (socket.isConnected()) {
                // stream reader for read from server
                initStreams();
                connectionReady = true;
            }

        } catch (ConnectException e) {
            JOptionPane.showMessageDialog(null, "Server not found!");

        } catch (UnknownHostException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Unknown Host Exception. Try again!");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return connectionReady;
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
    public void sendMessage(String message) {
        out.println(message);
    }

    // -----------------------------------------------------------
    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {
        userName = tfUsername.getText();
        if (initClient(tfIP.getText(), Integer.parseInt(tfPort.getText()))) {

            if (socket.isConnected()) {
                sendMessage(tfUsername.getText());
                sendMessage(pfPassword.getText());

                String response = "";
                try {
                    while (true) {
                        response = br.readLine();
                        if (response != null) {
                            if (response.equals(OK)) {
                                dispose();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new Client(socket, userName);
                                    }
                                }).start();

                            } else if (response.equals(wrongPass))
                                JOptionPane.showMessageDialog(null, wrongPass);
                            else if (response.equals(wrongUserName))
                                JOptionPane.showMessageDialog(null, wrongUserName);
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // -----------------------------------------------------------
    public void initLoginForm() {
        GridBagLayout layout;
        GridBagConstraints cs;
        setTitle("Login Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new JPanel();
        layout = new GridBagLayout();

        panel.setLayout(layout);
        cs = new GridBagConstraints();

        // Put constraints on different buttons
        cs.fill = GridBagConstraints.HORIZONTAL;

        JLabel lbIP = new JLabel("IP: ");
        cs.gridx = 0;
        cs.gridy = 0;
        cs.gridwidth = 1;
        panel.add(lbIP, cs);

        tfIP = new JTextField("localhost", 20);
        cs.gridx = 1;
        cs.gridy = 0;
        cs.gridwidth = 2;
        panel.add(tfIP, cs);

        JLabel lbPort = new JLabel("Port: ");
        cs.gridx = 0;
        cs.gridy = 1;
        cs.gridwidth = 1;
        panel.add(lbPort, cs);

        tfPort = new JTextField("6666", 20);
        cs.gridx = 1;
        cs.gridy = 1;
        cs.gridwidth = 2;
        panel.add(tfPort, cs);

        JLabel lbUsername = new JLabel("Username: ");
        cs.gridx = 0;
        cs.gridy = 2;
        cs.gridwidth = 1;
        panel.add(lbUsername, cs);

        tfUsername = new JTextField("reza", 20);
        cs.gridx = 1;
        cs.gridy = 2;
        cs.gridwidth = 2;
        panel.add(tfUsername, cs);

        JLabel lbPassword = new JLabel("Password: ");
        cs.gridx = 0;
        cs.gridy = 3;
        cs.gridwidth = 1;
        panel.add(lbPassword, cs);

        pfPassword = new JPasswordField("123", 20);
        cs.gridx = 1;
        cs.gridy = 3;
        cs.gridwidth = 2;
        panel.add(pfPassword, cs);

        loginButton = new JButton("Login");
        JPanel bp = new JPanel();
        bp.add(loginButton);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(bp, BorderLayout.PAGE_END);

        add(panel);
        pack();
        setVisible(true);
    }
    // -----------------------------------------------------------
}
