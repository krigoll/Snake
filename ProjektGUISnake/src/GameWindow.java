import javax.swing.*;

public class GameWindow extends JFrame {
    public GameWindow() {
        this.add(GamePanel.getInstance());
        this.pack();
        this.setTitle("Snake");
        this.setVisible(true);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
