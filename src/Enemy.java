import java.awt.Color;
import java.awt.Graphics2D;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import Geo.Line;
import Geo.Point;
import Geo.Polygon;

abstract class Enemy<T extends Enemy<T>> extends Entity {
    int id;
    Line muzzleFlash;
    boolean muzzleFlashing;
    double speed;
    double moveSpeed;
    double rotationSpeed;

    Weapon weapon;

    Enemy(Room room, Point[] points) {
        super(room, points);
    }

    Enemy(Room room, int id) {
        super(room);
        this.id = id;
        muzzleFlash = new Line();
        muzzleFlash.setBorderColor(new Color(247, 241, 181));
        muzzleFlash.setWidth(8);
    }

    void scan(Scanner data) throws IOException {
        int length = Integer.parseInt(data.next());
        Point[] points = new Point[length];
        for (int j = 0; j < length; j++) {
            points[j] = new Point(Double.parseDouble(data.next()), Double.parseDouble(data.next()));
        }
        set(points);
        value = Integer.parseInt(data.next()) * 10;
        health = Integer.parseInt(data.next());
        moveSpeed = Double.parseDouble(data.next());
    }

    public void draw(Graphics2D g2d, int x, int y) {
        if (muzzleFlashing) {
            muzzleFlash.draw(g2d, x, y);
        }
        super.draw(g2d, x, y);
    }

    void shoot() {
        weapon.shoot(getCentroid(), direction);
        attemptMove(weapon.recoil, direction);
        Casing casing = new Casing(room, getCentroid(),
                direction + Math.PI / 2 + Math.PI / 10 * Math.random() - Math.PI / 20, 20,
                40, 4, 30, 100);
        casing.color = new Color(175, 156, 96);
        casing.width = 3;
        muzzleFlashing = true;
        double muzzleDirection = direction;
        Timer timer = new Timer();
        TimerTask timertask = new TimerTask() {
            int count = 0;

            public void run() {
                count++;
                muzzleFlash.setP1(getCentroid().clone());
                muzzleFlash.setP2(getCentroid().directionTranslate(20 + weapon.projectile.getLength() / 2, muzzleDirection));
                if (count == 3) {
                    muzzleFlashing = false;
                    timer.cancel();
                    timer.purge();
                }
            }
        };
        timer.schedule(timertask, 0, Game.delay);
        if (weapon.shotCount == 1) {
            return;
        }
        Timer timer2 = new Timer();
        TimerTask timertask2 = new TimerTask() {
            int count = 0;

            public void run() {
                count++;
                attemptMove(weapon.recoil, direction);
                muzzleFlashing = true;
                double muzzleDirection = direction;
                Timer timer = new Timer();
                TimerTask timertask = new TimerTask() {
                    int count = 0;

                    public void run() {
                        count++;
                        muzzleFlash.setP1(getCentroid().clone());
                        muzzleFlash
                                .setP2(getCentroid().directionTranslate(20 + weapon.projectile.getLength() / 2, muzzleDirection));
                        if (count == 3) {
                            muzzleFlashing = false;
                            timer.cancel();
                            timer.purge();
                        }
                    }
                };
                timer.schedule(timertask, 0, Game.delay);
                weapon.shoot(getCentroid(), direction);
                if (count == weapon.shotCount - 1) {
                    timer2.cancel();
                    timer2.purge();
                }
            }
        };
        timer2.schedule(timertask2, weapon.shotCooldown * Game.delay,
                weapon.shotCooldown * Game.delay);
    }

    void hit() {
        color = new Color(139, 0, 0);
        Timer timer = new Timer();
        speed /= 2;
        moveSpeed /= 2;
        rotationSpeed /= 2;
        TimerTask timertask = new TimerTask() {
            public void run() {
                Enemy.this.setColor(orginalColor);
                speed *= 2;
                moveSpeed *= 2;
                rotationSpeed *= 2;
            }
        };
        timer.schedule(timertask, 10 * Game.delay);
    }

    void death() {
        room.entities.remove(this);
        timer.cancel();
        timer.purge();
        room.score += value;
        Polygon corpse = clone();
        corpse.color = new Color(139, 0, 0);
        Timer timer = new Timer();
        TimerTask timertask = new TimerTask() {
            int count = Enemy.this.corpseLength;

            public void run() {
                count--;
                corpse.color = new Color(0, 0, 0, -255 * (Enemy.this.corpseLength - count)
                        * (Enemy.this.corpseLength - count) / Enemy.this.corpseLength / Enemy.this.corpseLength + 255);
                corpse.setBorderColor(new Color(0, 0, 0, -255 * (Enemy.this.corpseLength - count)
                        * (Enemy.this.corpseLength - count) / Enemy.this.corpseLength / Enemy.this.corpseLength + 255));
                if (count == 0) {
                    room.polygons.remove(corpse);
                    timer.cancel();
                    timer.purge();
                }
            }
        };
        room.polygons.add(corpse);
        timer.schedule(timertask, 30 * Game.delay, Game.delay);
    }

    public T translate(int x, int y) {
        return translate((double) x, (double) y);
    }

    public T translate(double x, double y) {
        T t = clone();
        t.move(x, y);
        return t;
    }

    public abstract T clone();

    abstract void process();

}

class Chaser extends Enemy<Chaser> {
    Chaser(Room room, int id) {
        super(room, id);
        orginalColor = Color.yellow;
        color = orginalColor;
        speed = moveSpeed;
        corpseLength = 600;
        try {
            Scanner data = new Scanner(new FileReader("data/enemies/chaser" + id + ".txt"));
            scan(data);
        } catch (IOException e) {
            System.out.println("Enemy Loading Error");
            System.exit(-1);
        }
    }

    Chaser(Room room, Point[] points, int id) {
        this(room, id);
        set(points);
    }

    public Chaser clone() {
        Chaser chaser = new Chaser(room, getPoints(), id);
        return chaser;
    }

    void process() {
        TimerTask timertask = new TimerTask() {
            public void run() {
                Point playerCentroid = room.player.getCentroid();
                double radian = new Line(getCentroid(), playerCentroid).caculateRadian();
                rotate(direction - radian);
                direction = radian;
                directionMove(moveSpeed, direction);
            }
        };
        timer.schedule(timertask, 0, Game.delay);
    }
}

class Rifle extends Enemy<Rifle> {
    int shootDistance;
    int moveDistance;

    Rifle(Room room, int id) {
        super(room, id);
        orginalColor = Color.blue;
        color = orginalColor;
        corpseLength = 600;
        try {
            Scanner data = new Scanner(new FileReader("data/enemies/rifle" + id + ".txt"));
            scan(data);
            weapon = new Weapon(this, "rifle" + id);
            shootDistance = (int) (Integer.parseInt(data.next()) * (0.2 * Math.random() + 1));
            moveDistance = (int) (Integer.parseInt(data.next()) * (0.2 * Math.random() + 0.8));
        } catch (IOException e) {
            System.out.println("Enemy Loading Error");
            System.exit(-1);
        }
    }

    Rifle(Room room, Point[] points, int id) {
        this(room, id);
        set(points);
    }

    public Rifle clone() {
        Rifle rifle = new Rifle(room, getPoints(), id);
        rifle.shootDistance = shootDistance;
        rifle.moveDistance = moveDistance;
        return rifle;
    }

    void process() {
        TimerTask timertask = new TimerTask() {
            public void run() {
                Point playerCentroid = room.player.getCentroid();
                Line line = new Line(getCentroid(), playerCentroid);
                double radian = line.caculateRadian();
                rotate(direction - radian);
                direction = radian;
                if (line.getLength() >= moveDistance) {
                    directionMove(speed, direction);
                }
            }
        };
        timer.schedule(timertask, 0, Game.delay);
        timertask = new TimerTask() {
            public void run() {
                Point playerCentroid = room.player.getCentroid();
                if (new Line(getCentroid(), playerCentroid).getLength() <= shootDistance) {
                    speed = moveSpeed * weapon.shootMovementSpeed;
                    shoot();
                } else {
                    speed = moveSpeed;
                }
            }
        };
        timer.schedule(timertask, 0, weapon.cooldown);
    }
}

class Sniper extends Enemy<Sniper> {
    int shootDistance;
    int moveDistance;
    int runDistance;

    Sniper(Room room, int id) {
        super(room, id);
        orginalColor = Color.white;
        color = orginalColor;
        corpseLength = 600;
        try {
            Scanner data = new Scanner(new FileReader("data/enemies/sniper" + id + ".txt"));
            scan(data);
            weapon = new Weapon(this, "sniper" + id);
            shootDistance = (int) (Integer.parseInt(data.next()) * (0.2 * Math.random() + 1));
            moveDistance = (int) (Integer.parseInt(data.next()) * (0.2 * Math.random() + 0.8));
            runDistance = (int) (Integer.parseInt(data.next()) * (0.2 * Math.random() + 0.9));
        } catch (IOException e) {
            System.out.println("Enemy Loading Error");
            System.exit(-1);
        }
    }

    Sniper(Room room, Point[] points, int id) {
        this(room, id);
        set(points);
    }

    public Sniper clone() {
        Sniper sniper = new Sniper(room, getPoints(), id);
        sniper.shootDistance = shootDistance;
        sniper.moveDistance = moveDistance;
        sniper.runDistance = runDistance;
        return sniper;
    }

    void process() {
        TimerTask timertask = new TimerTask() {
            public void run() {
                Point playerCentroid = room.player.getCentroid();
                Line line = new Line(getCentroid(), playerCentroid);
                double radian = line.caculateRadian();
                rotate(direction - radian);
                direction = radian;
                if (line.getLength() >= moveDistance) {
                    directionMove(speed, direction);
                } else if (Sniper.this.directionTranslate(-speed, direction)
                        .intersects(room.boundingBox(20))) {
                    directionMove(moveSpeed, direction);
                } else if (runDistance >= line.getLength()) {
                    if (!Sniper.this.directionTranslate(-speed, direction)
                            .intersects(room.boundingBox(30))) {
                        directionMove(-speed, direction);
                    }
                }

            }
        };
        timer.schedule(timertask, 0, Game.delay);
        timertask = new TimerTask() {
            public void run() {
                Point playerCentroid = room.player.getCentroid();
                double length = new Line(getCentroid(), playerCentroid).getLength();
                if (length > shootDistance) {
                    speed = moveSpeed;
                    return;
                }
                if (runDistance <= length && length <= shootDistance
                        || Sniper.this.directionTranslate(-speed, direction)
                                .intersects(room.boundingBox(30))) {
                    speed = moveSpeed * weapon.shootMovementSpeed;
                    shoot();
                } else {
                    speed = moveSpeed;
                }
            }
        };
        timer.schedule(timertask, 0, weapon.cooldown);
    }
}

class Machine extends Enemy<Machine> {
    int shootDistance;
    int moveDistance;

    Machine(Room room, int id) {
        super(room, id);
        orginalColor = Color.green;
        color = orginalColor;
        corpseLength = 700;
        try {
            Scanner data = new Scanner(new FileReader("data/enemies/machine" + id + ".txt"));
            scan(data);
            weapon = new Weapon(this, "machine" + id);
            shootDistance = (int) (Integer.parseInt(data.next()) * (0.2 * Math.random() + 1));
            moveDistance = (int) (Integer.parseInt(data.next()) * (0.2 * Math.random() + 0.8));
        } catch (IOException e) {
            System.out.println("Enemy Loading Error");
            System.exit(-1);
        }
    }

    Machine(Room room, Point[] points, int id) {
        this(room, id);
        set(points);
    }

    public Machine clone() {
        Machine machine = new Machine(room, getPoints(), id);
        machine.shootDistance = shootDistance;
        machine.moveDistance = moveDistance;
        return machine;
    }

    void process() {
        TimerTask timertask = new TimerTask() {
            public void run() {
                Point playerCentroid = room.player.getCentroid();
                Line line = new Line(getCentroid(), playerCentroid);
                double radian = line.caculateRadian();
                rotate(direction - radian);
                direction = radian;
                if (line.getLength() >= moveDistance) {
                    directionMove(speed, direction);
                }
            }
        };
        timer.schedule(timertask, 0, Game.delay);
        timertask = new TimerTask() {
            public void run() {
                Point playerCentroid = room.player.getCentroid();
                if (new Line(getCentroid(), playerCentroid).getLength() <= shootDistance) {
                    speed = moveSpeed * weapon.shootMovementSpeed;
                    shoot();
                } else {
                    speed = moveSpeed;
                }
            }
        };
        timer.schedule(timertask, 0, weapon.cooldown);
    }
}

class Sharp extends Enemy<Sharp> {
    int shootDistance;
    int moveDistance;
    int strafing;

    Sharp(Room room, int id) {
        super(room, id);
        orginalColor = Color.gray;
        color = orginalColor;
        corpseLength = 1000;
        if (Math.random() < 0.5) {
            strafing = 1;
        } else {
            strafing = -1;
        }
        try {
            Scanner data = new Scanner(new FileReader("data/enemies/sharp" + id + ".txt"));
            scan(data);
            weapon = new Weapon(this, "sharp" + id);
            shootDistance = (int) (Integer.parseInt(data.next()) * (0.1 * Math.random() + 1));
            moveDistance = (int) (Integer.parseInt(data.next()) * (0.1 * Math.random() + 0.9));
        } catch (IOException e) {
            System.out.println("Enemy Loading Error");
            System.exit(-1);
        }
    }

    Sharp(Room room, Point[] points, int id) {
        this(room, id);
        set(points);
    }

    public Sharp clone() {
        Sharp sharp = new Sharp(room, getPoints(), id);
        sharp.shootDistance = shootDistance;
        sharp.moveDistance = moveDistance;
        sharp.strafing = strafing;
        return sharp;
    }

    void process() {
        TimerTask timertask = new TimerTask() {
            public void run() {
                Point playerCentroid = room.player.getCentroid();
                Line line = new Line(getCentroid(), playerCentroid);
                double radian = line.caculateRadian();
                rotate(direction - radian);
                direction = radian;
                if (line.getLength() >= moveDistance) {
                    directionMove(2 * speed / 3, direction);
                    if (room
                            .intersects(directionTranslate(speed, direction + strafing * Math.PI / 2))) {
                        strafing *= -1;
                    }
                    directionMove(speed / 3, direction);
                } else {
                    if (room
                            .intersects(directionTranslate(speed, direction + strafing * Math.PI / 2))) {
                        strafing *= -1;
                    } else if (Math.random() < 0.05) {
                        strafing *= -1;
                    }
                    directionMove(speed, direction + strafing * Math.PI / 2);
                }
            }
        };
        timer.schedule(timertask, 0, Game.delay);
        timertask = new TimerTask() {
            public void run() {
                Point playerCentroid = room.player.getCentroid();
                if (new Line(getCentroid(), playerCentroid).getLength() <= shootDistance) {
                    speed = moveSpeed * weapon.shootMovementSpeed;
                    shoot();
                } else {
                    speed = moveSpeed;
                }
            }
        };
        timer.schedule(timertask, 0, weapon.cooldown);
    }
}