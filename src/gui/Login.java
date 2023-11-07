package gui;

import javax.swing.*;
import java.awt.*;

public class Login extends JPanel {
    private GridBagConstraints c = new GridBagConstraints();

    // Needed to query database and log the user in or register new credentials into the database
    private JTextField username = new JTextField(15);
    private JTextField password = new JTextField(15);
    private JButton loginBtn = new JButton("Login");

    public JButton switchToReg = new JButton("Register");
    //private JLabel warning = new JLabel("");

    public Login() {
        this.setLayout(new GridBagLayout());

        JLabel loginLabel = new JLabel("Login");
        loginLabel.setFont(new Font("Sans Serif", Font.PLAIN, 32));

        // JLabel usernameLabel = new JLabel("Username");
        // usernameLabel.setFont(new Font("Sans Serif", Font.PLAIN, 14));

        // JLabel passwordLabel = new JLabel("Password");
        // passwordLabel.setFont(new Font("Sans Serif", Font.PLAIN, 14));

        this.loginBtn.addActionListener((e) -> {
            // TODO - Authenticate user by querying database
        });

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
        this.add(this.loginBtn, c);

        this.c.gridy = 10;
        this.add(this.switchToReg, c);

        

    }

}