import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

import Geo.Line;
import Geo.Point;
import Geo.Polygon;

class Player extends Polygon {
    boolean a, d, s, w;
    boolean shoot, shooting;
    double direction;
    int moveSpeed = 8;
    Weapon weapon;

    Player(Point[] points) {
        super(points);
        weapon = new Weapon("gun");
    }

    void process() {
        move();
    }

    void paint(Graphics2D g2d) {
        draw(g2d);
        fill(g2d);
        // mouseLocation = Game.getInstance().panel.mouseLocation();
    }

    void move() {
        Point mouseLocation = Game.getInstance().panel.mouseLocation();
        double radian = new Line(centroid, mouseLocation).caculateRadian();
        Polygon polygon = this.clone();
        if (w) {
            polygon.moveY(-moveSpeed);
        }
        if (s) {
            polygon.moveY(moveSpeed);
        }
        if (a) {
            polygon.moveX(-moveSpeed);
        }
        if (d) {
            polygon.moveX(moveSpeed);
        }
        polygon.rotate(direction - radian);
        if (!Game.getInstance().room.intersects(polygon)) {
            if (w) {
                moveY(-moveSpeed);
            }
            if (s) {
                moveY(moveSpeed);
            }
            if (a) {
                moveX(-moveSpeed);
            }
            if (d) {
                moveX(moveSpeed);
            }
            super.rotate(direction - radian);
            direction = radian;
        }
    }

    void shoot() {
        Timer timer = new Timer();
        TimerTask timertask = new TimerTask() {
            public void run() {
                if (!shoot) {
                    timer.cancel();
                    timer.purge();
                    shooting = false;
                    return;
                }
                weapon.shoot(centroid, direction);
            }
        };
        timer.schedule(timertask, 0, weapon.cooldown);
        shooting = true;
    }

    void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_A:
                a = true;
                break;
            case KeyEvent.VK_D:
                d = true;
                break;
            case KeyEvent.VK_W:
                w = true;
                break;
            case KeyEvent.VK_S:
                s = true;
                break;
            case KeyEvent.VK_SPACE:
                if (!shoot && !shooting) {
                    shoot = true;
                    shoot();
                }
                break;
        }
    }

    void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_A:
                a = false;
                break;
            case KeyEvent.VK_D:
                d = false;
                break;
            case KeyEvent.VK_W:
                w = false;
                break;
            case KeyEvent.VK_S:
                s = false;
                break;
            case KeyEvent.VK_SPACE:
                shoot = false;
        }
    }

    void mouseClicked(MouseEvent e) {
        // shoot = true;
    }
}
