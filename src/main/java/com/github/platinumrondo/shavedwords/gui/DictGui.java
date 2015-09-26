package com.github.platinumrondo.shavedwords.gui;

import com.github.platinumrondo.shavedwords.DictClient;
import com.github.platinumrondo.shavedwords.gui.cards.DefineCard;
import com.github.platinumrondo.shavedwords.gui.cards.LoadingCard;
import com.github.platinumrondo.shavedwords.gui.cards.MatchCard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

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
    //dict client
    private DictClient client;
    private boolean closeEnabled;

    public DictGui() {
        initComponents();
        setSize(500, 400);
        setTitle("shavedwords");
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        closeEnabled = true;
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!closeEnabled)
                    return;
                e.getWindow().setVisible(false);
                try {
                    if (client != null && client.isConnected())
                        client.quit();
                } catch (IOException ex) {
                    //TODO bad practice!
                    System.err.println(ex);
                }
                e.getWindow().dispose();
            }
        });
        client = new DictClient("dict.org", 2628);
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
        new DefineSearch(text).execute();
    }

    private void connectToServerIfNecessary() throws IOException {
        if (client == null)
            client = new DictClient("dict.org", 2628);
        if (!client.isConnected()) {
            client.connect();
            client.client("shavedwords");
        }
    }

    private class DefineSearch extends SwingWorker<String[], Void> {
        private final String word;

        public DefineSearch(String txt) {
            super();
            this.word = txt;
        }

        @Override
        protected String[] doInBackground() throws Exception {
            closeEnabled = false;
            connectToServerIfNecessary();
            return client.define("*", word);
        }

        @Override
        protected void done() {
            try {
                String[] strs = get();
                if (strs.length == 0) {
                    System.out.println("DefineSearch: no results...");
                    new MatchSearch(word).execute();
                } else {
                    defineCard.setContent(strs);
                    cardLayout.show(contentPanel, "define");
                    closeEnabled = true;
                    searchField.getAction().setEnabled(true);
                }
            } catch (Exception e) {
                //TODO get jframe reference for this dialog
                JOptionPane.showMessageDialog(null, e.getMessage());
                closeEnabled = true;
                searchField.getAction().setEnabled(true);
            }
        }
    }

    private class MatchSearch extends SwingWorker<String[], Void> {
        private final String word;

        public MatchSearch(String txt) {
            super();
            this.word = txt;
        }

        @Override
        protected String[] doInBackground() throws Exception {
            closeEnabled = false;
            connectToServerIfNecessary();
            String match = client.match("*", "prefix", word);
            return match.split("\n");
        }

        @Override
        protected void done() {
            try {
                String[] matchLines = get();
                matchCard.setList(matchLines);
                cardLayout.show(contentPanel, "match");
            } catch (Exception e) {
                //TODO get jframe reference for this dialog
                JOptionPane.showMessageDialog(null, e.getMessage());

            }
            closeEnabled = true;
            searchField.getAction().setEnabled(true);
        }
    }
}
