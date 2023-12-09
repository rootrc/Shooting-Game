import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;

class Particle extends Point {
    double direction;

    Particle(Point point, double direction) {
        super(point.x, point.y);
        this.direction = direction;
        Game.getInstance().room.particles.add(this);
        process();
    }

    // for inheritence
    void process() {

    }

    void attemptMovement(double distance, double direction) {
        Point point = (this.translate(distance * Math.cos(direction),
                -distance * Math.sin(direction)));
        if (Game.getInstance().room.points[0].x < point.x - width / 2 && point.x + width / 2 < Game.getInstance().room.points[2].x
                && Game.getInstance().room.points[0].y < point.y - width / 2 && point.y + width / 2 < Game.getInstance().room.points[2].y) {
            move(distance * Math.cos(direction),
                    -distance * Math.sin(direction));
        } else {
            int l = 0;
            int r = (int) distance;
            while (l < r) {
                int m = (l + r + 1) / 2;
                point = (this.translate(distance * Math.cos(direction),
                        -distance * Math.sin(direction)));
                if (Game.getInstance().room.points[0].x < point.x - width / 2 && point.x + width / 2 < Game.getInstance().room.points[2].x
                && Game.getInstance().room.points[0].y < point.y - width / 2 && point.y + width / 2 < Game.getInstance().room.points[2].y) {
                    l = m;
                } else {
                    r = m - 1;
                }
            }
            move(l * Math.cos(direction),
                    -l * Math.sin(direction));
        }
    }
}

class Casing extends Particle {
    double distance1;
    double distance2;
    int time1;
    int time2;
    int time3;

    Casing(Point point, double direction, double distance1, double distance2, int time1, int time2, int time3) {
        super(point, direction);
        this.distance1 = distance1;
        this.distance2 = distance2;
        this.time1 = time1;
        this.time2 = time2;
        this.time3 = time3;
    }

    void process() {
        Timer timer = new Timer();
        TimerTask timertask = new TimerTask() {
            int count = 0;

            public void run() {
                count++;
                double N = distance1 * (-2.0 * count / (time1 * time1) + 1.0 / (time1 * time1) + 2.0 / time1);
                attemptMovement(N, direction);
                if (count == time1) {
                    timer.cancel();
                    timer.purge();
                }
            }
        };
        timer.schedule(timertask, 0, Game.getInstance().delay);
        Timer timer2 = new Timer();
        TimerTask timertask2 = new TimerTask() {
            int count = time1;

            public void run() {
                count++;
                double N = (distance2 - distance1) * (2 * time1 + 2 * time2 - 2 * count + 1) / (time2 * time2);
                attemptMovement(N, direction);
                if (count == time2) {
                    timer2.cancel();
                    timer2.purge();
                }
            }
        };
        timer2.schedule(timertask2, 0, Game.getInstance().delay);
        Timer timer3 = new Timer();
        TimerTask timertask3 = new TimerTask() {
            int count = 0;

            public void run() {
                count++;
                color = new Color(175, 156, 96, -255 * count
                        * count / (time3 * time3) + 255);
                if (count == time3) {
                    Game.getInstance().room.particles.remove(Casing.this);
                    timer3.cancel();
                    timer3.purge();
                }
            }
        };
        timer3.schedule(timertask3, 0, Game.getInstance().delay);
    }
}