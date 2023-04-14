import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class SnakeGame extends JPanel implements Runnable {
    static int panelWidth = 780, panelHeight = 500;
    int x = panelWidth / 2 - 10, y = panelHeight / 2 - 10;
    int snakeWidth, initSnakeLength, numOfEagles, numOfApples, numOfGhosts;
    int dir;
    final int UP = 1, DOWN = 2, LEFT = 3, RIGHT = 4;
    Color randomColorForSnakeBody = Color.green;
    Color randomColorForSnakeHead = Color.magenta;
    boolean gameOver;
    int score = 0;
    int highestScore = 0;
    int initialEnergyValue;
    int restartButtonWidth = 240, restartButtonHeight = 72;
    boolean snakeHitWall;
    boolean snakeHitSelf;
    boolean checkEasyMode, checkNormalMode, checkHardMode;
    ArrayList<Point> snake, eagles, apples, ghosts, bolts;
    static Image Eagle = Toolkit.getDefaultToolkit().getImage("Owl.png");
    static Image Apple = Toolkit.getDefaultToolkit().getImage("apple.png");
    static Image PointSymbol = Toolkit.getDefaultToolkit().getImage("StarPoints.png");
    static Image HighScoreSymbol = Toolkit.getDefaultToolkit().getImage("GoldTrophy.png");
    static Image Ghost = Toolkit.getDefaultToolkit().getImage("Ghost3.png");
    int[] dx = {0, 0, 0, -1, 1};
    int[] dy = {0, -1, 1, 0, 0};

    public void run() {
        while (true) {
            if (!moveSnake()) { // moveSnake() returns a boolean, true or false
                //TODO: change this for the snake
                gameOver = true;
                repaint();
                break;
            }
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                //add your handling of this exception
            }
            repaint();
        }
    }

    boolean moveSnake() {
        if (dir != 0) {
            Point newHead = new Point(snake.get(0).x + dx[dir] * snakeWidth,
                    snake.get(0).y + dy[dir] * snakeWidth);
            //check hitting wall
            if (newHead.x < 0 || newHead.y < 0
                    || newHead.x >= panelWidth || newHead.y >= panelHeight) {
                snakeHitWall = true;
                return false;
            }
            initialEnergyValue -= 2;
            if (initialEnergyValue == 0) {
                return false;
            }
            if (eagles.contains(newHead)) { // if snake hit eagle
                //remove this eagle
                eagles.remove(newHead);
                //add a new eagle
                score--;
                addEagle();
                //decrease snake's length by 1
                snake.remove(snake.size() - 1); // shrink
                // check if hitting eagle
            }

            if (snake.isEmpty()) { // if snake is empty
                return false;
            }
            //check hitting self
            if (snake.contains(newHead)) {
                snakeHitSelf = true;
                return false;
            }
            if (ghosts.contains(newHead)) {
                ghosts.remove(newHead);
                if (initialEnergyValue < 10) {
                    initialEnergyValue = 0;
                    return false;
                } else {
                    initialEnergyValue -= 20;
                }
                addGhost();
            }
            // check if hitting apple
            if (apples.contains(newHead)) {
                apples.remove(newHead);
                score++;
                initialEnergyValue += 50;
                int tempVariable;
                if (checkEasyMode) {
                    tempVariable = 200;
                } else if (checkNormalMode) {
                    tempVariable = 100;
                } else if (checkHardMode) {
                    tempVariable = 50;
                } else { // this would be the default energy value. If no modes are selected, an energy value of 150 will be set.
                    tempVariable = 150;
                }
                // checker to see if the energy value goes over the energy vaue set to be maximum
                if (initialEnergyValue > tempVariable) {
                    initialEnergyValue = tempVariable;
                }
                if (score > highestScore) {
                    highestScore++;
                }
                addApple();
            } else {
                snake.remove(snake.size() - 1); // this would be when we didn't hit
                // apple, so we just remove
            }
            //move snake
            snake.add(0, newHead);
        }
        return true;
    }

    void easyMode() {
        initialEnergyValue = 200;
        checkEasyMode = true;
    }

    void normalMode() {
        initialEnergyValue = 100;
        checkNormalMode = true;
    }

    void hardMode() {
        initialEnergyValue = 50;
        checkHardMode = true;
    }

    void start() {
        numOfEagles = 30;
        numOfApples = 15;
        numOfGhosts = 30;
        initialEnergyValue = 150;
//        if(lvlNum == levelEasy) {
//            easyMode();
//        }
        score = 0;
        snake = new ArrayList<>();
        Point head = new Point(panelWidth / 2 - snakeWidth / 2, panelHeight / 2 - snakeWidth / 2);
        snake.add(head);
        //by default add some "body square"
        for (int i = 0; i < initSnakeLength - 1; i++) {
            int x = head.x - snakeWidth * (i + 1);
            int y = head.y;
            snake.add(new Point(x, y));
        }
        eagles = new ArrayList<>();
        for (int i = 0; i < numOfEagles; i++) {
            addEagle(); //add one eagle
        }
        apples = new ArrayList<>();
        for (int i = 0; i < numOfApples; i++) {
            addApple(); //add one eagle
        }
        ghosts = new ArrayList<>();
        for (int i = 0; i < numOfGhosts; i++) {
            addGhost();
        }
        new Thread(this).start(); // this happens more than one time, we need to
        // end thread after each time
    }

    void addEagle() {
        int x = (int) (Math.random() * (panelWidth / snakeWidth));
        int y = (int) (Math.random() * (panelHeight / snakeWidth));
        Point newEagle = new Point(x * snakeWidth, y * snakeWidth);
        eagles.add(newEagle);
    }

    void addApple() {
        int x = (int) (Math.random() * (panelWidth / snakeWidth));
        int y = (int) (Math.random() * (panelHeight / snakeWidth));
        Point newApple = new Point(x * snakeWidth, y * snakeWidth);
        apples.add(newApple);
    }

    void addGhost() {
        int x = (int) (Math.random() * (panelWidth / snakeWidth));
        int y = (int) (Math.random() * (panelHeight / snakeWidth));
        Point newGhost = new Point(x * snakeWidth, y * snakeWidth);
        ghosts.add(newGhost);
    }


    void drawEagles(Graphics g) {
        g.setColor(Color.GRAY);
        for (Point e : eagles) {
            g.drawImage(Eagle, e.x, (int) (e.y - snakeWidth * 0.1), snakeWidth, (int) (snakeWidth * 1.2), this);
        }
    }

    void drawApples(Graphics g) {
        g.setColor(Color.red);
        for (Point e : apples) {
            g.drawImage(Apple, e.x, (e.y), snakeWidth, (snakeWidth), this);
        }
    }

    void drawGhosts(Graphics g) {
        for (Point e : ghosts) {
            g.drawImage(Ghost, e.x, (e.y), snakeWidth, (snakeWidth), this);
        }
    }

    public SnakeGame() {
        snakeWidth = 20;
        initSnakeLength = 6; //initial number of squares for the snake body to start with
        setFocusable(true);
        start();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int click_x = e.getX();
                int click_y = e.getY();
                System.out.println("Detected a mouse click at " + click_x + "," + click_y);
//                x = panelWidth / 2 - 10;
//                y = panelHeight / 2 - 10;
                if (click_x >= 520 && click_x <= 760 && click_y >= 520 && click_y <= 592) {
                    x = panelWidth / 2 - 10;
                    y = panelHeight / 2 - 10;
                    snakeHitWall = false;
                    snakeHitSelf = false;
                    checkEasyMode = false;
                    checkNormalMode = false;
                    checkHardMode = false;
                    dir = 0;
                    gameOver = false;
                    start();
                    score = 0;
                    // highestScore = 0;
                }

                if (click_x >= 320 && click_x <= 465 && click_y >= 615 && click_y <= 675) {
                    easyMode();
                    checkNormalMode = false;
                    checkHardMode = false;
                }
                if (click_x >= (320 + 145) && click_x <= (465 + 145) && click_y >= 615 && click_y <= 675) {
                    normalMode();
                    checkEasyMode = false;
                    checkHardMode = false;
                }
                if (click_x >= (320 + 145 + 145) && click_x <= (465 + 145 + 145) && click_y >= 615 && click_y <= 675) {
                    hardMode();
                    checkEasyMode = false;
                    checkNormalMode = false;
                }

                //this is for changing snake color
                if (click_x >= 320 && click_x <= 495 && click_y >= 550 && click_y <= 610) {
                    int randomR1 = (int) (Math.random() * 255);
                    int randomG1 = (int) (Math.random() * 255);
                    int randomB1 = (int) (Math.random() * 255);
                    int randomR2 = (int) (Math.random() * 255);
                    int randomG2 = (int) (Math.random() * 255);
                    int randomB2 = (int) (Math.random() * 255);
                    randomColorForSnakeBody = new Color(randomR1, randomG1, randomB1);
                    randomColorForSnakeHead = new Color(randomR2, randomG2, randomB2);
                }
                //add our handling here
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                System.out.println(e.getKeyCode());

                boolean ignore;

                ignore = e.getKeyCode() == KeyEvent.VK_LEFT && dir == 0;
                ignore |= e.getKeyCode() == KeyEvent.VK_LEFT && dir == RIGHT;
                ignore |= e.getKeyCode() == KeyEvent.VK_RIGHT && dir == LEFT;
                ignore |= e.getKeyCode() == KeyEvent.VK_DOWN && dir == UP;
                ignore |= e.getKeyCode() == KeyEvent.VK_UP && dir == DOWN;
                if (ignore) {
                    repaint();
                    return;
                }
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP: {
                        dir = UP;
                        break;
                    }
                    case KeyEvent.VK_W:
                        dir = UP;
                        break;
                    case KeyEvent.VK_DOWN: {
                        dir = DOWN;
                        break;
                    }
                    case KeyEvent.VK_LEFT:
                        dir = LEFT;
                        break;
                    case KeyEvent.VK_RIGHT:
                        dir = RIGHT;
                        break;
                }
                repaint();
            }
        });
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i <= 500; i += 20) {
            g.drawLine(0, i, panelWidth, i);
        }
        for (int i = 0; i <= 780; i += 20) {
            g.drawLine(i, 0, i, panelHeight);
        }
        for (int i = 0; i < 7; i++) {
            g.drawLine(0, panelHeight + i, panelWidth, panelHeight + i);
        }
        for (int i = 0; i < 7; i++) {
            g.drawLine(300 + i, panelHeight + 200, 300 + i, panelHeight);
        }
        g.setColor(Color.CYAN);
        g.fillRect(0, 507, 300, 193);
        g.setColor(Color.BLACK);
        g.drawImage(PointSymbol, 20, panelHeight + 20, 70, 70, this);
        g.drawImage(HighScoreSymbol, 20, panelHeight + 110, 70, 70, this);
        g.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
        g.drawString("Score: " + score, 95, panelHeight + 60);
        g.drawString("Highest score: " + highestScore, 90, panelHeight + 150);
        g.setFont(new Font(Font.DIALOG, Font.BOLD, 30));
        g.drawString("Energy: " + initialEnergyValue, 320, 540); // display energy
        g.setFont(new Font(Font.DIALOG, Font.BOLD, 40));
        g.setColor(Color.RED); // setting color for the button
        g.fillRect(520, 520, restartButtonWidth, restartButtonHeight);
        g.setColor(Color.BLACK);
        g.drawRect(520, 520, restartButtonWidth, restartButtonHeight);
        g.drawString("RESTART", 545, 570);
        g.setColor(Color.PINK);
        g.fillRect(320, 550, 175, 60);
        g.setColor(Color.BLACK);
        g.drawRect(320, 550, 175, 60); // border of the "set snake color" button
        g.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
        g.drawString("Give your snake", 325, 570);
        g.drawString("a random color", 330, 600);
        g.setColor(new Color(26, 25, 25, 173));
        g.fillRect(320, 615, 435, 60);
        g.setColor(Color.BLACK);
        for (int i = 0; i < 4; i++) {
            g.drawLine(320, 615 + i, 755, 615 + i); // horizontal line 1 (black outline)
        }
        for (int i = 0; i < 4; i++) {
            g.drawLine(320, 675 - i, 755, 675 - i); // horizontal line 2 (black outline)
        }
        for (int i = 0; i < 4; i++) {
            g.drawLine(320 + i, 615, 320 + i, 675); // vertical line 1 (black outline)
        }
        for (int i = 0; i < 4; i++) {
            g.drawLine(465 + i, 615, 465 + i, 675); // vertical line 2 (black outline)
        }
        for (int i = 0; i < 4; i++) {
            g.drawLine(610 + i, 615, 610 + i, 675); // vertical line 3 (black outline)
        }
        for (int i = 0; i < 4; i++) {
            g.drawLine(755 + i, 615, 755 + i, 675); // vertical line 3 (black outline)
        }
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.DIALOG, Font.BOLD, 40));
        g.drawString("Easy", 345, 660);
        g.setFont(new Font(Font.DIALOG, Font.BOLD, 35));
        g.drawString("Normal", 475, 660);
        g.setFont(new Font(Font.DIALOG, Font.BOLD, 40));
        g.drawString("Hard", 635, 660);
        g.setColor(Color.BLACK);
        if (gameOver) {
            g.setFont(new Font(Font.DIALOG, Font.BOLD, 15));
            if (snake.isEmpty()) {
                g.drawString("Too many owls attacked you. Click 'RESTART' to restart!", panelWidth / 2 - 50, (panelHeight + 200) - 8);
            } else if (initialEnergyValue == 0) {
                g.drawString("You ran out of energy. Click 'RESTART' to restart!", panelWidth / 2 - 30, (panelHeight + 200) - 8);
            } else if (snakeHitWall) {
                g.drawString("You hit the wall! Click 'RESTART' to restart!", panelWidth / 2 - 30, (panelHeight + 200) - 8);
            } else if (snakeHitSelf) {
                g.drawString("You hit yourself! Click 'RESTART' to restart!", panelWidth / 2 - 30, (panelHeight + 200) - 8);
            }
        }
        //draw the snake
        if (!snake.isEmpty()) {
            drawSnake(g);
        }   // do a protection to get rid of the errors
        drawEagles(g); // this is drawing eagles
        drawApples(g); // this is drawing apples
        drawGhosts(g); // this is drawing ghosts
    }

    void drawSnake(Graphics g) {
        g.setColor(randomColorForSnakeBody);
        for (Point p : snake) {
            g.fillOval(p.x, p.y, snakeWidth, snakeWidth);
        }
        g.setColor(randomColorForSnakeHead);
        g.fillOval(snake.get(0).x, snake.get(0).y, snakeWidth, snakeWidth);
    }

    public static void main(String[] args) { // main function can only access static variables
        JFrame myFrame = new JFrame("Deadly Snake Game! :)");
        SnakeGame myPanel = new SnakeGame();
        myPanel.setPreferredSize(new Dimension(panelWidth, panelHeight + 200)); //pixel
        myPanel.setBackground(Color.white);
        myFrame.add(myPanel);
        myFrame.pack();
        myFrame.setLocationRelativeTo(null); //show the window at the screen center
        myFrame.setVisible(true);
        myFrame.setResizable(false);
    }
}



