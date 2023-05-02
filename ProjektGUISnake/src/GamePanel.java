import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Random;

public class GamePanel extends JPanel {

    public static boolean isRunning = false;
    public static int initialSnakeBody = 5;
    public static int snakeBody = initialSnakeBody;
    public static Movements movement = Movements.RIGHT;

    private static final String FILE_NAME = "score.txt";
    private static GamePanel instance;
    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_HEIGHT = 500;
    private static final int DOT_SIZE = 25;
    private static final int ALL_DOTS = (WINDOW_WIDTH / DOT_SIZE) * (WINDOW_HEIGHT / DOT_SIZE);
    private static int scoreCounter = 0;

    private final Image apple = new ImageIcon("images/apple.png").getImage().getScaledInstance(DOT_SIZE, DOT_SIZE, Image.SCALE_SMOOTH);
    private final Image snakeRight = new ImageIcon("images/snakeRight.png").getImage().getScaledInstance(DOT_SIZE, DOT_SIZE, Image.SCALE_SMOOTH);
    private final Image snakeLeft = new ImageIcon("images/snakeLeft.png").getImage().getScaledInstance(DOT_SIZE, DOT_SIZE, Image.SCALE_SMOOTH);
    private final Image snakeUp = new ImageIcon("images/snakeUp.png").getImage().getScaledInstance(DOT_SIZE, DOT_SIZE, Image.SCALE_SMOOTH);
    private final Image snakeDown = new ImageIcon("images/snakeDown.png").getImage().getScaledInstance(DOT_SIZE, DOT_SIZE, Image.SCALE_SMOOTH);

    private int[] snakeX = new int[ALL_DOTS];
    private int[] snakeY = new int[ALL_DOTS];
    private int pointX = new Random().nextInt((WINDOW_WIDTH / DOT_SIZE)) * DOT_SIZE;
    private int pointY = new Random().nextInt((WINDOW_HEIGHT / DOT_SIZE)) * DOT_SIZE;
    private Timer timer;

    private GamePanel() {
        this.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.setBackground(new Color(255, 255, 255, 255));
        this.setFocusable(true);
        this.addKeyListener(new Key());
        start();
    }


    public static GamePanel getInstance() {
        if (instance == null) {
            instance = new GamePanel();
        }
        return instance;
    }


    @Override
    public void paint(Graphics g) {
        if (isRunning) {
            super.paint(g);
            drawPoint(g);
            drawSnake(g);
            scoreBoard(g);
            getPoint();
        } else {
            gameOver(g);
            saveScoreToFile();
        }
    }


    private void saveScoreToFile() {
        int score = getScoreCounter();
        int highestScore = readScoreFromFile();
        if (score > highestScore) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
                writer.write("" + score);
            } catch (IOException e) {
                System.out.println("Nie udalo sie zapisac do pliku!");
            }
        }
    }


    private int readScoreFromFile() {
        String score = "0";
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            score = reader.readLine();
        } catch (IOException e) {
            System.err.println("Nie udalo sie wczytac z pliku!");
        }
        return Integer.parseInt(score);
    }


    public void drawPoint(Graphics g) {
        g.setColor(new Color(194, 176, 31));
        g.drawImage(apple, pointX, pointY, null);
    }


    public void drawSnake(Graphics g) {
        if (movement == Movements.RIGHT) {
            g.drawImage(snakeRight, snakeX[0], snakeY[0], null);
        } else if (movement == Movements.LEFT) {
            g.drawImage(snakeLeft, snakeX[0], snakeY[0], null);
        } else if (movement == Movements.UP) {
            g.drawImage(snakeUp, snakeX[0], snakeY[0], null);
        } else if (movement == Movements.DOWN) {
            g.drawImage(snakeDown, snakeX[0], snakeY[0], null);
        }

        for (int i = 1; i < snakeBody; i++) {
            g.setColor(new Color(133, 210, 93));
            g.fillOval(snakeX[i], snakeY[i], DOT_SIZE, DOT_SIZE);
        }
    }


    public void scoreBoard(Graphics g) {
        g.setColor(new Color(171, 0, 0));
        g.setFont(new Font("Arial", Font.BOLD, 25));
        FontMetrics scoreBoardFont = getFontMetrics(g.getFont());
        g.drawString("Best score: " + readScoreFromFile(), WINDOW_WIDTH - scoreBoardFont.stringWidth("Best score: " + readScoreFromFile()), DOT_SIZE);
        g.drawString("Score: " + scoreCounter, WINDOW_WIDTH - scoreBoardFont.stringWidth("Score: " + scoreCounter), 2 * DOT_SIZE);
    }

    public void gameOver(Graphics g) {
        g.setColor(new Color(0, 38, 121));
        g.setFont(new Font("Arial", Font.BOLD, 50));
        FontMetrics gameOverFont = getFontMetrics(g.getFont());
        g.drawString("Game Over", (WINDOW_WIDTH - gameOverFont.stringWidth("Game Over")) / 2, WINDOW_HEIGHT / 2);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics gameRestart = getFontMetrics(g.getFont());
        g.drawString("Press ENTER to restart!", (WINDOW_WIDTH - gameRestart.stringWidth("Press ENTER to restart!")) / 2, WINDOW_HEIGHT / 2 + 30);
    }

    public void start() {
        if (timer != null) {
            timer.stop();
        }
        isRunning = true;
        timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                    if (isRunning) {
                        move();
                        checkCollisionWithWall();
                        checkCollisionWithSnake();
                    }
                    repaint();
            }
        });
        timer.start();
    }

    public void move() {
        for (int i = snakeBody; i > 0; i--) {
            snakeX[i] = snakeX[i - 1];
            snakeY[i] = snakeY[i - 1];
        }

        switch (movement) {
            case RIGHT -> snakeX[0] += DOT_SIZE;
            case LEFT -> snakeX[0] -= DOT_SIZE;
            case UP -> snakeY[0] -= DOT_SIZE;
            case DOWN -> snakeY[0] += DOT_SIZE;
        }
    }

    public void getPoint() {
        if (snakeX[0] == pointX && snakeY[0] == pointY) {
            setPointX();
            setPointY();
            snakeBody++;
            scoreCounter++;
            this.setBackground(new Color(new Random().nextInt(250 - 150) + 150,
                    new Random().nextInt(250 - 150) + 150,
                    new Random().nextInt(250 - 150) + 150, 255));
        }
    }

    public void checkCollisionWithWall() {
        if (snakeX[0] < 0) {
            isRunning = false;
        } else if (snakeX[0] >= WINDOW_WIDTH) {
            isRunning = false;
        } else if (snakeY[0] < 0) {
            isRunning = false;
        } else if (snakeY[0] >= WINDOW_HEIGHT) {
            isRunning = false;
        }
    }

    public void checkCollisionWithSnake() {
        for (int i = snakeBody; i > 0; i--) {
            if (snakeX[i] == snakeX[0] && snakeY[i] == snakeY[0]) {
                isRunning = false;
                break;
            }
        }
    }



    public static void setMovement(Movements movement) {
        GamePanel.movement = movement;
    }

    public void setPointX() {
        this.pointX = new Random().nextInt((WINDOW_WIDTH / DOT_SIZE)) * DOT_SIZE;
    }

    public void setPointY() {
        this.pointY = new Random().nextInt((WINDOW_HEIGHT / DOT_SIZE)) * DOT_SIZE;
    }

    public void setSnakeX0() {
        this.snakeX = new int[ALL_DOTS];
    }

    public void setSnakeY0() {
        this.snakeY = new int[ALL_DOTS];
    }

    public static void setScoreCounter0() {
        GamePanel.scoreCounter = 0;
    }

    public static int getScoreCounter() {
        return scoreCounter;
    }
}