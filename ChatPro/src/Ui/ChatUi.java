package Ui;

import javax.swing.*;
import java.awt.*;

public class ChatUi extends JFrame {
    private JPanel panelMessage;
    private JPanel panelReceiver;
    private JPanel panel;
    private JLabel labelMessage;
    JLabel labelReceiver;
    private JTextField tfMessage;
    private JTextField tfReceiver;
    private JButton send;
    private JTextArea ta;
    private ClickCallback clickCallback;
    // -----------------------------------------------------------

    public ChatUi(String windowName, int x, int y, ClickCallback clickCallback) {
        initComponents();
        setTitle(windowName);
        setLocation(x, y);
        this.clickCallback = clickCallback;
    }
    // -----------------------------------------------------------

    public void initComponents() {
        //Creating the panelMessage at bottom and adding components
        panelMessage = new JPanel();
        labelMessage = new JLabel("Enter Message");
        tfMessage = new JTextField(20);
        send = new JButton("Send");
        panelMessage.add(labelMessage);
        panelMessage.add(tfMessage);
        panelMessage.add(send);

        panelReceiver = new JPanel();
        labelReceiver = new JLabel("Receiver : ");
        tfReceiver = new JTextField(20);
        panelReceiver.add(labelReceiver);
        panelReceiver.add(tfReceiver);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(panelReceiver);
        panel.add(panelMessage);

        ta = new JTextArea();

        //Adding Components to the frame.
        getContentPane().add(BorderLayout.SOUTH, panel);
        getContentPane().add(BorderLayout.CENTER, ta);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setVisible(true);

        // Action Listeners
        send.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSendActionPerformed(evt);
            }
        });
    }
    // -----------------------------------------------------------

    public synchronized void addTextToTextArea(String text) {
        ta.append(text);
    }
    // -----------------------------------------------------------

    private void jButtonSendActionPerformed(java.awt.event.ActionEvent evt) {
        clickCallback.onClick(tfMessage.getText(), tfReceiver.getText());
        addTextToTextArea("Me : " + tfMessage.getText() + "\n");
        tfMessage.setText("");
    }

    // -----------------------------------------------------------
    public interface ClickCallback {
        void onClick(String message, String receiver);
    }
}