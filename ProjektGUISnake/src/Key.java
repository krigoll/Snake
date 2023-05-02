import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Key implements KeyListener {

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP -> {
                if (GamePanel.movement != Movements.DOWN) {
                    GamePanel.setMovement(Movements.UP);
                }
            }
            case KeyEvent.VK_DOWN -> {
                if (GamePanel.movement != Movements.UP) {
                    GamePanel.setMovement(Movements.DOWN);
                }
            }
            case KeyEvent.VK_LEFT -> {
                if (GamePanel.movement != Movements.RIGHT) {
                    GamePanel.setMovement(Movements.LEFT);
                }
            }
            case KeyEvent.VK_RIGHT -> {
                if (GamePanel.movement != Movements.LEFT) {
                    GamePanel.setMovement(Movements.RIGHT);
                }
            }
            case KeyEvent.VK_ENTER -> {
                if (!GamePanel.isRunning) {
                    GamePanel.snakeBody = GamePanel.initialSnakeBody;
                    GamePanel.movement = Movements.RIGHT;
                    GamePanel.setScoreCounter0();
                    GamePanel.getInstance().setSnakeX0();
                    GamePanel.getInstance().setSnakeY0();
                    GamePanel.getInstance().start();
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
