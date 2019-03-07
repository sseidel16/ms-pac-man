package seidel.mspacman;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.PrintStream;

public class Man implements KeyListener {

    static Man instance;
    static int startX;
    static int startY;
    static int blockX;
    static int blockY;
    static int x;
    static int y;
    static int step = 5;
    static int dir;
    static int pressed;

    public Man() {
        instance = this;
    }

    public static void reset() {
        x = startX * 20;
        y = startY * 20;
        dir = 4;
        pressed = 4;
    }

    public static void update() {
        if (dir == 1) x += step;
        else if (dir == 2) y += step;
        else if (dir == 3) x -= step;
        else if (dir == 4) y -= step;
        if (x > Board.width * 20) x -= Board.width * 20;
        else if (x < 0) x += Board.width * 20;
        blockX = (int) Math.ceil(((double) (x) / 20.0) - 0.5);
        blockY = (int) Math.ceil(((double) (y) / 20.0) - 0.5);
        if (blockX >= Board.width) blockX -= Board.width;
        else if (blockX < 0) blockX += Board.width;
        if (isStopped()) {
            x = blockX * 20;
            y = blockY * 20;
        }
        boolean atXJunk = Math.floor((double) (x) / 20.0) == (double) (x) / 20.0;
        boolean atYJunk = Math.floor((double) (y) / 20.0) == (double) (y) / 20.0;
        if (atYJunk && pressed == 1 && !isRStopped()) dir = 1;
        else if (atXJunk && pressed == 2 && !isDStopped()) dir = 2;
        else if (atYJunk && pressed == 3 && !isLStopped()) dir = 3;
        else if (atXJunk && pressed == 4 && !isUStopped()) dir = 4;
        if (Board.square[blockX][blockY] == 2) {
            Manager.sound[4].play();
            Manager.score += 10;
            Board.removePill(blockX, blockY);
        } else if (Board.square[blockX][blockY] == 3) {
            Manager.sound[8].play();
            Ghost.away = true;
            Ghost.pointsForEating = 200;
            Manager.score += 30;
            Board.removePill(blockX, blockY);
        }
        Manager.boardG.drawImage(Manager.man[dir - 1][Manager.frame], x + 5, y + 45, null);
    }

    public static boolean isStopped() {
        if (dir == 1) return isRStopped();
        else if (dir == 2) return isDStopped();
        else if (dir == 3) return isLStopped();
        else if (dir == 4) return isUStopped();
        return false;
    }

    public static boolean isRStopped() {
        int blockX2 = blockX + 2;
        if (blockX2 >= Board.width) blockX2 -= Board.width;
        return (Board.square[blockX2][blockY] == 1 ||
                Board.square[blockX2][blockY + 1] == 1) && x >= blockX * 20;
    }

    public static boolean isDStopped() {
        return (Board.square[blockX][blockY + 2] == 1 ||
                Board.square[blockX + 1][blockY + 2] == 1) && y >= blockY * 20;
    }

    public static boolean isLStopped() {
        int blockX2 = blockX - 1;
        if (blockX2 < 0) blockX2 += Board.width;
        return (Board.square[blockX2][blockY] == 1 ||
                Board.square[blockX2][blockY + 1] == 1) && x <= blockX * 20;
    }

    public static boolean isUStopped() {
        return (Board.square[blockX][blockY - 1] == 1 ||
                Board.square[blockX + 1][blockY - 1] == 1) && y <= blockY * 20;
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == 10) {
            if (Menu.dialog == Menu.LOAD) {
                if (Menu.menuPos == 0) {
                    Manager.nextLevel();
                    Menu.dialog = Menu.NONE;
                    Menu.input = Menu.NONE;
                    Manager.gameStarted = true;
                } else {
                    Menu.dialog = Menu.NONE;
                    Menu.input = Menu.LOAD;
                    Menu.menuPos = 0;
                    Menu.update();
                }
            } else if (Menu.dialog == Menu.SAVE) {
                if (Menu.menuPos == 0) {
                    Menu.input = Menu.SAVE;
                    Menu.dialog = Menu.NONE;
                    Menu.update();
                } else Manager.writeSettings();
            } else if (Menu.input == Menu.LOAD) {
                Manager.loadGame(Menu.inputString);
            } else if (Menu.input == Menu.SAVE) {
                Manager.saveGameAs(Menu.inputString);
            }
        } else if (code == 37) {
            pressed = 3;
        } else if (code == 38) {
            pressed = 4;
            if (Menu.dialog == Menu.LOAD || Menu.dialog == Menu.SAVE) {
                Menu.menuPos = 0;
                Menu.update();
            }
        } else if (code == 39) {
            pressed = 1;
        } else if (code == 40) {
            pressed = 2;
            if (Menu.dialog == Menu.LOAD || Menu.dialog == Menu.SAVE) {
                Menu.menuPos = 1;
                Menu.update();
            }
        } else if (code == 32) {
            if (Manager.gameStarted && !Manager.gameOver) {
                Manager.paused = !Manager.paused;
                for (int i = 0; i < 9; ++i) Manager.sound[i].stop();
            }
        } else if (code == 27) {
            for (int i = 0; i < 9; ++i) Manager.sound[i].stop();
            if (Manager.gameStarted && !Manager.gameOver) {
                if (Manager.endCause != Manager.NONE) Manager.afterTimer = 35;
                else if (Manager.act != Manager.NONE) Manager.afterTimer = Manager.player[Manager.act - 1].frames;
                else {
                    Manager.sound[9].loop();
                    Menu.dialog = Menu.SAVE;
                    Manager.gameOver = true;
                    Menu.update();
                }
            } else Manager.writeSettings();
        } else {
            char c = e.getKeyChar();
            if (((int) c > 64 && (int) c < 91) || ((int) c > 96 && (int) c < 123)) Menu.keyPressed(c);
            else if (code == 8) Menu.keyPressed((char) 0);
        }
    }

    public void keyReleased(KeyEvent e) {
    }

}