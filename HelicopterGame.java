/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hw_lab10;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class HelicopterGame extends JPanel implements ActionListener, KeyListener{
    //Game
    private int score = 0;
    private int timeLeft = 60;
    private boolean isGameOver = false;

    // Helicopter
    private int heliX = 350, heliY = 60;
    private int heliSpeed = 6;
    private boolean leftPressed = false, rightPressed = false;

    // Bomb
    private ArrayList<Point> bombs = new ArrayList<>();
    private int bombSpeed = 7;

    // Goal(Base)
    private int targetX = 350, targetY = 500, targetWidth = 60, targetHeight = 40;
    private Random random = new Random();

    private Timer gameTimer;
    private Timer secondTimer;

    public HelicopterGame() {
        setFocusable(true);
        addKeyListener(this);
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);

        // Timer game loop
        gameTimer = new Timer(16, this);
        gameTimer.start();

        // Timer Game
        secondTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isGameOver) {
                    timeLeft--;
                    if (timeLeft <= 0) {
                        isGameOver = true;
                        repaint();
                        gameTimer.stop();
                        secondTimer.stop();
                    }
                }
            }
        });
        secondTimer.start();
    }
    //draw Helicopter
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        g.drawString("score: "+score, 20, 30);
        
        /*g.drawString("score", 20, 30);
        g.drawString("+" + score, 30, 60);*/

        g.drawString("Time " + timeLeft + "s", getWidth() - 150, 30);

        // check game over
        if (isGameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 50));
            FontMetrics fm = g.getFontMetrics();
            String gameOverText = "Game over";
            int textWidth = fm.stringWidth(gameOverText);
            g.drawString(gameOverText, (getWidth() - textWidth) / 2, getHeight() / 2);
            return; 
        }

        // base
        g.drawRect(targetX, targetY, targetWidth, targetHeight);
        g.drawLine(targetX, targetY + targetHeight, targetX + 15, targetY);
        g.drawLine(targetX + 15, targetY, targetX + 30, targetY + targetHeight);
        g.drawLine(targetX + 30, targetY + targetHeight, targetX + 45, targetY);
        g.drawLine(targetX + 45, targetY, targetX + targetWidth, targetY + targetHeight);

        // Helicopter
        // 1. fan
        g.drawOval(heliX + 10, heliY - 15, 60, 15);
        g.drawLine(heliX + 40, heliY, heliX + 40, heliY - 5); 
        // 2. center
        g.drawOval(heliX, heliY, 80, 40);
        // 3. fan tail
        g.drawLine(heliX + 80, heliY + 20, heliX + 110, heliY + 20);
        g.drawOval(heliX + 110, heliY + 10, 20, 20);
        // 4. leg
        g.drawLine(heliX + 20, heliY + 40, heliX + 15, heliY + 55);
        g.drawLine(heliX + 60, heliY + 40, heliX + 65, heliY + 55);
        g.drawLine(heliX - 10, heliY + 55, heliX + 90, heliY + 55);

        // draw bomb
        for (Point bomb : bombs) {
            g.drawOval(bomb.x, bomb.y, 10, 15);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (isGameOver) return;

        // Helicopter Movements
        if (leftPressed && heliX > 20) heliX -= heliSpeed;
        if (rightPressed && (heliX + 130) < getWidth()) heliX += heliSpeed;

        // update bomb
        Iterator<Point> it = bombs.iterator();
        while (it.hasNext()) {
            Point bomb = it.next();
            bomb.y += bombSpeed;

            // Collision Detection
            Rectangle bombRect = new Rectangle(bomb.x, bomb.y, 10, 15);
            Rectangle targetRect = new Rectangle(targetX, targetY, targetWidth, targetHeight);

            if (bombRect.intersects(targetRect)) {
                score += 10; // get point
                it.remove(); // delete bomb
                
                // random x,y
                targetX = random.nextInt(getWidth() - targetWidth - 40) + 20;
            }
            // protect bomb out of frame
            else if (bomb.y > getHeight()) {
                it.remove();
            }
        }
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) leftPressed = true;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) rightPressed = true;
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // space bar-> bomb
            if (!isGameOver) {
                bombs.add(new Point(heliX + 35, heliY + 60));
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) leftPressed = false;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) rightPressed = false;
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Helicopter Bombing Game");
        HelicopterGame game = new HelicopterGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
