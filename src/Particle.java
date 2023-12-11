import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Timer;
import java.util.TimerTask;

import Geo.Point;

abstract class Particle extends Point {
    private Room room;
    protected Timer timer = new Timer();
    protected double direction;

    Particle(Room room, Point point, double direction) {
        super(point.getX(), point.getY());
        this.room = room;
        this.direction = direction;
        getRoom().particles.add(this);
        process();
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
    }

    void process() {
        TimerTask timertask = new TimerTask() {
            int count = 0;

            public void run() {
                count++;
                double N = distance1 * (-2.0 * count / (time1 * time1) + 1.0 / (time1 * time1) + 2.0 / time1);
                attemptMove(N, direction);
                if (count == time1) {
                    timer.cancel();
                    timer.purge();
                }
            }
        };
        timer.schedule(timertask, 0, Game.delay);
        Timer timer2 = new Timer();
        TimerTask timertask2 = new TimerTask() {
            int count = time1;

            public void run() {
                count++;
                double N = (distance2 - distance1) * (2 * time1 + 2 * time2 - 2 * count + 1) / (time2 * time2);
                attemptMove(N, direction);
                if (count == time2) {
                    timer2.cancel();
                    timer2.purge();
                }
            }
        };
        timer2.schedule(timertask2, 0, Game.delay);
        Timer timer3 = new Timer();
        TimerTask timertask3 = new TimerTask() {
            int count = 0;

            public void run() {
                count++;
                Casing.this.setBorderColor(new Color(175, 156, 96, -255 * count
                        * count / (time3 * time3) + 255));
                if (count == time3) {
                    getRoom().particles.remove(Casing.this);
                    timer3.cancel();
                    timer3.purge();
                }
            }
        };
        timer3.schedule(timertask3, 0, Game.delay);
    }
}