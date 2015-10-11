package com.github.platinumrondo.shavedwords.gui.cards;

import com.github.platinumrondo.shavedwords.DefineResult;

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

    public void setContent(DefineResult[] strs) {
        StringBuilder sb = new StringBuilder();
        for (DefineResult s : strs) {
            sb.append(s.getDefinition());
            sb.append("\n----------\n");
        }
        contentArea.setText(sb.toString());
        contentArea.setCaretPosition(0);
    }

}
