package translator.jcodez;


import javax.swing.*;
import java.io.File;
import java.util.Locale;

public class Main {
    public static final File dir = new File(System.getProperty("user.home")+ File.separator+ "JCodez"+ File.separator +"data");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Translator::new);
    }
}