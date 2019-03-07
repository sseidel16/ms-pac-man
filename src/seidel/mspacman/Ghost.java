package seidel.mspacman;

import java.awt.geom.Point2D;
import java.util.Vector;

public class Ghost {

    static Ghost[] ghost = new Ghost[4];

    static {
        for (int i = 0; i < 4; ++i) {
            ghost[i] = new Ghost();
            ghost[i].i = i;
        }
    }

    static int[] startX = new int[4];
    static int[] startY = new int[4];
    static int time;
    static int pointsForEating;
    static boolean away;
    static int timer2;//Syncs everything (used by seidel.mspacman.Manager)
    static int count;//Determines when to release ghost (incremented by seidel.mspacman.Board.removePill())
    static int released;

    int step;
    int timer1;
    int i;
    int x;
    int y;
    int dir;
    int blockX;
    int blockY;
    int destX;
    int destY;
    boolean dead;
    boolean insAway;

    public static void setTime(int time) {
        Ghost.time = time;
        pointsForEating = 200;
        away = false;
        timer2 = 0;
        count = 0;
        released = 1;
        for (int i = 0; i < 4; ++i) {
            ghost[i].step = 4;
            ghost[i].x = startX[i];
            ghost[i].y = startY[i];
            ghost[i].dir = 4;
            ghost[i].destX = Man.x;
            ghost[i].destY = Man.y;
            ghost[i].dead = false;
            ghost[i].insAway = false;
            ghost[i].setBlock();
            ghost[i].setRoute();
        }
    }

    public static void update() {
        ++timer2;
        if (count > 20 && released < 4) {
            ++released;
            count = 0;
        }
        if (timer2 >= 10) timer2 = 0;
        for (int i = 0; i < 4; ++i) {
            ghost[i].instanceUpdate();
        }
        away = false;
    }

    boolean pointReached = false;
    int pointTimer = 0;

    public boolean isBlinking() {
        return timer1 >= (time - 1) * 60;
    }

    public void instanceUpdate() {
        if (!dead) {
            if (insAway) {
                if (isBlinking() && Manager.frame == 1)
                    Manager.boardG.drawImage(Manager.ghost[5][1], x + 5, y + 45, null);
                else Manager.boardG.drawImage(Manager.ghost[4][0], x + 5, y + 45, null);
            } else Manager.boardG.drawImage(Manager.ghost[i][Manager.frame], x + 5, y + 45, null);
        }
        Manager.boardG.drawImage(Manager.eye[dir - 1], x + 5, y + 45, null);
        if (i >= released) return;
        if (away && !dead) {
            insAway = true;
            timer1 = 0;
            step = 2;
            destX = startX[2];
            destY = startY[2];
        }
        if (insAway && !dead) {
            ++timer1;
            if (timer1 >= ((time - 1) * 60) + 60) {
                insAway = false;
                step = 4;
                destX = Man.x;
                destY = Man.y;
            }
        }
        if (dir == 1) x += step;
        else if (dir == 2) y += step;
        else if (dir == 3) x -= step;
        else if (dir == 4) y -= step;
        if (x > Board.width * 20) x -= Board.width * 20;
        else if (x < 0) x += Board.width * 20;
        setBlock();
        if (Point2D.distance((double) x, (double) y, (double) Man.x, (double) Man.y) < 20.0) {
            if (insAway && !dead) {
                Manager.addPoints(pointsForEating, x, y);
                pointsForEating *= 2;
                dead = true;
                step = 10;
                Manager.sound[6].play();
            } else if (!dead) {
                Manager.endCause = Manager.DEAD;
                Manager.sound[5].play();
            }
        }
        if (dead) {
            int endX = (int) Math.ceil(((double) (destX) / 20.0) - 0.5);
            int endY = (int) Math.ceil(((double) (destY) / 20.0) - 0.5);
            if (blockX == endX && blockY == endY) {
                insAway = false;
                step = 4;
                dead = false;
                destX = Man.x;
                destY = Man.y;
                dir = 4;
            }
        } else if (!insAway) {
            destX = Man.x;
            destY = Man.y;
        }
        if (pointReached) pointTimer += step;
        if (pointTimer > 10) pointReached = false;
        if (Point2D.distance((double) blockX * 20.0,
                (double) blockY * 20.0, (double) x, (double) y) < (double) step && !pointReached) {
            x = blockX * 20;
            y = blockY * 20;
            pointReached = true;
            pointTimer = 0;
            setRoute();
        }
    }

    public void setBlock() {
        blockX = (int) Math.ceil(((double) (x) / 20.0) - 0.5);
        blockY = (int) Math.ceil(((double) (y) / 20.0) - 0.5);
        if (blockX >= Board.width) blockX -= Board.width;
        else if (blockX < 0) blockX += Board.width;
    }

    public void setRoute() {
        int routes = 0;
        if (!isRStopped()) ++routes;
        if (!isDStopped()) ++routes;
        if (!isLStopped()) ++routes;
        if (!isUStopped()) ++routes;
        if (routes == 1) dir = getOnlyRoute();
        else if (routes == 2 || (Math.random() < 0.25 && !dead)) dir = getRandomRoute();
        else dir = getFastestRoute();
    }

    public int getRandomRoute() {
        int route;
        while (true) {
            route = 1 + (int) Math.floor(Math.random() * 4.0);
            if (!isOpp(dir, route)) {
                if (route == 1 && !isRStopped()) break;
                else if (route == 2 && !isDStopped()) break;
                else if (route == 3 && !isLStopped()) break;
                else if (route == 4 && !isUStopped()) break;
            }
        }
        return route;
    }

    static Vector<End> ends = new Vector<End>(5, 5);
    static boolean[][] checked;

    public int getFastestRoute() {
        checked = new boolean[Board.width][Board.height];
        for (int i = 0; i < Board.width; ++i) {
            for (int ii = 0; ii < Board.height; ++ii) {
                checked[i][ii] = false;
            }
        }
        ends.removeAllElements();
        int blockXp1 = loopBlock(blockX + 1);
        int blockXp2 = loopBlock(blockX + 2);
        int blockXm1 = loopBlock(blockX - 1);
        if (dir != 3 && Board.square[blockXp2][blockY] != 1 &&
                Board.square[blockXp2][blockY + 1] != 1 && checked[blockXp1][blockY] != true)
            new End(blockXp1, blockY, 1, 1);
        if (dir != 4 && Board.square[blockX][blockY + 2] != 1 &&
                Board.square[blockXp1][blockY + 2] != 1 && checked[blockX][blockY + 1] != true)
            new End(blockX, blockY + 1, 2, 2);
        if (dir != 1 && Board.square[blockXm1][blockY] != 1 &&
                Board.square[blockXm1][blockY + 1] != 1 && checked[blockXm1][blockY] != true)
            new End(blockXm1, blockY, 3, 3);
        if (dir != 2 && Board.square[blockX][blockY - 1] != 1 &&
                Board.square[blockXp1][blockY - 1] != 1 && checked[blockX][blockY - 1] != true)
            new End(blockX, blockY - 1, 4, 4);
        while (true) {
            if (ends.size() == 0) return getRandomRoute();
            ends.get(0).extend();
            if (ends.get(0).solved) return ends.get(0).startDir;
            ends.remove(0);
        }
    }

    public int getOnlyRoute() {
        if (!isRStopped()) return 1;
        else if (!isDStopped()) return 2;
        else if (!isLStopped()) return 3;
        else return 4;
    }

    public boolean isStopped() {
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

    public boolean isRStopped() {
        int blockX2 = loopBlock(blockX + 2);
        return (Board.square[blockX2][blockY] == 1 ||
                Board.square[blockX2][blockY + 1] == 1) && x >= blockX * 20;
    }

    public boolean isDStopped() {
        return (Board.square[blockX][blockY + 2] == 1 ||
                Board.square[blockX + 1][blockY + 2] == 1) && y >= blockY * 20;
    }

    public boolean isLStopped() {
        int blockX2 = loopBlock(blockX - 1);
        return (Board.square[blockX2][blockY] == 1 ||
                Board.square[blockX2][blockY + 1] == 1) && x <= blockX * 20;
    }

    public boolean isUStopped() {
        return (Board.square[blockX][blockY - 1] == 1 ||
                Board.square[blockX + 1][blockY - 1] == 1) && y <= blockY * 20;
    }

    public class End {

        boolean solved = false;
        int startDir;
        int dir;
        int x;
        int y;

        public End(int x, int y, int dir, int startDir) {
            this.x = x;
            this.y = y;
            this.dir = dir;
            this.startDir = startDir;
            ends.add(this);
            checked[x][y] = true;
        }

        public void extend() {
            int blockXp1 = loopBlock(x + 1);
            int blockXp2 = loopBlock(x + 2);
            int blockXm1 = loopBlock(x - 1);
            int endX = (int) Math.ceil(((double) (destX) / 20.0) - 0.5);
            int endY = (int) Math.ceil(((double) (destY) / 20.0) - 0.5);
            if (x == endX && y == endY) solved = true;
            if (dir != 3 && Board.square[blockXp2][y] != 1 &&
                    Board.square[blockXp2][y + 1] != 1 && !checked[blockXp1][y]) new End(blockXp1, y, 1, startDir);
            if (dir != 4 && Board.square[x][y + 2] != 1 &&
                    Board.square[x + 1][y + 2] != 1 && !checked[x][y + 1]) new End(x, y + 1, 2, startDir);
            if (dir != 1 && Board.square[blockXm1][y] != 1 &&
                    Board.square[blockXm1][y + 1] != 1 && !checked[blockXm1][y]) new End(blockXm1, y, 3, startDir);
            if (dir != 2 && Board.square[x][y - 1] != 1 &&
                    Board.square[x + 1][y - 1] != 1 && !checked[x][y - 1]) new End(x, y - 1, 4, startDir);
        }

    }

}