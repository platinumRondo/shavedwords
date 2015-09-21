package com.github.platinumrondo.shavedwords.gui.cards;

import javax.swing.*;
import java.awt.*;

/**
 * Show a list of possible words similar to the one provided by the user.
 */
public class MatchCard extends JPanel {
    private JList<String> matchList;
    private JScrollPane scrollPane;

    public MatchCard() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        matchList = new JList<>();
        scrollPane = new JScrollPane(matchList);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setList(String[] strs) {
        matchList.setListData(strs);
        scrollPane.getHorizontalScrollBar().setValue(scrollPane.getHorizontalScrollBar().getMinimum());
        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMinimum());
    }
}
