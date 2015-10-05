package com.github.platinumrondo.shavedwords.gui.cards;

import com.github.platinumrondo.shavedwords.MatchResult;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    public void setList(Set<MatchResult> strs) {
        //TODO think of something better
        List<String> strl = new ArrayList<>();
        for (MatchResult mr : strs) {
            strl.add(mr.getWord());
        }
        matchList.setListData(strl.toArray(new String[strl.size()]));
        scrollPane.getHorizontalScrollBar().setValue(scrollPane.getHorizontalScrollBar().getMinimum());
        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMinimum());
    }
}
