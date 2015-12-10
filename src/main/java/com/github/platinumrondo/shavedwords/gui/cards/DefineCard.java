package com.github.platinumrondo.shavedwords.gui.cards;

import com.github.platinumrondo.shavedwords.DefineResult;

import javax.swing.*;
import java.awt.*;

/**
 * Show the definition(s) of the word searched.
 */
public class DefineCard extends JPanel {
    private JEditorPane contentArea;
    private JScrollPane scrollPane;

    public DefineCard() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        contentArea = new JEditorPane();
        contentArea.setContentType("text/html");
        contentArea.setEditable(false);
        scrollPane = new JScrollPane(contentArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setContent(DefineResult[] strs) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        for (DefineResult s : strs) {
            sb.append("<b>");
            sb.append(s.getWord());
            sb.append("</b><br/>");
            sb.append(s.getDefinition().replace("\n", "<br/>"));
            sb.append("<br/><hr/><br/>");
        }
        contentArea.setText(sb.toString());
        contentArea.setCaretPosition(0);
    }

}
