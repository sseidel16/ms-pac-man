package seidel.mspacman;

import java.awt.geom.Point2D;

public class Fruit {

    static int[] points = {100, 200, 500, 700, 1000, 2000, 5000};
    static int type;//cherry strawberry orange pretzel apple pear bannana
    static int x;
    static int y;
    static int blockX;
    static int blockY;
    static int dir;
    static int count;
    static int step = 2;
    static boolean random;
    static int released;
    static boolean roaming = false;

    public static void setFruit(int kind) {//initializer
        setType(kind);
        count = 0;
        released = 0;
        roaming = false;
    }

    public static void setType(int kind) {
        if (kind > 6) type = (int) Math.floor(Math.random() * (double) 7);
        else type = kind;
    }

    public static void update() {
        if (((count == 70 && released == 0) || (count == 170 && released == 1)) && !roaming) {
            ++released;
            roaming = true;
            dir = 4;
            x = Ghost.startX[0];
            y = Ghost.startY[0];
            setType(Manager.level - 1);
        }
        if (roaming) move();
    }

    static boolean pointReached = false;
    static int pointTimer = 0;

    public static void move() {
        if (dir == 1) x += step;
        else if (dir == 2) y += step;
        else if (dir == 3) x -= step;
        else if (dir == 4) y -= step;
        if (x > Board.width * 20) x -= Board.width * 20;
        else if (x < 0) x += Board.width * 20;
        setBlock();
        if (isStopped()) {
            x = blockX * 20;
            y = blockY * 20;
        }
        int routes = 0;
        if (!isRStopped()) ++routes;
        if (!isDStopped()) ++routes;
        if (!isLStopped()) ++routes;
        if (!isUStopped()) ++routes;
        if (Point2D.distance((double) x, (double) y, (double) Man.x, (double) Man.y) < 20.0) {
            Manager.sound[7].play();
            Manager.addPoints(points[type], x, y);
            roaming = false;
        }
        if (pointReached) pointTimer += step;
        if (pointTimer > 10) pointReached = false;
        if (Point2D.distance((double) blockX * 20.0,
                (double) blockY * 20.0, (double) x, (double) y) < (double) step && !pointReached) {
            x = blockX * 20;
            y = blockY * 20;
            pointReached = true;
            pointTimer = 0;
            if (routes == 1) dir = getOnlyRoute();
            else dir = getRandomRoute();
        }
        Manager.boardG.drawImage(Manager.fruit[type], x + 5, y + 45, null);
    }

    public static void setBlock() {
        blockX = (int) Math.ceil(((double) (x) / 20.0) - 0.5);
        blockY = (int) Math.ceil(((double) (y) / 20.0) - 0.5);
        if (blockX >= Board.width) blockX -= Board.width;
        else if (blockX < 0) blockX += Board.width;
    }

    public static int getRandomRoute() {
        int route;
        while (true) {
            route = 1 + (int) Math.floor(Math.random() * 4.0);
            if (!isOpp(route, dir)) {
                if (route == 1 && !isRStopped()) break;
                else if (route == 2 && !isDStopped()) break;
                else if (route == 3 && !isLStopped()) break;
                else if (route == 4 && !isUStopped()) break;
            }
        }
        return route;
    }

    public static int getOnlyRoute() {
        if (!isRStopped()) return 1;
        else if (!isDStopped()) return 2;
        else if (!isLStopped()) return 3;
        else return 4;
    }

    public static boolean isStopped() {
        if (dir == 1) return isRStopped();
        else if (dir == 2) return isDStopped();
        else if (dir == 3) return isLStopped();
        else if (dir == 4) return isUStopped();
        return false;
    }

    public static boolean isOpp(int dir1, int dir2) {
        return ((int) Math.abs((double) dir1 - (double) dir2) == 2.0);
    }

    public static int loopBlock(int block) {
        if (block >= Board.width) return block - Board.width;
        else if (block < 0) return block + Board.width;
        else return block;
    }

    public static boolean isRStopped() {
        int blockX2 = loopBlock(blockX + 2);
        return (Board.square[blockX2][blockY] == 1 ||
                Board.square[blockX2][blockY + 1] == 1) && x >= blockX * 20;
    }

    public static boolean isDStopped() {
        return (Board.square[blockX][blockY + 2] == 1 ||
                Board.square[blockX + 1][blockY + 2] == 1) && y >= blockY * 20;
    }

    public static boolean isLStopped() {
        int blockX2 = loopBlock(blockX - 1);
        return (Board.square[blockX2][blockY] == 1 ||
                Board.square[blockX2][blockY + 1] == 1) && x <= blockX * 20;
    }

    public static boolean isUStopped() {
        return (Board.square[blockX][blockY - 1] == 1 ||
                Board.square[blockX + 1][blockY - 1] == 1) && y <= blockY * 20;
    }

}