package com.github.platinumrondo.shavedwords.gui;

import javax.swing.*;

/**
 * Launch the main app.
 */
public class DictGuiMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new DictGui();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}
