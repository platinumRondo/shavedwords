package com.github.platinumrondo.shavedwords.gui;

import com.github.platinumrondo.shavedwords.DictClient;
import com.github.platinumrondo.shavedwords.gui.cards.DefineCard;
import com.github.platinumrondo.shavedwords.gui.cards.LoadingCard;
import com.github.platinumrondo.shavedwords.gui.cards.MatchCard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * The main gui/app.
 */
public class DictGui extends JFrame {
    private JTextField searchField;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    //CARD s
    private DefineCard defineCard;
    private MatchCard matchCard;

    public DictGui() {
        initComponents();
        setSize(500, 400);
        setTitle("shavedwords");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;
        searchField = new JTextField();
        searchField.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lockAndSearch();
            }
        });
        add(searchField, c);
        c.gridy = 1;
        c.weighty = 1;
        c.insets = new Insets(0, 4, 4, 4);
        c.fill = GridBagConstraints.BOTH;
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        LoadingCard loadingCard = new LoadingCard();
        contentPanel.add(loadingCard);
        cardLayout.addLayoutComponent(loadingCard, "loading");
        defineCard = new DefineCard();
        contentPanel.add(defineCard);
        cardLayout.addLayoutComponent(defineCard, "define");
        matchCard = new MatchCard();
        contentPanel.add(matchCard);
        cardLayout.addLayoutComponent(matchCard, "match");
        cardLayout.show(contentPanel, "define");
        add(contentPanel, c);
    }

    private void lockAndSearch() {
        searchField.getAction().setEnabled(false);
        String text = searchField.getText();
        if (text.trim().compareTo("") == 0) {
            searchField.getAction().setEnabled(true);
            return;
        }
        //send search
        cardLayout.show(contentPanel, "loading");
        new Searcher(text).execute();
    }

    private class Searcher extends SwingWorker<String[], Void> {
        private final String word;

        public Searcher(String txt) {
            super();
            this.word = txt;
        }

        @Override
        protected String[] doInBackground() throws Exception {
            DictClient dc = new DictClient("dict.org", 2628);
            dc.connect();
            dc.client("shavedwords");
            String[] definition = dc.define("*", word);
            dc.quit();
            return definition;
        }

        @Override
        protected void done() {
            try {
                //TODO if empty go with match query
                String[] strs = get();
                defineCard.setContent(strs);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
            cardLayout.show(contentPanel, "define");
            searchField.getAction().setEnabled(true);
        }
    }
}
