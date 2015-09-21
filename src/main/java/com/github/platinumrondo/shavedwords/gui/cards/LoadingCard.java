package com.github.platinumrondo.shavedwords.gui.cards;

import javax.swing.*;
import java.awt.*;

/**
 * Card that show a "loading" in the middle of the screen.
 * Maybe one day it will have a spinning thing near the text.
 */
public class LoadingCard extends JPanel {

    public LoadingCard() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        add(new JLabel("Loading...", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
