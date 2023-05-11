package translator.jcodez;

import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static translator.jcodez.Main.dir;

public class TranslateUtils {
    static String p = dir + File.separator + "languages.cfg";
    static JFrame frame;

    public static String translate(String text, String langFrom, String langTo) {
        String html = getHTML(text, langFrom, langTo);
        String translated = parseHTML(html);

        if (text.equalsIgnoreCase(translated))
            return null;

        return translated;
    }

    private static String getHTML(String text, String langFrom, String langTo) {
        URL url = createURL(text, langFrom, langTo);

        try {
            URLConnection connection = setupConnection(url);

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder html = new StringBuilder();

                String line;
                while ((line = br.readLine()) != null)
                    html.append(line).append("\n");

                return html.toString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } catch (IOException e) {
            return null;
        }
    }

    private static URL createURL(String text, String langFrom, String langTo) {
        try {
            String encodedText = URLEncoder.encode(text.trim(), StandardCharsets.UTF_8);

            String urlString = String.format(
                    "https://translate.google.com/m?hl=en&sl=%s&tl=%s&ie=UTF-8&prev=_m&q=%s",
                    langFrom, langTo, encodedText);

            return new URL(urlString);

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private static URLConnection setupConnection(URL url) throws IOException {
        URLConnection connection = url.openConnection();

        connection.setConnectTimeout(5000);
        connection.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

        return connection;
    }

    @SuppressWarnings("deprecation")
    private static String parseHTML(String html) {
        String regex = "class=\"result-container\">([^<]*)<\\/div>";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);

        Matcher matcher = pattern.matcher(html);
        matcher.find();
        String match = matcher.group(1);

        if (match == null || match.isEmpty())
            return null;

        return org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4(match);
    }

    public static void saveLanguage(String info) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(p))) {
            writer.write(info);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readLanguage() {

        try (BufferedReader reader = new BufferedReader(new FileReader(p))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            return content.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void selectLanguage() {
        FlatDarculaLaf.install();
        frame = new JFrame("Select Language");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton customButton = new JButton("Custom");
        JButton detectedButton = new JButton("Detected");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(customButton);
        buttonPanel.add(detectedButton);

        JLabel headingLabel = new JLabel("Select an option");
        headingLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(headingLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 0;
        panel.add(buttonPanel, gbc);

        frame.getContentPane().add(panel, BorderLayout.CENTER);

        customButton.addActionListener(e -> selectCustomLanguage());

        detectedButton.addActionListener(e -> {
            saveLanguage("Detected");
            frame.dispose();
        });

        frame.setSize(300, 170);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
}
    public static void selectCustomLanguage() {
        Locale[] locals = Locale.getAvailableLocales();
        String[] languageNames = new String[locals.length];
        for (int i = 0; i < locals.length; i++) {
            languageNames[i] = locals[i].getDisplayName();
        }

        JComboBox<String> combo = new JComboBox<>(languageNames);
        combo.setSelectedIndex(0);

        JOptionPane.showMessageDialog(
                null,
                combo,
                "Select a Language",
                JOptionPane.PLAIN_MESSAGE
        );

        String saveCustom = (String) combo.getSelectedItem();
        if (saveCustom != null) {
            Locale selectedLocale = null;
            for (Locale locale : locals) {
                if (locale.getDisplayName().equals(saveCustom)) {
                    selectedLocale = locale;
                    break;
                }
            }
            if (selectedLocale != null) {
                frame.dispose();
                saveLanguage(saveCustom);
            }
        }
    }
    public static boolean isCustom(){
        File e = new File(dir + File.separator + "languages.cfg");
        return !Objects.equals(readLanguage(), "Custom") && e.exists();
    }
}