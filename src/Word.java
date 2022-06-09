import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class Word {
    JLabel[] letters = new JLabel[Config.wordLength];
    int currentLetter = 0;
    String triedWord = "";

    public JPanel createPanel() {
        var panel = new JPanel();
        var wordLayout = new GridLayout(1, letters.length);
        panel.setLayout(wordLayout);

        for (int i = 0; i < letters.length; i++) {
            var label = new JLabel();
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setVerticalAlignment(JLabel.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 24));
            label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            letters[i] = label;
            panel.add(label);
        }

        return panel;
    }

    public boolean Submit(String theWord) {
        System.out.println("Submiting...");

        for (int i = 0; i < triedWord.length(); i++) {
            var triedChar = triedWord.charAt(i);
            var actualChar = theWord.charAt(i);

            letters[i].setOpaque(true);
            System.out.println(theWord.indexOf(triedChar));

            if (triedChar == actualChar) {
                letters[i].setBackground(Color.GREEN);
            } else if (theWord.indexOf(triedChar) != -1) {
                letters[i].setBackground(Color.YELLOW);
            } else {
                letters[i].setBackground(Color.RED); 
            }
        }

        if (triedWord.equals(theWord)) {
            System.out.println("You won!");
            return true;
        } else {
            System.out.println("Try again!");
            return false;
        }
    }

    public void EnterChar(char key) {
        var strKey = Character.toString(key);
        letters[currentLetter].setText(strKey.toUpperCase(Locale.ENGLISH));
        System.out.println("Entering new Char: " + key);
        triedWord += strKey;
        System.out.println("Current Word: " + triedWord);
        if (currentLetter < Config.wordLength) {
            currentLetter += 1;
            System.out.println("Current Letter: " + currentLetter);
        }
    }

    public void DeleteChar() {
        System.out.println("Deleting char");
        if (currentLetter > 0) {
            currentLetter -= 1;
        }
        letters[currentLetter].setText("");
        triedWord = triedWord.substring(0, currentLetter);
        System.out.println("Current Word: " + triedWord);
    }
}
