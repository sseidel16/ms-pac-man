import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Board {

  static int type;
  static Board instance;
  static Color[] boardColor = {new Color(252, 191, 146), new Color(77, 166, 225), new Color(175, 0, 0), Color.darkGray};
  static Color[] borderColor = {new Color(255, 84, 68), new Color(151, 205, 244), new Color(100, 0, 0), Color.lightGray};
  static {
    new Board();
  }
  static int[][] square;
  static int width;
  static int height;
  BufferedImage image;
  BufferedImage inverse;
  static Graphics2D g;
  static Graphics2D invG;
  static int pillsLeft;

  public Board() {
    instance = this;
  }

  public static void setBoard(int type) {
    instance.setInstanceBoard(type);
  }

  public void setInstanceBoard(int type) {
    try {
      Resource r = new Resource("board" + type);
      loadBoard(r, type);
    } catch (Exception e) {}
  }

  public void loadBoard(Resource r, int type) {
    Board.type = type;
    width = r.getNumber();
    height = r.getNumber();
    image = new BufferedImage(width * 20, height * 20, BufferedImage.TYPE_INT_ARGB);
    g = image.createGraphics();
    g.setColor(Color.black);
    g.fillRect(0, 0, width * 20, height * 20);
    inverse = new BufferedImage(width * 20, height * 20, BufferedImage.TYPE_INT_ARGB);
    invG = inverse.createGraphics();
    invG.setColor(Color.black);
    invG.fillRect(0, 0, width * 20, height * 20);
    square = new int[width][height];
    for (int y = 0; y < height; ++y) {
      for (int x = 0; x < width; ++x) {
        square[x][y] = 0;
      }
    }
    int ghost = 0;
    pillsLeft = 0;
    for (int y = 0; y < height; ++y) {
      for (int x = 0; x < width; ++x) {
        square[x][y] = r.getNumber();
        if (square[x][y] == 1) {
          g.setColor(boardColor[type - 1]);
          g.fillRect(x * 20, y * 20, 20, 20);
          g.setColor(borderColor[type - 1]);
          if (x == 0 || square[x - 1][y] == 0) g.drawLine(x * 20, y * 20, x * 20, (y * 20) + 20);
          if (y == 0 || square[x][y - 1] == 0) g.drawLine(x * 20, y * 20, (x * 20) + 20, y * 20);
          if (x == width - 1 || square[x + 1][y] == 0) g.drawLine((x * 20) + 20, y * 20, (x * 20) + 20, (y * 20) + 20);
          if (y == height - 1 || square[x][y + 1] == 0) g.drawLine(x * 20, (y * 20) + 20, (x * 20) + 20, (y * 20) + 20);
          invG.setColor(borderColor[type - 1]);
          invG.fillRect(x * 20, y * 20, 20, 20);
          invG.setColor(boardColor[type - 1]);
          if (x == 0 || square[x - 1][y] == 0) invG.drawLine(x * 20, y * 20, x * 20, (y * 20) + 20);
          if (y == 0 || square[x][y - 1] == 0) invG.drawLine(x * 20, y * 20, (x * 20) + 20, y * 20);
          if (x == width - 1 || square[x + 1][y] == 0) invG.drawLine((x * 20) + 20, y * 20, (x * 20) + 20, (y * 20) + 20);
          if (y == height - 1 || square[x][y + 1] == 0) invG.drawLine(x * 20, (y * 20) + 20, (x * 20) + 20, (y * 20) + 20);
        } else if (square[x][y] == 2) {
          g.setColor(Color.white);
          g.fillOval((x * 20) + 18, (y * 20) + 18, 4, 4);
          ++pillsLeft;
        } else if (square[x][y] == 3) {
          g.setColor(Color.white);
          g.fillOval((x * 20) + 10, (y * 20) + 10, 20, 20);
          ++pillsLeft;
        } else if (square[x][y] == 4) {
          Man.startX = x;
          Man.startY = y;
          square[x][y] = 4;
        } else if (square[x][y] == 5) {
          Ghost.startX[ghost] = x * 20;
          Ghost.startY[ghost] = y * 20;
          ++ghost;
        }
      }
    }
  }

  public static void removePill(int x, int y) {
    --pillsLeft;
    ++Fruit.count;
    ++Ghost.count;
    square[x][y] = 0;
    g.setColor(Color.black);
    g.fillOval((x * 20) + 8, (y * 20) + 8, 24, 24);
    if (pillsLeft == 0) Manager.endCause = Manager.DONE;
  }

}