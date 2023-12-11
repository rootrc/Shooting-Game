import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

import Geo.Line;
import Geo.Point;
import Geo.Polygon;

class Player extends Entity {
    private boolean a, d, s, w;
    private boolean shoot, shooting;
    private double speed;
    private double moveSpeed;
    private int xMovement, yMovement;
    private Weapon weapon1;
    private Weapon weapon2;

    Player(Room room, Point[] points) {
        super(room, points);
        moveSpeed = 5;
        speed = moveSpeed;
        health = 100;
        orginalColor = Color.blue;
        setColor(orginalColor);
        corpseTime = 200;
        weapon1 = new Weapon(this, "player_gun");
        weapon1.projectile.isPlayer = true;
        weapon2 = new Weapon(this, "player_sniper");
        weapon2.projectile.isPlayer = true;
        weapon = weapon1;
    }

    public Player clone() {
        Player player = new Player(room, getPoints());
        player.a = a;
        player.d = d;
        player.s = s;
        player.w = w;
        player.shoot = shoot;
        player.shooting = shooting;
        player.shooting = shooting;
        player.speed = speed;
        player.moveSpeed = moveSpeed;
        player.xMovement = xMovement;
        player.yMovement = yMovement;
        player.weapon = weapon;
        player.weapon1 = weapon1;
        player.weapon2 = weapon2;
        return player;
    }

    public void draw(Graphics2D g2d, int x, int y) {
        super.draw(g2d, x, y);
    }

    void process() {
        TimerTask timertask = new TimerTask() {
            public void run() {
                for (Entity entity : room.entities) {
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
        timer.schedule(timertask, 0, Game.delay);
    }

    void move() {
        Point mouseLocation = Game.getInstance().panel.mouseLocation();
        double radian = new Line(getCentroid(), mouseLocation).caculateRadian();
        Player player = clone();
        if (xMovement == 0 || yMovement == 0) {
            player.move(xMovement * speed, yMovement * speed);
        } else {
            player.move(xMovement * speed / Math.sqrt(2), yMovement * speed / Math.sqrt(2));
        }
        player.rotate(direction - radian);
        if (!room.intersects(player)) {
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
        setColor(new Color(139, 0, 0));
        speed /= 2;
        moveSpeed /= 2;
        Timer timer = new Timer();
        TimerTask timertask = new TimerTask() {
            public void run() {
                Player.this.setColor(orginalColor);
                speed *= 2;
                moveSpeed *= 2;
            }
        };
        timer.schedule(timertask, 10 * Game.delay);
        Timer timer2 = new Timer();
        timertask = new TimerTask() {
            int count = 0;

            public void run() {
                count++;
                room.xAdjust = (int) (10 * Math.random()) - 5;
                room.yAdjust = (int) (10 * Math.random()) - 5;
                if (count == 10) {
                    room.xAdjust = 0;
                    room.yAdjust = 0;
                    timer2.cancel();
                }
            }
        };
        timer2.schedule(timertask, 0, Game.delay);
    }

    void death() {
        timer.cancel();
        timer.purge();
        room.entities.remove(this);
        new Corpse(this, corpseTime);
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
                weapon.shoot(getCentroid(), direction);
                attemptMove(weapon.recoil, direction);
                Casing casing = new Casing(room, getCentroid(),
                        direction + Math.PI / 2 + Math.PI / 10 * Math.random() - Math.PI / 20, 25, 50, 4, 40, 200);
                casing.setBorderColor(new Color(175, 156, 96));
                casing.setWidth(3);
                new MuzzleFlash(Player.this);
                TimerTask timertask2 = new TimerTask() {
                    public void run() {
                        room.xAdjust = 0;
                        room.yAdjust = 0;
                    }
                };
                room.xAdjust = (int) (6 * Math.random()) - 3;
                room.yAdjust = (int) (6 * Math.random()) - 3;
                timer.schedule(timertask2, Game.delay);
            }
        };
        timer.schedule(timertask, 0, weapon.cooldown);
        shooting = true;
    }

    void keyPressed(KeyEvent e) {
        if (health <= 0) {
            return;
        }
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
                    speed = (int) (moveSpeed * weapon.shootMoveSpeed);
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
        if (health <= 0) {
            return;
        }
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
