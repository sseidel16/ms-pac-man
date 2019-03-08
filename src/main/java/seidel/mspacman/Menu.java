package seidel.mspacman;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Menu {

    static int NONE = 0;
    static int SAVE = 1;
    static int LOAD = 2;
    static int dialog = NONE;
    static int input = NONE;
    static int menuPos = 0;
    static BufferedImage image = new BufferedImage(400, 300, BufferedImage.TYPE_INT_ARGB);
    static Graphics2D g = image.createGraphics();
    static String[][] dialogButton = new String[2][2];
    static String inputString = "";

    static {
        g.setFont(new Font("Courier New", Font.BOLD, 20));
        dialogButton[0][0] = "Start New Game";
        dialogButton[0][1] = "Load Saved Game";
        dialogButton[1][0] = "Save This Game";
        dialogButton[1][1] = "Discard This Game";
    }

    public static void update() {
        g.drawImage(Manager.main, 0, 0, 400, 300, 0, 0, 500, 250, null);
        if (dialog != NONE) {
            int ID = 0;
            if (dialog == SAVE) ID = 1;
            if (menuPos == 0) g.setColor(Color.black);
            else g.setColor(Color.gray);
            g.fillRect(50, 50, 300, 50);
            if (menuPos == 1) g.setColor(Color.black);
            else g.setColor(Color.gray);
            g.fillRect(50, 150, 300, 50);
            g.setColor(Color.white);
            g.drawString(dialogButton[ID][0], 75, 75);
            g.drawString(dialogButton[ID][1], 75, 175);
        } else {
            g.setColor(Color.white);
            g.fillRect(50, 50, 300, 50);
            g.setColor(Color.black);
            if (input == LOAD) g.drawString("Type the game name", 75, 75);
            else g.drawString("Type a game name", 75, 75);
            g.fillRect(50, 150, 300, 50);
            g.setColor(Color.white);
            g.drawString(inputString, 75, 175);
        }
        Manager.g.drawImage(image, 0, 0, Manager.full.getWidth(), Manager.full.getHeight(), 0, 0, 400, 300, null);
    }

    static boolean hacked = false;
    static String code = "";

    public static void keyPressed(char c) {
        if (input == NONE) {
            code += c;
            if (code.equals("debuggingtest")) {
                Manager.sound[4].loop();
                hacked = true;
            }
            if (hacked) {
                if (c == 'f') {//fruit release
                    Fruit.count = 76;
                } else if (c == 'h') {//highscore
                    Manager.score = Manager.highscore;
                } else if (c == 'n') {//next level
                    Manager.endCause = Manager.DONE;
                } else if (c == 'p') {//pill
                    Manager.sound[8].play();
                    Ghost.away = true;
                    Ghost.pointsForEating = 200;
                } else if (c == 'r') {//reset
                    hacked = false;
                    code = "";
                    Manager.sound[4].stop();
                } else if (c == 's') {//screenshot
                    try {
                        Robot robot = new Robot();
                        ImageIO.write(robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())),
                                "png", new File("screenshot.png"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                if (c == 'm') {//minimize
                    if (Manager.window.getState() != Frame.ICONIFIED) {
                        Manager.window.setState(Frame.ICONIFIED);
                        Manager.paused = true;
                    }
                } else if (c == 's' && !Manager.gameStarted) {//sound
                    Sound.mute = !Sound.mute;
                    Manager.writeSettings();
                } else if (c == 'r') {//reset
                    code = "";
                } else if (c == 'x') {//screenshot
                    try {
                        Robot robot = new Robot();
                        ImageIO.write(robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())),
                                "png", new File("screenshot.png"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            if ((int) c == 0) {
                if (inputString.length() > 0) inputString = inputString.substring(0, inputString.length() - 1);
            } else inputString += c + "";
            Menu.update();
        }
    }

}