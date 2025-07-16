import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    private static final int BOARD_WIDTH = 360, BOARD_HEIGHT = 640;
    private Image bg, birdImg, topPipeImg, bottomPipeImg;
    private class Bird { int x, y, w = 34, h = 24; Image img; Bird(Image i){ img=i; x=BOARD_WIDTH/8; y=BOARD_HEIGHT/2;} }
    private class Pipe { int x=BOARD_WIDTH, y, w=64, h=512; Image img; boolean passed = false; Pipe(Image i){ img=i; } }
    private Bird bird;
    private ArrayList<Pipe> pipes = new ArrayList<>();
    private Timer gameLoop, pipeTimer;
    private int velY = 0, gravity = 1, velX = -4;
    private boolean started = false, gameOver = false;
    private static int highScore = 0;
    private int score = 0;

    public FlappyBird() {
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setFocusable(true);
        addKeyListener(this);

        bg = new ImageIcon(getClass().getResource("flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("bottompipe.png")).getImage();

        bird = new Bird(birdImg);

        pipeTimer = new Timer(1500, e -> placePipes());
        gameLoop = new Timer(1000 / 60, this);
    }

    private void placePipes() {
        int y = -128 - (int)(Math.random() * 256);
        Pipe top = new Pipe(topPipeImg);
        top.y = y;
        Pipe bot = new Pipe(bottomPipeImg);
        bot.y = y + 512 + BOARD_HEIGHT / 4;
        pipes.add(top);
        pipes.add(bot);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bg, 0, 0, BOARD_WIDTH, BOARD_HEIGHT, null);
        g.drawImage(bird.img, bird.x, bird.y, bird.w, bird.h, null);
        for (Pipe p : pipes) {
            g.drawImage(p.img, p.x, p.y, p.w, p.h, null);
        }

        g.setFont(new Font("Arial", Font.BOLD, 32));
        g.setColor(Color.white);

        if (!started) {
            g.drawString("Press SPACE to Start", 30, BOARD_HEIGHT / 2);
        } else if (gameOver) {
            g.drawString("Game Over", 100, 200);
            g.drawString("Score: " + score, 100, 250);
            g.drawString("High Score: " + highScore, 80, 300);
            g.drawString("Press SPACE to Retry", 15, 400);
        } else {
            g.drawString(String.valueOf(score), 10, 35);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!started || gameOver) return;

        velY += gravity;
        bird.y += velY;

        for (Pipe p : pipes) {
            p.x += velX;
            if (!p.passed && bird.x > p.x + p.w) {
                score++;
                p.passed = true;
            }
            if (collision(bird, p)) gameOver = true;
        }

        if (bird.y > BOARD_HEIGHT) gameOver = true;

        if (gameOver) {
            pipeTimer.stop();
            gameLoop.stop();
            highScore = Math.max(highScore, score);
        }

        repaint();
    }

    private boolean collision(Bird b, Pipe p) {
        return b.x < p.x + p.w && b.x + b.w > p.x &&
               b.y < p.y + p.h && b.y + b.h > p.y;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!started) {
                started = true;
                gameLoop.start();
                pipeTimer.start();
            } else if (gameOver) {
                reset();
                started = false; // back to start screen
            }
            velY = -9;
        }
    }

    private void reset() {
        gameOver = false;
        score = 0;
        bird.y = BOARD_HEIGHT / 2;
        velY = 0;
        pipes.clear();
        repaint();
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Flappy Bird");
            FlappyBird panel = new FlappyBird();
            f.add(panel);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setResizable(false);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setVisible(true);
            panel.requestFocusInWindow();
        });
    }
}
