package com.github.platinumrondo.shavedwords.gui.cards;

import javax.swing.*;
import java.awt.*;

/**
 * Show the definition(s) of the word searched.
 */
public class DefineCard extends JPanel {
    private JTextArea contentArea;
    private JScrollPane scrollPane;

    public DefineCard() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        contentArea = new JTextArea();
        contentArea.setEditable(false);
        scrollPane = new JScrollPane(contentArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setContent(String[] strs) {
        StringBuilder sb = new StringBuilder();
        for (String s : strs) {
            sb.append(s);
            sb.append("\n----------\n");
        }
        contentArea.setText(sb.toString());
        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMinimum());
        scrollPane.getHorizontalScrollBar().setValue(scrollPane.getHorizontalScrollBar().getMinimum());
    }
}
