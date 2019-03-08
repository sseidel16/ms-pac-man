package seidel.mspacman;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JWindow;

public class Manager {

    static boolean paused;
    static boolean gameStarted = false;
    static boolean gameOver = false;
    static int level = 0;
    static int afterTimer = 0;
    static int NONE = 0;
    static int DONE = 1;
    static int DEAD = 2;
    static int endCause = NONE;
    static int act = NONE;
    static int[] ghostColor = {0xFFFC3B10, 0xFF4ADFCB, 0xFFFFBE56, 0xFFFDC2D4, 0xFF3F48CC, 0xFF99D9EA};
    static BufferedImage board;
    static Graphics2D boardG;
    static Graphics g;
    static JFrame window;
    static JWindow full;
    static int score = 0;
    static int highscore;
    static int lives = 5;
    static Image main = getImage("main.png");
    static Image topBack = getImage("top.png");
    static Image[] eye = new Image[4];
    static BufferedImage[] die = new BufferedImage[7];
    static Image[] fruit = new Image[7];
    static Image[] number = new Image[10];
    static Image[] points = new Image[10];
    static BufferedImage[][] man = new BufferedImage[4][2];
    static Image[][] ghost = new Image[6][2];
    static Sound[] sound = new Sound[10];
    static Player[] player = new Player[3];
    static double allScale;
    static int boardWidth;
    static int boardHeight;

    static {
        man[0][0] = getImage("man0.png");
        man[0][1] = getImage("man1.png");
        eye[0] = getImage("eye0.png");
        for (int i = 1; i < 4; ++i) {
            man[i][0] = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = man[i][0].createGraphics();
            g.rotate((double) i * (Math.PI / 2.0), 15, 15);
            g.drawImage(man[0][0], 0, 0, null);
            man[i][1] = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
            g = man[i][1].createGraphics();
            g.rotate((double) i * (Math.PI / 2.0), 15, 15);
            g.drawImage(man[0][1], 0, 0, null);
            eye[i] = getImage("eye" + i + ".png");
        }
        for (int i = 0; i < 6; ++i) {
            ghost[i][0] = Transparency.colorTrans(getImage("ghost0.png"), 0xFFFFFFFF, ghostColor[i]);
            ghost[i][1] = Transparency.colorTrans(getImage("ghost1.png"), 0xFFFFFFFF, ghostColor[i]);
        }
        double step = Math.PI / 7.0;
        for (int i = 0; i < 7; ++i) {
            die[i] = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = die[i].createGraphics();
            for (int x = 0; x < 30; ++x) {
                for (int y = 0; y < 30; ++y) {
                    double angle = Manager.getAngle(15.0, 15.0, (double) x, (double) y) + (Math.PI / 2.0);
                    if (angle >= 2.0 * Math.PI) angle -= 2.0 * Math.PI;
                    boolean right = false;
                    if (angle >= Math.PI) angle -= Math.PI;
                    else {
                        right = true;
                        angle = Math.PI - angle;
                    }
                    if (angle < Math.PI - ((double) i * step)) {
                        double distance = Point2D.distance(15.0, 15.0, (double) x, (double) y);
                        double scale = Math.PI / (Math.PI - ((double) i * step));
                        angle *= scale;
                        if (!right) angle += Math.PI;
                        else angle = Math.PI - angle;
                        angle -= Math.PI / 2.0;
                        if (angle < 0.0) angle += 2.0 * Math.PI;
                        int getX = 15 + (int) (Math.cos(angle) * distance);
                        int getY = 15 + (int) (Math.sin(angle) * distance);
                        if (getX < 30 && getY < 30 && getX >= 0 && getY >= 0) {
                            int[] pixel = getPixel(man[3][0], getX, getY);
                            g.setColor(new Color(pixel[0], pixel[1], pixel[2]));
                            g.drawLine(x, y, x, y);
                        }
                    }
                }
            }
        }
        for (int i = 0; i < 7; ++i) {
            fruit[i] = getImage("fruit" + i + ".png");//relies on 7 frames
        }
        for (int i = 0; i < 10; ++i) {
            points[i] = getImage("points" + i + ".png");
        }
        for (int i = 0; i < 10; ++i) {
            number[i] = getImage(i + ".png");
        }
        for (int i = 0; i < 10; ++i) {
            sound[i] = new Sound("sound" + i + ".wav");
        }
        try {
            Resource r = new Resource("settings", "Ms_Pac-man");
            highscore = r.getNumber();
            Sound.mute = r.getBoolean();
        } catch (Exception e) {
            File dir = new File("Ms_Pac-man/");
            dir.mkdir();
            highscore = 0;
            Sound.mute = true;
        }
    }

    public static void main(String[] arg) {
        if (arg.length > 0 && arg[0].equals("-version")) {
            System.out.println("Ms Pac-man");
            System.out.println("Created By: Stephen Seidel");
            System.out.println("Version: 1.4");
            System.exit(0);
        }
        window = new JFrame("Pac seidel.mspacman.Man");
        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        window.addKeyListener(new Man());
        full = new JWindow(window);
        window.setVisible(true);
        board = new BufferedImage(600, 720, BufferedImage.TYPE_INT_ARGB);
        boardG = board.createGraphics();
        boardG.setColor(Color.black);
        boardG.fillRect(0, 680, 600, 40);
        boardG.drawImage(man[0][0], 5, 685, null);
        boardG.drawImage(man[0][0], 45, 685, null);
        boardG.drawImage(man[0][0], 85, 685, null);
        boardG.drawImage(man[0][0], 125, 685, null);
        boardG.drawImage(man[0][0], 165, 685, null);
        sound[9].loop();
        GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(full);
        g = full.getGraphics();
        Loading.load();
        player[0] = new Player("Act1.vid");
        player[1] = new Player("Act2.vid");
        player[2] = new Player("Act3.vid");
    }

    public static void start() {
        Menu.dialog = Menu.LOAD;
        Menu.update();
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new PaintingTask(), 0L, 50L);
    }

    public static int[] getPixel(BufferedImage bi, int x, int y) {
        return bi.getData().getPixel(x, y, new int[4]);
    }

    public static double getAngle(double xcor1, double ycor1, double xcor2, double ycor2) {
        if (xcor1 == xcor2 & ycor1 == ycor2) return 0.0;
        else {
            double xspace = xcor2 - xcor1;
            double yspace = ycor2 - ycor1;
            xspace = xspace / Point2D.distance(xcor1, ycor1, xcor2, ycor2);
            yspace = yspace / Point2D.distance(xcor1, ycor1, xcor2, ycor2);
            if (Math.asin(yspace) < 0) return (2 * Math.PI) - Math.acos(xspace);
            else return Math.acos(xspace);
        }
    }

    public static BufferedImage getImage(String file) {
        // open image specified by file name under images directory in resources
        try {
            String path = "/images/" + file;
            InputStream imageStream = Manager.class.getResourceAsStream(path);
            return Transparency.colorTrans(ImageIO.read(imageStream), 0xFF000000, 0x00000000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // failed to load image; just return null
        return null;
    }

    public static void saveGameAs(String name) {
        try {
            PrintStream ps = new PrintStream("Ms_Pac-man/" + name + "game.rsc");
            ps.println(Board.type);
            ps.println(Board.width);
            ps.println(Board.height);
            for (int y = 0; y < Board.height; ++y) {
                for (int x = 0; x < Board.width; ++x) {
                    ps.println(Board.square[x][y]);
                }
            }
            ps.println(Manager.level);
            ps.println(Manager.score);
            ps.println(Manager.lives);
            ps.println(Man.x);
            ps.println(Man.y);
            ps.println(Man.dir);
            ps.println(Man.pressed);
            ps.println(Ghost.time);
            ps.println(Ghost.pointsForEating);
            ps.println(Ghost.away);
            ps.println(Ghost.timer2);
            ps.println(Ghost.count);
            ps.println(Ghost.released);
            for (int i = 0; i < 4; ++i) {
                ps.println(Ghost.ghost[i].step);
                ps.println(Ghost.ghost[i].x);
                ps.println(Ghost.ghost[i].y);
                ps.println(Ghost.ghost[i].dir);
                ps.println(Ghost.ghost[i].destY);
                ps.println(Ghost.ghost[i].destX);
                ps.println(Ghost.ghost[i].dead);
                ps.println(Ghost.ghost[i].insAway);
            }
        } catch (Exception e) {
        }
        writeSettings();
    }

    public static void loadGame(String file) {
        try {
            Resource r = new Resource(file + "game", "Ms_Pac-man");
            Board.instance.loadBoard(r, r.getNumber());
            level = r.getNumber();
            score = r.getNumber();
            lives = r.getNumber();
            Man.x = r.getNumber();
            Man.y = r.getNumber();
            Man.dir = r.getNumber();
            Man.pressed = r.getNumber();
            Ghost.time = r.getNumber();
            Ghost.pointsForEating = r.getNumber();
            Ghost.away = r.getBoolean();
            Ghost.timer2 = r.getNumber();
            Ghost.count = r.getNumber();
            Ghost.released = r.getNumber();
            for (int i = 0; i < 4; ++i) {
                Ghost.ghost[i].step = r.getNumber();
                Ghost.ghost[i].x = r.getNumber();
                Ghost.ghost[i].y = r.getNumber();
                Ghost.ghost[i].dir = r.getNumber();
                Ghost.ghost[i].destX = r.getNumber();
                Ghost.ghost[i].destY = r.getNumber();
                Ghost.ghost[i].dead = r.getBoolean();
                Ghost.ghost[i].insAway = r.getBoolean();
            }
            r.delete();
            Manager.paused = true;
            allScale = (double) full.getHeight() / 720.0;
            boardWidth = (int) ((double) board.getWidth() * allScale);
            boardHeight = (int) ((double) board.getHeight() * allScale);
            int iMax = level;
            if (iMax > 7) iMax = 7;
            for (int i = 0; i < iMax; ++i) {
                boardG.drawImage(fruit[i], 600 - ((i + 1) * 40), 685, null);
            }
            boardG.fillRect(lives * 40, 680, (5 - lives) * 40, 720);
            Menu.inputString = "";
            Menu.input = Menu.NONE;
            gameStarted = true;
            Manager.sound[9].stop();
        } catch (Exception e) {
            Menu.inputString = "";
            Menu.dialog = Menu.LOAD;
            Menu.input = Menu.NONE;
            Menu.update();
        }
    }

    public static void writeSettings() {
        try {
            PrintStream ps = new PrintStream("Ms_Pac-man/settings.rsc");
            ps.println(Manager.highscore);
            ps.println(Sound.mute);
        } catch (Exception x) {
        }
        System.exit(0);
    }

    public static void nextLevel() {
        paused = true;
        Fruit.setFruit(level);
        ++level;
        if (level == 1) {
            sound[9].stop();
            sound[0].play();
        }
        if (level < 3) Board.setBoard(1);
        else if (level < 6) Board.setBoard(2);
        else if (level < 9) Board.setBoard(3);
        else Board.setBoard(4);
        Man.reset();
        if (level == 1 || level == 3 || level == 6) Ghost.setTime(3);
        else if (level == 2 || level == 4 || level == 7) Ghost.setTime(2);
        else Ghost.setTime(1);
        allScale = (double) full.getHeight() / 720.0;
        boardWidth = (int) (600.0 * allScale);
        boardHeight = (int) (720.0 * allScale);
        if (level <= 7) boardG.drawImage(fruit[level - 1], 600 - (level * 40), 685, null);
        g.fillRect(0, 0, full.getWidth(), full.getHeight());
    }

    static int pointsTimer;
    static int currentPoints;
    static int currentPointsX;
    static int currentPointsY;

    public static void addPoints(int points, int x, int y) {
        pointsTimer = 0;
        currentPoints = points;
        currentPointsX = x;
        currentPointsY = y;
        score += points;
    }

    public static void showPoints() {
        if (pointsTimer < 25) ++pointsTimer;
        else return;
        if (currentPoints == 100) boardG.drawImage(points[0], currentPointsX + 5, currentPointsY + 45, null);
        else if (currentPoints == 200) boardG.drawImage(points[1], currentPointsX + 5, currentPointsY + 45, null);
        else if (currentPoints == 400) boardG.drawImage(points[2], currentPointsX + 5, currentPointsY + 45, null);
        else if (currentPoints == 500) boardG.drawImage(points[3], currentPointsX + 5, currentPointsY + 45, null);
        else if (currentPoints == 700) boardG.drawImage(points[4], currentPointsX + 5, currentPointsY + 45, null);
        else if (currentPoints == 800) boardG.drawImage(points[5], currentPointsX + 5, currentPointsY + 45, null);
        else if (currentPoints == 1000) boardG.drawImage(points[6], currentPointsX + 5, currentPointsY + 45, null);
        else if (currentPoints == 1600) boardG.drawImage(points[7], currentPointsX + 5, currentPointsY + 45, null);
        else if (currentPoints == 2000) boardG.drawImage(points[8], currentPointsX + 5, currentPointsY + 45, null);
        else if (currentPoints == 5000) boardG.drawImage(points[9], currentPointsX + 5, currentPointsY + 45, null);
    }

    public static void decreaseLife() {
        Manager.paused = true;
        --lives;
        boardG.fillRect(lives * 40, 680, 40, 40);
        if (lives == 0) {
            gameOver = true;
            return;
        }
        Man.reset();
        if (level == 1 || level == 3 || level == 6) Ghost.setTime(3);
        else if (level == 2 || level == 4 || level == 7) Ghost.setTime(2);
        else Ghost.setTime(1);
        Fruit.roaming = false;
    }

    public static void updateTop() {
        String scoreString = score + "";
        boardG.drawImage(topBack, 0, 0, null);
        for (int i = 0; i < scoreString.length(); ++i) {
            try {
                boardG.drawImage(number[Integer.parseInt(scoreString.charAt(i) + "")], 150 + (i * 12), 4, null);
            } catch (Exception e) {
            }
        }
        if (score > highscore) highscore = score;
        scoreString = highscore + "";
        for (int i = 0; i < scoreString.length(); ++i) {
            try {
                boardG.drawImage(number[Integer.parseInt(scoreString.charAt(i) + "")], 150 + (i * 12), 22, null);
            } catch (Exception e) {
            }
        }
    }

    public static void finishAct() {
        afterTimer = 0;
        act = NONE;
        nextLevel();
    }

    static int frame;
    static BufferedImage current;

    public static class PaintingTask extends TimerTask {

        public void run() {
            if (act != NONE) {
                if (!paused) {
                    current = player[act - 1].getNextFrame();
                    ++afterTimer;
                }
                if (afterTimer >= player[act - 1].frames) finishAct();
                else g.drawImage(current, 0, 0, full.getWidth(), full.getHeight(), 0, 0, 600, 640, null);
            } else if (Menu.dialog == Menu.NONE && Menu.input == Menu.NONE) {
                if (endCause != NONE && !paused) {
                    ++afterTimer;
                    if (afterTimer > 35) {
                        if (endCause == DONE) {
                            if (level == 2) {
                                Manager.sound[1].play();
                                act = 1;
                            } else if (level == 5) {
                                Manager.sound[2].play();
                                act = 2;
                            } else if (level == 8) {
                                Manager.sound[3].play();
                                act = 3;
                            } else nextLevel();
                        } else decreaseLife();
                        endCause = NONE;
                        afterTimer = 0;
                    }
                }
                frame = 1;
                if (Ghost.timer2 < 5) frame = 0;
                updateTop();
                if (endCause == DONE && ((afterTimer > 10 && afterTimer < 15) ||
                        (afterTimer > 20 && afterTimer < 25) ||
                        (afterTimer > 30 && afterTimer < 35))) boardG.drawImage(Board.instance.inverse, 0, 40, null);
                else boardG.drawImage(Board.instance.image, 0, 40, null);
                if (endCause != DEAD) {
                    if (paused || endCause == DONE) {
                        boardG.drawImage(man[Man.dir - 1][frame], Man.x + 5, Man.y + 45, null);
                        for (int i = 0; i < 4; ++i) {
                            if (!Ghost.ghost[i].dead) {
                                if (Ghost.ghost[i].insAway) {
                                    if (Ghost.ghost[i].isBlinking() && frame == 1)
                                        boardG.drawImage(ghost[5][1], Ghost.ghost[i].x + 5, Ghost.ghost[i].y + 45, null);
                                    else
                                        boardG.drawImage(ghost[4][0], Ghost.ghost[i].x + 5, Ghost.ghost[i].y + 45, null);
                                } else
                                    boardG.drawImage(ghost[i][frame], Ghost.ghost[i].x + 5, Ghost.ghost[i].y + 45, null);
                            }
                            boardG.drawImage(eye[Ghost.ghost[i].dir - 1], Ghost.ghost[i].x + 5, Ghost.ghost[i].y + 45, null);
                        }
                        if (Fruit.roaming) boardG.drawImage(fruit[Fruit.type], Fruit.x + 5, Fruit.y + 45, null);
                    } else if (!gameOver && endCause == NONE && act == NONE) {
                        Man.update();
                        Ghost.update();
                        Fruit.update();
                    }
                } else boardG.drawImage(die[(int) ((double) afterTimer / 5.1)], Man.x + 5, Man.y + 45, null);
                showPoints();
                g.drawImage(board, 0, 0, boardWidth, boardHeight, 0, 0, 600, 720, null);
            }
        }

    }

}