import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;

public class Player {

  BufferedInputStream bis;
  int frames;
  BufferedImage last = new BufferedImage(600, 640, BufferedImage.TYPE_INT_RGB);
  static Graphics2D g;

  public Player(String name) {
    try {
      bis = new BufferedInputStream(Class.forName("Manager").getResourceAsStream(name));
      frames = bis.read() + (256 * bis.read());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public BufferedImage getNextFrame() {
    int x = 0;
    int y = 0;
    int i = -1;
    BufferedImage curr = new BufferedImage(600, 640, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = curr.createGraphics();
    g.drawImage(last, 0, 0, null);
    while (true) {
      try {
        int untilNext = bis.read() + (bis.read() * 256);
        if (untilNext == 0) {
          last = copy(curr);
          return curr;
        }
        i += untilNext;
        y = i / 600;
        x = i - (y * 600);
        g.setColor(new Color(bis.read(), bis.read(), bis.read()));
        g.drawLine(x, y, x, y);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static BufferedImage copy(BufferedImage a) {
    BufferedImage bi = new BufferedImage(600, 640, BufferedImage.TYPE_INT_RGB);
    bi.createGraphics().drawImage(a, 0, 0, null);
    return bi;
  }

}