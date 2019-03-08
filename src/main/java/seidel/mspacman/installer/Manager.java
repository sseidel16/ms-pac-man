package seidel.mspacman.installer;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Manager {

    static JLabel label;
    static JFrame window;
    static int n = 1;
    static boolean silent = false;

    public static void main(String[] arg) {
        if (arg.length > 0 && arg[0].equals("-s")) silent = true;
        else if (arg.length > 0 && arg[0].equals("-h")) {
            System.out.println();
            System.out.println("-h     Displays this help page");
            System.out.println("-s     Runs in silent mode (deletes installed files on exit)");
            System.out.println();
            System.exit(0);
        }
        window = new JFrame("Installer");
        window.setBounds(50, 50, 1000, 500);
        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        label = new JLabel("Installing");
        final Container pane = window.getContentPane();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        pane.add(label);
        label.setVisible(true);
        label.setFont(new Font("Arial", Font.BOLD, 50));
        window.setVisible(true);
        File dir = new File("Ms_Pac-man/");
        dir.mkdir();
        if (silent) dir.deleteOnExit();
        BufferedReader brA = null;
        BufferedReader brB = null;
        try {
            brA = new BufferedReader(new FileReader("Ms_Pac-man/highscore.rsc"));
        } catch (Exception e) {}
        try {
            brB = new BufferedReader(new FileReader("Ms_Pac-man/settings.rsc"));
        } catch (Exception e) {}
        int highscore = 0;
        boolean mute = false;
        if ((new File("Ms_Pac-man/highscore.rsc")).exists()) {
            try {
                highscore = Integer.parseInt(brA.readLine());
            } catch (Exception e) {}
        }
        if ((new File("Ms_Pac-man/settings.rsc")).exists() && highscore == 0) {
            try {
                highscore = Integer.parseInt(brB.readLine());
            } catch (Exception e) {}
            try {
                mute = Boolean.parseBoolean(brB.readLine());
            } catch (Exception e) {}
        }
        try {
            PrintStream ps = new PrintStream("Ms_Pac-man/settings.rsc");
            ps.println(highscore);
            ps.println(mute);
        } catch (Exception e) {}
        copy("ms-pac-man-1.0-SNAPSHOT-standard.jar", "Ms_Pac-man.jar");
        copy("info.txt", "Ms_Pac-man/info.txt");
        pane.remove(label);
        final JCheckBox box1 = new JCheckBox("Run Program", true);
        final JCheckBox box2 = new JCheckBox("View info.txt", false);
        box1.setFont(new Font("Arial", Font.BOLD, 50));
        box2.setFont(new Font("Arial", Font.BOLD, 50));
        pane.add(box1);
        pane.add(box2);
        JButton button = new JButton("Finish");
        button.setFont(new Font("Arial", Font.BOLD, 50));
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (box1.isSelected()) Runtime.getRuntime().exec("java -jar Ms_Pac-man.jar");
                    if (box2.isSelected()) {
                        BufferedReader br = new BufferedReader(new FileReader("Ms_Pac-man/info.txt"));
                        pane.removeAll();
                        JTextArea ta = new JTextArea();
                        ta.setFont(new Font("Arial", Font.BOLD, 50));
                        String content = "";
                        while (true) {
                            String line = br.readLine();
                            if (line != null) content += line;
                            else break;
                            content += "\n";
                        }
                        ta.setText(content);
                        ta.setVisible(true);
                        JScrollPane sp = new JScrollPane(ta);
                        sp.setVisible(true);
                        pane.add(sp);
                        sp.validate();
                        br.close();
                        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        pane.repaint();
                        window.validate();
                    } else System.exit(0);
                } catch (Exception x) {
                    x.printStackTrace();
                    System.exit(0);
                }
            }
        });
        pane.add(button);
        pane.repaint();
        window.validate();
    }

    public static void copy(String from, String to) {
        try {
            FileOutputStream fos = new FileOutputStream(to);
            InputStream is = ClassLoader.getSystemResourceAsStream(from);
            label.setText("Installing file " + n + " of 2");
            label.setFont(new Font("Arial", Font.BOLD, 50));
            int x;
            while ((x = is.read()) != -1) {
                fos.write(x);
            }
            fos.close();
            if (silent) (new File(to)).deleteOnExit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ++n;
    }

}