import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

import Geo.Point;
class Panel extends JPanel {
    Panel() {
        super();
        addKeyListener(new TKeyAdapter());
        addMouseListener(new TMouseAdapter());
        setBackground(Color.gray);
        setFocusable(true);
        setLayout(null);
    }
    Point mouseLocation() {
        return new Point(MouseInfo.getPointerInfo().getLocation().getX() - Game.getInstance().frame.getLocationOnScreen().getX() - 8, MouseInfo.getPointerInfo().getLocation().getY() - Game.getInstance().frame.getLocationOnScreen().getY() - 30);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
        Toolkit.getDefaultToolkit().sync();
    }

    void doDrawing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        Game.getInstance().room.paint(g2d);
    }

    class TKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            Game.getInstance().room.keyPressed(e);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            Game.getInstance().room.keyReleased(e);
        }
    }

    class TMouseAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            Game.getInstance().room.mouseClicked(e);
        }
    }
}