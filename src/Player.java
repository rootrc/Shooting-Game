import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

import Geo.Line;
import Geo.Point;

class Player extends Entity {
    private boolean a, d, s, w;
    private boolean shooting;
    private double speed;
    private double moveSpeed;
    private int xMovement, yMovement;
    private Weapon weapon1;
    // temp
    public Weapon weapon2;

    Player(Room room, Point[] points) {
        super(room, points);
        moveSpeed = 5;
        speed = moveSpeed;
        health = 10;
        healthBar.setTotalHealth(health);
        orginalColor = Color.cyan;
        setColor(orginalColor);
        corpseTime = 200;
        weapon1 = Weapon.createWeapon(this, "player_gun");
        weapon = weapon1;
    }

    public Player clone() {
        Player player = new Player(getRoom(), getPoints());
        player.a = a;
        player.d = d;
        player.s = s;
        player.w = w;
        player.speed = speed;
        player.moveSpeed = moveSpeed;
        player.xMovement = xMovement;
        player.yMovement = yMovement;
        player.weapon = weapon;
        player.weapon1 = weapon1;
        player.weapon2 = weapon2;
        return player;
    }

    public void process() {
        for (Entity entity : getRoom().entities) {
            if (this == entity) {
                continue;
            }
            if (intersects(entity)) {
                int damage = Math.min(health, entity.health);
                decreaseHealth(damage);
                entity.decreaseHealth(damage);
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
        shoot();
    }

    private void move() {
        Point mouseLocation = Game.getInstance().panel.mouseLocation();
        double radian = new Line(getCentroid(), mouseLocation).caculateRadian();
        Player player = clone();
        if (xMovement == 0 || yMovement == 0) {
            player.move(xMovement * speed, yMovement * speed);
        } else {
            player.move(xMovement * speed / Math.sqrt(2), yMovement * speed / Math.sqrt(2));
        }
        player.rotate(direction - radian);
        if (!getRoom().intersects(player)) {
            if (xMovement == 0 || yMovement == 0) {
                super.move(xMovement * speed, yMovement * speed);
            } else {
                super.move(xMovement * speed / Math.sqrt(2), yMovement * speed / Math.sqrt(2));
            }
            super.rotate(direction - radian);
            direction = radian;
        }
    }

    protected void hit() {
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
            int frame = 0;

            public void run() {
                frame++;
                getRoom().xAdjust = (int) (10 * Math.random()) - 5;
                getRoom().yAdjust = (int) (10 * Math.random()) - 5;
                if (frame == 10) {
                    getRoom().xAdjust = 0;
                    getRoom().yAdjust = 0;
                    timer2.cancel();
                }
            }
        };
        timer2.schedule(timertask, 0, Game.delay);
    }

    protected void death() {
        timer.cancel();
        timer.purge();
        getRoom().entities.remove(this);
        new Corpse(this, corpseTime);
        Timer timer = new Timer();
        TimerTask timertask = new TimerTask() {
            public void run() {
                getRoom().xAdjust = 0;
                getRoom().yAdjust = 0;
            }
        };
        getRoom().xAdjust = (int) (20 * Math.random()) - 10;
        getRoom().yAdjust = (int) (20 * Math.random()) - 10;
        timer.schedule(timertask, Game.delay);
        // System.exit(0);
    }

    private void shoot() {
        if (weapon.canShoot() && shooting) {
            weapon.shoot();
            Timer timer = new Timer();
            TimerTask timertask = new TimerTask() {
                public void run() {
                    getRoom().xAdjust = 0;
                    getRoom().yAdjust = 0;
                }
            };
            getRoom().xAdjust = (int) (6 * Math.random()) - 3;
            getRoom().yAdjust = (int) (6 * Math.random()) - 3;
            timer.schedule(timertask, Game.delay);
        }
    }

    public void pause() {
        speed = moveSpeed;
        a = false;
        d = false;
        s = false;
        w = false;
        shooting = false;
        speed = moveSpeed;
        xMovement = 0;
        yMovement = 0;
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
                shooting = true;
                speed = moveSpeed * weapon.shootMoveSpeed;
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
                shooting = false;
                speed = moveSpeed;
                break;
        }
    }

    void mouseClicked(MouseEvent e) {
        // shoot = true;
    }
}