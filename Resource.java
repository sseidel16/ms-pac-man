import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;

public class Resource {

  BufferedReader br;
  String file;

  public Resource(String file) throws Exception {
    this.file = file;  
    br = new BufferedReader(new InputStreamReader(Class.forName("Manager").getResourceAsStream(file + ".rsc")));
  }

  public Resource(String file, String dir) throws Exception {
    this.file = dir + "/" + file;
    br = new BufferedReader(new FileReader(new File(this.file + ".rsc")));
  }

  public int getNumber() {
    try {
      return Integer.parseInt(br.readLine());
    } catch (Exception e) {
      System.out.println("Error while trying to read resource file: " + file + " getNumber()");
      e.printStackTrace();
      System.exit(0);
    }
    return 0;
  }

  public boolean getBoolean() {
    try {
      return Boolean.parseBoolean(br.readLine());
    } catch (Exception e) {
      System.out.println("Error while trying to read resource file: " + file + " getBoolean()");
      e.printStackTrace();
      System.exit(0);
    }
    return false;
  }

  public void delete() {
    try {
      br.close();
      br = null;
      File f = new File(file + ".rsc");
      if (!f.delete()) {
        System.out.println("Unable to delete game resource file");
        System.exit(0);
      }
    } catch (Exception e) {
      e.printStackTrace();;
      System.exit(0);
    }
  }

}