import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.Timer;

class Game {
    final int delay = 20;
    private static Game instance = new Game();
    int gameState;
    JFrame frame;
    Panel panel;
    Room room;

    private Game() {
        frame = new JFrame();
        panel = new Panel();
        room = new Room("lvl1");
        frame.setTitle("Game");
        frame.setBackground(Color.black);
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.setVisible(true);
        Timer timer = new Timer(delay, new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                Game.getInstance().room.process();
                panel.repaint();
            }
        });
        timer.start();
    }
    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    public static void main(String[] args) {
    }

    // https://stackoverflow.com/questions/37758061/rotate-a-buffered-image-in-java
    static BufferedImage rotateImageByDegrees(BufferedImage img, double angle) {
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, Game.getInstance().panel);
        // g2d.setColor(Color.RED);
        // g2d.drawRect(0, 0, newWidth - 1, newHeight - 1);
        g2d.dispose();

        return rotated;
    }
}
