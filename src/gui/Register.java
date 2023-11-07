package gui;

import javax.swing.*;
import java.awt.*;

public class Register extends JPanel {
    private GridBagConstraints c = new GridBagConstraints();

    private JTextField username = new JTextField(15);
    private JTextField password = new JTextField(15);
    private JButton registerBtn = new JButton("Register");

    public JButton switchToLog = new JButton("Login");

    public Register() {
        this.setLayout(new GridBagLayout());

        JLabel registerLabel = new JLabel("Register");
        registerLabel.setFont(new Font("Sans Serif", Font.PLAIN, 32));

        this.registerBtn.addActionListener((e) -> {
            // TOOD - Register credentials into the database
        });

        this.c.gridx = 0;
        this.c.gridy = 0;
        this.c.ipady = 40;
        this.add(registerLabel, c);

        this.c.gridy = 1;
        this.c.ipady = 5;
        this.add(this.username, c);

        this.c.gridy = 2;
        this.add(this.password, c);

        
        this.c.gridy = 5;
        this.add(this.registerBtn, c);

        this.c.gridy = 10;
        this.add(this.switchToLog, c);

    }
    
}
