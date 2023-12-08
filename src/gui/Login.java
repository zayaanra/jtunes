package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Login extends JPanel {
    private GridBagConstraints c = new GridBagConstraints();

    // Needed to query database and log the user in or register new credentials into the database
    public JTextField username = new JTextField(15);
    public JPasswordField password = new JPasswordField(15);
    
    public JButton loginBtn = new JButton("Login");
    public JLabel switchToReg = new JLabel("<html><u>Register</u></html>");

    public Login() {
        this.setLayout(new GridBagLayout());

        JLabel loginLabel = new JLabel("Login");
        loginLabel.setFont(new Font("Sans Serif", Font.PLAIN, 32));

        this.c.gridx = 0;
        this.c.gridy = 0;
        this.c.ipady = 40;
        this.add(loginLabel, c);

        this.c.gridy = 1;
        this.c.ipady = 5;
        this.add(this.username, c);

        this.c.gridy = 2;
        
        this.add(this.password, c);

        
        this.c.gridy = 5;
        this.c.weighty = 1;
        this.add(this.loginBtn, c);

        this.c.gridy = 10;
        this.add(this.switchToReg, c);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
            }
        });

    }

}