import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.awt.event.*;
import java.awt.dnd.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.awt.datatransfer.*;
import java.util.Locale;
import java.util.Random;
import javax.swing.TransferHandler;

public class Game implements KeyListener {

    JFrame gameWindow = new JFrame("Wordle Game");
    Word[] Words = new Word[Config.totalGuess];
    int currentWord = 0;
    JPanel gamePanel;
    JPanel keyboardPanel;
    Timer timer;
    JLabel timerLabel = new JLabel("0:0");
    int timerSecond = 0;
    String theWord = getWordleString();
    boolean isGameActive = false;
    boolean isKeyboard;

    Game(boolean isKeyboard) {
        System.out.println("The Word: " + theWord);
        this.isKeyboard = isKeyboard;

        gameWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gameWindow.setSize(Config.windowWidth, Config.windowHeight);
        gameWindow.setVisible(true);
        gameWindow.setLayout(null);
        gameWindow.setLocationRelativeTo(null);
        gameWindow.addKeyListener(this);
        // gameWindow.setLayout(new BoxLayout(gameWindow, BoxLayout.Y_AXIS));
        gameWindow.setResizable(false);

        // Timer
        JPanel timerPanel = new JPanel();
        timerPanel.setSize(Config.windowWidth, 50);
        timerPanel.add(timerLabel);
        gameWindow.add(timerPanel);

        timer = new Timer(1000, (e) -> {
            updateTimer();
        });

        // Game Panel
        var wordLayout = new GridLayout(Words.length, 1, 0, 20);

        gamePanel = new JPanel();
        gamePanel.setSize(Config.windowWidth, Config.windowHeight / 3 * 2);
        gamePanel.setLocation(0, 50);
        gamePanel.setBorder(new EmptyBorder(10, 100, 10, 100));
        gamePanel.setLayout(wordLayout);
        gamePanel.setDropTarget(new DropTarget() {
            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    var l = (String) dtde.getTransferable().getTransferData(
                            new DataFlavor("application/x-java-jvm-local-objectref; class=java.lang.String"));
                    char key = l.toCharArray()[0];
                    handleKeyPress((int) key);
                    System.out.println("drop detected from " + l);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        gameWindow.add(gamePanel);

        for (int i = 0; i < Words.length; i++) {
            Words[i] = new Word();
            var panel = Words[i].createPanel();
            gamePanel.add(panel);
        }

        if (isKeyboard) {
            // Keyboard Panel
            var keyboardLayout = new FlowLayout();

            keyboardPanel = new JPanel();
            keyboardPanel.setLayout(keyboardLayout);
            keyboardPanel.setSize(Config.windowWidth, Config.windowHeight / 3);
            keyboardPanel.setLocation(0, gamePanel.getHeight() + 50);

            for (char c : Config.validChars) {
                var text = String.valueOf(c).toUpperCase(Locale.ENGLISH);
                var button = new JButton(text);
                button.setFocusable(false);
                button.setTransferHandler(new TransferHandler("text"));
                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        var c = (JButton) e.getSource();
                        var handler = c.getTransferHandler();
                        handler.exportAsDrag(c, e, TransferHandler.COPY);
                    }
                });
                keyboardPanel.add(button);
            }

            var buttonEnter = new JButton("Enter");
            buttonEnter.setFocusable(false);
            buttonEnter.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    handleKeyPress(10);
                }
            });
            keyboardPanel.add(buttonEnter);

            var buttonBackspace = new JButton("Backspace");
            buttonBackspace.setFocusable(false);
            buttonBackspace.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    handleKeyPress(8);
                }
            });
            keyboardPanel.add(buttonBackspace);
            gameWindow.add(keyboardPanel);
        }

    }

    public void updateTimer() {
        timerSecond++;
        var minutes = (int) Math.floor(timerSecond / 60);
        var seconds = timerSecond % 60;
        timerLabel.setText(minutes + ":" + seconds);
    }

    public void setGameStatus(boolean status) {
        isGameActive = status;
        if (status == true) {
            timer.start();
            return;
        }
        timer.stop();

        try {
            Path path = Paths.get(".\\assets\\highscore.txt");
            var highScore = Integer.parseInt(Files.readString(path).trim());

            if (highScore > timerSecond) {
                PrintWriter writer = new PrintWriter(".\\assets\\highscore.txt", "UTF-8");
                writer.println(timerSecond);
                writer.close();
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public String getWordleString() {
        Random random = new Random();
        int position = random.nextInt(Config.wordList.size());
        return Config.wordList.get(position).trim().toLowerCase(Locale.ENGLISH);
    }

    public void gameOver(boolean win) {
        if (win) {
            try {
                Path path = Paths.get(".\\assets\\highscore.txt");
                var highScore = Integer.parseInt(Files.readString(path).trim());

                JOptionPane.showMessageDialog(null, "🎉 Highest Score :" + highScore, "Congrats",
                        JOptionPane.INFORMATION_MESSAGE);
                return;

            } catch (Exception e) {
                System.out.println(e);
            }
        }

        JOptionPane.showMessageDialog(null, "You lost :( the word was : "+theWord, "loser :(", JOptionPane.INFORMATION_MESSAGE);
    }

    public void handleKeyPress(int keyCode) {

        var key = (char) keyCode;

        boolean contains = false;
        for (char c : Config.validChars) {
            if (c == Character.toLowerCase(key)) {
                contains = true;
                break;
            }
        }

        var word = Words[currentWord];

        if (contains && word.currentLetter < Config.wordLength) {
            setGameStatus(true);
            word.EnterChar(Character.toLowerCase(key));
            return;
        }

        if (!isGameActive) {
            return;
        }

        if (keyCode == 10) { // Enter
            if (word.currentLetter != Config.wordLength) {
                System.out.println("You need to finish the word!");
                return;
            }

            if (!Config.wordList.contains(word.triedWord.toLowerCase())) {
                System.out.println(Config.wordList);
                System.out.println(word.triedWord);
                System.out.println("Invalid word");
                return;
            }

            var result = word.Submit(theWord);

            if (currentWord + 1 == Config.totalGuess || result == true) {
                setGameStatus(false);
                gameOver(result);
                return;
            }

            if (currentWord < Config.totalGuess) {
                currentWord += 1;
            }

            return;
        }

        if (keyCode == 8) { // Backspace
            word.DeleteChar();
            return;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!isKeyboard) {
            var keyCode = e.getKeyCode();
            handleKeyPress(keyCode);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }
}
