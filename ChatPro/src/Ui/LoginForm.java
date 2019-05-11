package Ui;

import javax.swing.*;
import java.awt.*;

public class LoginForm {
    private JFrame frame;
    private JPanel panel;
    private JTextField tfIP;
    private JTextField tfPort;
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JButton loginButton;

    // -----------------------------------------------------------

    public LoginForm() {
        initLoginForm();
        // Action listeners
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });

    }
    // -----------------------------------------------------------
    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {

    }
    // -----------------------------------------------------------
    public void initLoginForm() {
        GridBagLayout layout;
        GridBagConstraints cs;
        frame = new JFrame("Login Form");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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

        tfIP = new JTextField(20);
        cs.gridx = 1;
        cs.gridy = 0;
        cs.gridwidth = 2;
        panel.add(tfIP, cs);

        JLabel lbPort = new JLabel("Port: ");
        cs.gridx = 0;
        cs.gridy = 1;
        cs.gridwidth = 1;
        panel.add(lbPort, cs);

        tfPort = new JTextField(20);
        cs.gridx = 1;
        cs.gridy = 1;
        cs.gridwidth = 2;
        panel.add(tfPort, cs);

        JLabel lbUsername = new JLabel("Username: ");
        cs.gridx = 0;
        cs.gridy = 2;
        cs.gridwidth = 1;
        panel.add(lbUsername, cs);

        tfUsername = new JTextField(20);
        cs.gridx = 1;
        cs.gridy = 2;
        cs.gridwidth = 2;
        panel.add(tfUsername, cs);

        JLabel lbPassword = new JLabel("Password: ");
        cs.gridx = 0;
        cs.gridy = 3;
        cs.gridwidth = 1;
        panel.add(lbPassword, cs);

        pfPassword = new JPasswordField(20);
        cs.gridx = 1;
        cs.gridy = 3;
        cs.gridwidth = 2;
        panel.add(pfPassword, cs);

        loginButton = new JButton("Login");
        JPanel bp = new JPanel();
        bp.add(loginButton);

        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.getContentPane().add(bp, BorderLayout.PAGE_END);

        frame.add(panel);
        frame.setSize(1400, 400);
        frame.pack();
        frame.setVisible(true);
    }
}
