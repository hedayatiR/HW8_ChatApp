package Ui;

import javax.swing.*;
import java.awt.*;

public class ChatUi extends JFrame {
    private JPanel panel;
    private JLabel label;
    private JTextField tf;
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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);

        //Creating the panel at bottom and adding components
        panel = new JPanel();
        label = new JLabel("Enter Text");
        tf = new JTextField(20);
        send = new JButton("Send");
        panel.add(label);
        panel.add(label);
        panel.add(tf);
        panel.add(send);

        ta = new JTextArea();

        //Adding Components to the frame.
        getContentPane().add(BorderLayout.SOUTH, panel);
        getContentPane().add(BorderLayout.CENTER, ta);
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
        clickCallback.onClick(tf.getText());
        addTextToTextArea("Me : " + tf.getText() + "\n");
        tf.setText("");
    }
    // -----------------------------------------------------------
    public interface ClickCallback{
        void onClick(String str);
    }
}