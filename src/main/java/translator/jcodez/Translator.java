package translator.jcodez;

import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;
import java.awt.*;

public class Translator {
    private final JTextArea inputArea;
    private final JTextArea outputArea;
    private final JButton translateButton;
    private String previousResults;

    public Translator() {
        FlatDarculaLaf.install();
        JFrame frame = new JFrame("Translator || "+TranslateUtils.readLanguage());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLayout(new BorderLayout());

        inputArea = new JTextArea();
        inputArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        translateButton = new JButton("Translate");
        frame.getRootPane().setDefaultButton(translateButton);
        JButton clearButton = new JButton("Clear");
        JButton languageButton = new JButton("Language");

        translateButton.addActionListener(e -> {
            translateButton.setEnabled(false);
            outputArea.setText("Translating...");

            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    String input = inputArea.getText();
                    translate(input);
                    return null;
                }

                @Override
                protected void done() {
                    translateButton.setEnabled(true);
                    outputArea.setText(previousResults);
                }
            };

            worker.execute();
        });

        clearButton.addActionListener(e -> {
            outputArea.setText("");
            previousResults = "";
        });

        languageButton.addActionListener(e -> {
            TranslateUtils.selectLanguage();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(languageButton);
        buttonPanel.add(translateButton);
        buttonPanel.add(clearButton);

        frame.add(new JScrollPane(inputArea), BorderLayout.NORTH);
        frame.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
        previousResults = "";
    }

    private void translate(String input) {
        try {
            Thread.sleep(2000);

            String translatedText = performTranslation(input);

            previousResults += "Translated: " + translatedText + "\n";
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String performTranslation(String input) {
        String langFrom;

        if(TranslateUtils.isCustom()) {
            langFrom = TranslateUtils.readLanguage();
        }
        else {
            langFrom = "";
        }

        String langTo = "english";

        String translated = TranslateUtils.translate(input, langFrom, langTo);

        if (translated == null) {
            return "Translation failed";
        }

        return translated;
    }
}