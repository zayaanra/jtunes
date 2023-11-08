package gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class CustomEntry extends JTextField implements FocusListener {
    private final String custom;

    public CustomEntry(String custom) {
        super(custom, 15);

        addFocusListener(this);

        this.custom = custom;
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (super.getText().equals(custom)) {
            super.setText("");
            super.setForeground(Color.BLACK);
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (super.getText().isEmpty()) {
            super.setForeground(Color.GRAY);
            super.setText(custom);
        }
    }
}
