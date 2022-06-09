import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Config {
    static final int windowHeight = 1000;
    static final int windowWidth = 720;
    static final int wordLength = 5;
    static final int totalGuess = 5;
    static final char[] validChars = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
    static final List<String> wordList = loadWords();

    private static List<String> loadWords() {
        Path path = Paths.get(".\\assets\\Words.txt");
        List<String> wordList = new ArrayList<>();
        try {
            wordList = Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wordList;
    }
}
