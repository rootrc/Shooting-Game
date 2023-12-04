import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

class Player extends Entity {
    boolean a, d, s, w;
    boolean shoot, shooting;
    int moveSpeed = 5;
    int speed = moveSpeed;
    int xMovement, yMovement;
    Weapon weapon;
    Weapon weapon1;
    Weapon weapon2;

    Player(Point[] points) {
        super(points);
        health = 10;
        weapon1 = new Weapon("gun");
        weapon2 = new Weapon("sniper");
        weapon = weapon1;
    }

    void process() {
        super.process();
        move();
    }

    void move() {
        Point mouseLocation = Game.getInstance().panel.mouseLocation();
        double radian = new Line(centroid, mouseLocation).caculateRadian();
        Polygon polygon = this.clone();
        polygon.move(xMovement * speed, yMovement * speed);
        polygon.rotate(direction - radian);
        if (!Game.getInstance().room.intersects(polygon)) {
            super.move(xMovement * speed, yMovement * speed);
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
            case KeyEvent.VK_W:
                if (!w) {
                    yMovement--;
                    w = true;
                }
                break;
            case KeyEvent.VK_S:
                if (!s) {
                    yMovement++;
                    s = true;
                }
                break;
            case KeyEvent.VK_A:
                if (!a) {
                    xMovement--;
                    a = true;
                }
                break;
            case KeyEvent.VK_D:
                if (!d) {
                    xMovement++;
                    d = true;
                }
                break;
            case KeyEvent.VK_SPACE:
                if (!shoot && !shooting) {
                    shoot = true;
                    speed = (int) (moveSpeed * weapon.shootMovementSpeed);
                    shoot();
                }
                break;
            case KeyEvent.VK_Q:
                if (weapon2 != null) {
                    if (weapon == weapon1) {
                        weapon = weapon2;
                    } else {
                        weapon = weapon1;
                    }
                }
                break;
        }
    }

    void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_W:
                if (w) {
                    yMovement ++;
                    w = false;
                }
                break;
            case KeyEvent.VK_S:
                if (s) {
                    yMovement --;
                    s = false;
                }
                break;
            case KeyEvent.VK_A:
                if (a) {
                    xMovement ++;
                    a = false;
                }
                break;
            case KeyEvent.VK_D:
                if (d) {
                    d = false;
                    xMovement --;
                }
                break;
            case KeyEvent.VK_SPACE:
                shoot = false;
                speed = moveSpeed;
                break;
        }
    }

    void mouseClicked(MouseEvent e) {
        // shoot = true;
    }
}
