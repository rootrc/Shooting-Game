import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import javax.swing.SwingUtilities;

import Geo.Point;

abstract class Particle extends Point {
    private Room room;
    protected double direction;

    Particle(Room room, Point point, double direction) {
        super(point.getX(), point.getY());
        this.room = room;
        this.direction = direction;
    }

    protected void draw(Graphics2D g2d, int x, int y) {
        super.draw(g2d, x, y);
    }

    protected void attemptMove(double distance, double direction) {
        if (getRoom().inside(directionTranslate(-distance, direction))) {
            directionMove(-distance, direction);
        } else {
            int l = 0;
            int r = (int) distance;
            while (l < r) {
                int m = (l + r + 1) / 2;
                if (getRoom().inside(directionTranslate(-distance, direction))) {
                    l = m;
                } else {
                    r = m - 1;
                }
            }
            directionMove(-l, direction);
        }
    }

    protected int frame;

    protected Room getRoom() {
        return room;
    }

    abstract void process();
}

class Casing extends Particle {
    private double distance1;
    private double distance2;
    private int time1;
    private int time2;
    private int time3;

    Casing(Room room, Point point, double direction, double distance1, double distance2, int time1, int time2,
            int time3) {
        super(room, point, direction);
        this.distance1 = distance1;
        this.distance2 = distance2;
        this.time1 = time1;
        this.time2 = time2;
        this.time3 = time3;
        getRoom().particles1.add(this);
    }

    void process() {
        frame++;
        double N;
        if (frame < time1) {
            N = distance1 * (-2.0 * frame / (time1 * time1) + 1.0 / (time1 * time1) + 2.0 / time1);
            attemptMove(N, direction);
        }
        if (time1 <= frame && frame < time2) {
            N = (distance2 - distance1) * (2 * time1 + 2 * time2 - 2 * frame + 1) / (time2 * time2);
            attemptMove(N, direction);
        }
        if (frame < time3) {
            setBorderColor(new Color(175, 156, 96, -255 * frame
                    * frame / (time3 * time3) + 255));
        } else {
            getRoom().particles1.remove(this);
        }
    }
}

class DamageNum extends Particle {
    private String N;

    DamageNum(Room room, Point point, int N) {
        super(room, point, Math.PI / 2);
        this.N = Integer.toString(N);
        move(20 * Math.random() - 10, 20 * Math.random() - 10);
        getRoom().particles2.add(this);
    }

    protected void draw(Graphics2D g2d, int x, int y) {
        g2d.setFont(new Font(Font.SERIF, Font.BOLD, 16));
        g2d.setColor(Color.red);
        g2d.drawString(N, (int) (getX() - SwingUtilities.computeStringWidth(g2d.getFontMetrics(), N) / 2 + x),
                (int) (getY() + y));
    }

    private double speed = 0.8;
    private int time = 30;

    void process() {
        frame++;
        moveY((double) -speed * (2 * time - 2 * frame - 1) / time);
        if (frame > 50) {
            getRoom().particles2.remove(this);
        }
    }
}