import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

class Player extends Entity {
    boolean a, d, s, w;
    boolean shoot, shooting;
    int speed;
    int moveSpeed;
    int xMovement, yMovement;
    Weapon weapon;
    Weapon weapon1;
    Weapon weapon2;

    Player(Point[] points) {
        super(points);
        moveSpeed = 6;
        speed = moveSpeed;
        weapon1 = new Weapon("gun");
        weapon1.projectile.isPlayer = true;
        weapon2 = new Weapon("sniper");
        weapon2.projectile.isPlayer = true;
        weapon = weapon1;
        health = 5;
        color = Color.blue;
        corpseLength = 100;
    }

    void process() {
        TimerTask timertask = new TimerTask() {
            public void run() {
                Player.super.process();
                for (Entity entity : Game.getInstance().room.entities) {
                    if (Player.this == entity) {
                        continue;
                    }
                    if (Player.this.intersects(entity)) {
                        health--;
                        entity.health--;
                        if (health > 0) {
                            hit();
                        } else {
                            death();
                        }
                        if (entity.health > 0) {
                            entity.hit();
                        } else {
                            entity.death();
                        }
                    }
                }
                move();
            }
        };
        timer.schedule(timertask, 0, Game.getInstance().delay);
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

    void hit() {
        Color orginalColor = color;
        color = new Color(139, 0, 0);
        Timer timer = new Timer();
        speed /= 2;
        moveSpeed /= 2;
        TimerTask timertask = new TimerTask() {
            public void run() {
                Player.this.color = orginalColor;
                speed *= 2;
                moveSpeed *= 2;
            }
        };
        timer.schedule(timertask, 10 * Game.getInstance().delay);
    }

    void death() {
        timer.cancel();
        timer.purge();
        Game.getInstance().room.entities.remove(this);
        Polygon corpse = this.clone();
        corpse.color = new Color(139, 0, 0);
        Timer timer = new Timer();
        TimerTask timertask = new TimerTask() {
            int count = Player.this.corpseLength;

            public void run() {
                count--;
                corpse.color = new Color(0, 0, 0, 255 * count / Player.this.corpseLength);
                corpse.setBorderColor(new Color(0, 0, 0, 255 * count / Player.this.corpseLength));
                if (count == 0) {
                    Game.getInstance().room.polygons.remove(corpse);
                    System.exit(0);
                    System.exit(0);
                    timer.cancel();
                    timer.purge();
                }
            }
        };
        Game.getInstance().room.polygons.add(corpse);
        timer.schedule(timertask, 10 * Game.getInstance().delay, Game.getInstance().delay);
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
