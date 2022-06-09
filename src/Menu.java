import javax.swing.*;

public class Menu {

    public static void Create() {
        JFrame gameWindow = new JFrame("Wordle Game");
        gameWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gameWindow.setSize(400, 400);
        gameWindow.setVisible(true);
        gameWindow.setLocationRelativeTo(null);
        gameWindow.setResizable(false);

        var panel = new JPanel();
        gameWindow.add(panel);

        var keyboardButton = new JButton("Keyboard");
        keyboardButton.addActionListener(e -> {
            new Game(false);
            gameWindow.dispose();
        });

        var mouseButton = new JButton("Mouse");
        mouseButton.addActionListener(e -> {
            new Game(true);
            gameWindow.dispose();
        });


        panel.add(keyboardButton);
        panel.add(mouseButton);
    }
}