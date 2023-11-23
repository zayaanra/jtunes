package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Register extends JPanel {
    private GridBagConstraints c = new GridBagConstraints();

    public JTextField username = new JTextField(15);
    public JPasswordField password = new JPasswordField(15);
    public JPasswordField confirmation = new JPasswordField(15);

    public JButton registerBtn = new JButton("Register");
    public JLabel switchToLog = new JLabel("<html><u>Login</u></html>");

    public Register() {
        this.setLayout(new GridBagLayout());

        JLabel registerLabel = new JLabel("Register");
        registerLabel.setFont(new Font("Sans Serif", Font.PLAIN, 32));

        this.c.gridx = 0;
        this.c.gridy = 0;
        this.c.ipady = 40;
        this.add(registerLabel, c);

        this.c.gridy = 1;
        this.c.ipady = 5;
        this.add(this.username, c);

        this.c.gridy = 2;
        this.add(this.password, c);

        this.c.gridy = 3;
        this.add(this.confirmation, c);

        
        this.c.gridy = 5;
        this.c.weighty = 1;
        this.add(this.registerBtn, c);

        this.c.gridy = 10;
        this.add(this.switchToLog, c);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
            }
        });

    }
    
}
