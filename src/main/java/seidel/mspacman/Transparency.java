package seidel.mspacman;

import java.awt.*;
import java.awt.image.*;

public class Transparency {

    public static BufferedImage colorTrans(BufferedImage bi, int one, int two) {
        ImageFilter filter = new Filter(one, two);
        ImageProducer ip = new FilteredImageSource(bi.getSource(), filter);
        bi = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
        bi.createGraphics().drawImage(Toolkit.getDefaultToolkit().createImage(ip), 0, 0, null);
        return bi;
    }

    public static class Filter extends RGBImageFilter {

        int one;
        int two;

        public Filter(int one, int two) {
            this.one = one;
            this.two = two;
        }

        public final int filterRGB(int x, int y, int rgb) {
            if (rgb == one) return two;
            else return rgb;
        }

    }

}