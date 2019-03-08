package seidel.mspacman;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class Loading extends TimerTask {

    static Vector<Letter> letters = new Vector<Letter>(10, 10);
    static int i = 0;
    static int ii = 0;
    static BufferedImage bi = new BufferedImage(500, 250, BufferedImage.TYPE_INT_ARGB);
    static Graphics2D g = bi.createGraphics();
    static Timer timer;
    static int[][] loc = {{89, 77},
            {130, 80},
            {162, 94},
            {84, 143},
            {121, 143},
            {164, 143},
            {214, 143},
            {265, 143},
            {318, 142},
            {372, 141}};

    public static void load() {
        new Letter(loc[0][0], loc[0][1], Manager.getImage("letter0.png"));
        timer = new Timer();
        timer.scheduleAtFixedRate(new Loading(), 0L, 50L);
    }

    public void run() {
        ++i;
        if (i > 5) {
            i = 0;
            ++ii;
            if (ii < 10) new Letter(loc[ii][0], loc[ii][1], Manager.getImage("letter" + ii + ".png"));
        }
        g.drawImage(Manager.main, 0, 0, null);
        boolean moving = false;
        for (int index = 0; index < letters.size(); ++index) {
            moving = moving | letters.get(index).update();
        }
        Manager.g.drawImage(bi, 0, 0, Manager.full.getWidth(), Manager.full.getHeight(), 0, 0, 500, 250, null);
        if (!moving) {
            timer.cancel();
            Manager.main = bi;
            Manager.start();
        }
    }

    static class Letter {

        int x;
        int y;
        int scale;
        BufferedImage image;

        public Letter(int x, int y, BufferedImage bi) {
            this.x = x;
            this.y = y;
            scale = 1;
            image = bi;
            letters.add(this);
        }

        public boolean update() {
            boolean result = true;
            if (scale < 20) ++scale;
            else result = false;
            double width = ((double) image.getWidth() / (double) scale) * 20.0;
            double height = ((double) image.getHeight() / (double) scale) * 20.0;
            double tx = x - (int) (width / 2.0);
            double ty = y - (int) (height / 2.0);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.05f * (float) scale));
            g.drawImage(image, (int) tx, (int) ty, (int) (tx + width), (int) (ty + height), 0, 0, image.getWidth(),
                    image.getHeight(), null);
            return result;
        }

    }

}