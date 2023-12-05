import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

class Player extends Entity {
    boolean a, d, s, w;
    boolean shoot, shooting;
    int moveSpeed = 8;
    int speed = moveSpeed;
    int xMovement, yMovement;
    Weapon weapon;
    Weapon weapon1;
    Weapon weapon2;

    Player(Point[] points) {
        super(points);
        weapon1 = new Weapon("gun");
        weapon2 = new Weapon("sniper");
        weapon = weapon1;
        health = 3;
        color = Color.blue;
        corpseLength = 100;
    }

    void process() {
        super.process();
        for (Entity entity : Game.getInstance().room.entities) {
            if (this == entity || entity.health == Integer.MAX_VALUE) {
                continue;
            }
            if (this.intersects(entity)) {
                health--;
                entity.health--;
                if (health <= 0) {
                    this.death();
                }
                if (entity.health <= 0) {
                    entity.death();
                }
            }
        }
        move();
    }

    void move() {
        Point mouseLocation = Game.getInstance().panel.mouseLocation();
        double radian = new Line(centroid, mouseLocation).caculateRadian();
        Polygon polygon = this.clone();
        if (xMovement == 0 || yMovement == 0) {
            polygon.move(xMovement * speed, yMovement * speed);
        } else {
            polygon.move(xMovement * speed / Math.sqrt(2), yMovement * speed / Math.sqrt(2));
        }
        polygon.rotate(direction - radian);
        if (!Game.getInstance().room.intersects(polygon)) {
            if (xMovement == 0 || yMovement == 0) {
                super.move(xMovement * speed, yMovement * speed);
            } else {
                super.move(xMovement * speed / Math.sqrt(2), yMovement * speed / Math.sqrt(2));
            }
            super.rotate(direction - radian);
            direction = radian;
        }
    }

    void death() {
        Game.getInstance().room.entities.remove(this);
        Entity corpse = this.clone();
        corpse.color = Color.black;
        corpse.health = Integer.MAX_VALUE;
        Timer timer = new Timer();
        TimerTask timertask = new TimerTask() {
            int frame = 0;

            public void run() {
                frame++;
                if (frame == corpse.corpseLength) {
                    Game.getInstance().room.entities.remove(corpse);
                    System.exit(0);
                    System.exit(0);
                    timer.cancel();
                    timer.purge();
                }
            }
        };
        Game.getInstance().room.entities.add(corpse);
        timer.schedule(timertask, 0, Game.getInstance().delay);
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
                    yMovement++;
                    w = false;
                }
                break;
            case KeyEvent.VK_S:
                if (s) {
                    yMovement--;
                    s = false;
                }
                break;
            case KeyEvent.VK_A:
                if (a) {
                    xMovement++;
                    a = false;
                }
                break;
            case KeyEvent.VK_D:
                if (d) {
                    d = false;
                    xMovement--;
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
