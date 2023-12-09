import java.awt.Color;
import java.awt.Graphics2D;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

class Enemy extends Entity {
    int id;
    Line muzzleFlash;
    boolean muzzleFlashing;
    double speed;
    double moveSpeed;
    double rotationSpeed;

    Weapon weapon;

    Enemy(Point[] points) {
        super(points);
    }

    // for inheritance
    Enemy translate(double x, double y) {
        return this;
    }

    void asdf(Scanner data) throws IOException {
        muzzleFlash = new Line(new Point(0, 0), new Point(0, 0));
        muzzleFlash.color = new Color(247, 241, 181);
        muzzleFlash.width = 8;
        if (points == null) {
            length = Integer.parseInt(data.next());
            this.points = new Point[length];
            for (int j = 0; j < length; j++) {
                this.points[j] = new Point(Double.parseDouble(data.next()), Double.parseDouble(data.next()));
            }
            this.lines = new Line[length];
            for (int i = 0; i < length; i++) {
                lines[i] = new Line(this.points[i], this.points[(i + 1) % length]);
            }
            computeCentroid();
        } else {
            for (int j = 0; j <= length; j++) {
                data.nextLine();
            }
        }
        value = Integer.parseInt(data.next()) * 10;
        health = Integer.parseInt(data.next());
        moveSpeed = Double.parseDouble(data.next());
    }

    void draw(Graphics2D g2d) {
        if (muzzleFlashing) {
            muzzleFlash.draw(g2d);
        }
        super.draw(g2d);
    }

    void shoot() {
        weapon.shoot(centroid, direction);
        attemptMovement(weapon.recoil, direction);
        Casing casing = new Casing(centroid, direction + Math.PI / 2 + Math.PI / 10 * Math.random() - Math.PI / 20, 20, 40, 4, 30, 100);
        casing.color = new Color(175, 156, 96);
        casing.width = 3;
        muzzleFlashing = true;
        double muzzleDirection = direction;
        Timer timer = new Timer();
        TimerTask timertask = new TimerTask() {
            int count = 0;

            public void run() {
                count++;
                muzzleFlash.p1 = centroid.clone();
                muzzleFlash.p2 = centroid.translate(
                        -(20 + weapon.projectile.length / 2) * Math.cos(muzzleDirection),
                        (20 + weapon.projectile.length / 2) * Math.sin(muzzleDirection));
                if (count == 3) {
                    muzzleFlashing = false;
                    timer.cancel();
                    timer.purge();
                }
            }
        };
        timer.schedule(timertask, 0, Game.getInstance().delay);
        if (weapon.shotCount == 1) {
            return;
        }
        Timer timer2 = new Timer();
        TimerTask timertask2 = new TimerTask() {
            int count = 0;

            public void run() {
                count++;
                attemptMovement(weapon.recoil, direction);
                muzzleFlashing = true;
                double muzzleDirection = direction;
                Timer timer = new Timer();
                TimerTask timertask = new TimerTask() {
                    int count = 0;

                    public void run() {
                        count++;
                        muzzleFlash.p1 = centroid.clone();
                        muzzleFlash.p2 = centroid.translate(
                                -(20 + weapon.projectile.length / 2) * Math.cos(muzzleDirection),
                                (20 + weapon.projectile.length / 2) * Math.sin(muzzleDirection));
                        if (count == 3) {
                            muzzleFlashing = false;
                            timer.cancel();
                            timer.purge();
                        }
                    }
                };
                timer.schedule(timertask, 0, Game.getInstance().delay);
                weapon.shoot(centroid, direction);
                if (count == weapon.shotCount - 1) {
                    timer2.cancel();
                    timer2.purge();
                }
            }
        };
        timer2.schedule(timertask2, weapon.shotCooldown * Game.getInstance().delay,
                weapon.shotCooldown * Game.getInstance().delay);
    }

    void hit() {
        color = new Color(139, 0, 0);
        Timer timer = new Timer();
        speed /= 2;
        moveSpeed /= 2;
        rotationSpeed /= 2;
        TimerTask timertask = new TimerTask() {
            public void run() {
                Enemy.this.color = orginalColor;
                speed *= 2;
                moveSpeed *= 2;
                rotationSpeed *= 2;
            }
        };
        timer.schedule(timertask, 10 * Game.getInstance().delay);
    }

    void death() {
        Game.getInstance().room.entities.remove(this);
        timer.cancel();
        timer.purge();
        Game.getInstance().room.score += value;
        Polygon corpse = this.clone();
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
                    Game.getInstance().room.polygons.remove(corpse);
                    timer.cancel();
                    timer.purge();
                }
            }
        };
        Game.getInstance().room.polygons.add(corpse);
        timer.schedule(timertask, 30 * Game.getInstance().delay, Game.getInstance().delay);
    }

}

class Chaser extends Enemy {
    Chaser(Point[] points, int id) {
        super(points);
        this.id = id;
        orginalColor = Color.yellow;
        color = orginalColor;
        corpseLength = 600;
        try {
            Scanner data = new Scanner(new FileReader("data/enemies/chaser" + id + ".txt"));
            asdf(data);
        } catch (IOException e) {
            System.out.println("Enemy Loading Error");
            System.exit(-1);
        }
        speed = moveSpeed;
    }

    Chaser translate(double x, double y) {
        Point[] points = new Point[length];
        for (int i = 0; i < length; i++) {
            points[i] = this.points[i].translate(x, y);
        }
        Chaser chaser = new Chaser(points, id);
        return chaser;
    }

    void process() {
        TimerTask timertask = new TimerTask() {
            public void run() {
                Chaser.super.process();
                Point playerCentroid = Game.getInstance().room.player.centroid;
                double radian = new Line(centroid, playerCentroid).caculateRadian();
                rotate(direction - radian);
                direction = radian;
                move(-moveSpeed * Math.cos(direction), moveSpeed * Math.sin(direction));
            }
        };
        timer.schedule(timertask, 0, Game.getInstance().delay);
    }
}

class Rifle extends Enemy {
    int shootDistance;
    int moveDistance;

    Rifle(Point[] points, int id) {
        super(points);
        this.id = id;
        orginalColor = Color.blue;
        color = orginalColor;
        corpseLength = 600;
        try {
            Scanner data = new Scanner(new FileReader("data/enemies/rifle" + id + ".txt"));
            asdf(data);
            weapon = new Weapon("rifle" + id);
            shootDistance = (int) (Integer.parseInt(data.next()) * (0.2 * Math.random() + 1));
            moveDistance = (int) (Integer.parseInt(data.next()) * (0.2 * Math.random() + 0.8));
        } catch (IOException e) {
            System.out.println("Enemy Loading Error");
            System.exit(-1);
        }
    }

    Rifle translate(double x, double y) {
        Point[] points = new Point[length];
        for (int i = 0; i < length; i++) {
            points[i] = this.points[i].translate(x, y);
        }
        Rifle rifle = new Rifle(points, id);
        return rifle;
    }

    void process() {
        TimerTask timertask = new TimerTask() {
            public void run() {
                Rifle.super.process();
                Point playerCentroid = Game.getInstance().room.player.centroid;
                Line line = new Line(centroid, playerCentroid);
                double radian = line.caculateRadian();
                rotate(direction - radian);
                direction = radian;
                if (line.length >= moveDistance) {
                    move(-speed * Math.cos(direction), speed * Math.sin(direction));
                }
            }
        };
        timer.schedule(timertask, 0, Game.getInstance().delay);
        timertask = new TimerTask() {
            public void run() {
                Point playerCentroid = Game.getInstance().room.player.centroid;
                if (new Line(centroid, playerCentroid).length <= shootDistance) {
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

class Sniper extends Enemy {
    int shootDistance;
    int moveDistance;
    int runDistance;

    Sniper(Point[] points, int id) {
        super(points);
        this.id = id;
        orginalColor = Color.white;
        color = orginalColor;
        corpseLength = 600;
        try {
            Scanner data = new Scanner(new FileReader("data/enemies/sniper" + id + ".txt"));
            asdf(data);
            weapon = new Weapon("sniper" + id);
            shootDistance = (int) (Integer.parseInt(data.next()) * (0.2 * Math.random() + 1));
            moveDistance = (int) (Integer.parseInt(data.next()) * (0.2 * Math.random() + 0.8));
            runDistance = (int) (Integer.parseInt(data.next()) * (0.2 * Math.random() + 0.9));
        } catch (IOException e) {
            System.out.println("Enemy Loading Error");
            System.exit(-1);
        }
    }

    Sniper translate(double x, double y) {
        Point[] points = new Point[length];
        for (int i = 0; i < length; i++) {
            points[i] = this.points[i].translate(x, y);
        }
        Sniper sniper = new Sniper(points, id);
        return sniper;
    }

    void process() {
        TimerTask timertask = new TimerTask() {
            public void run() {
                Sniper.super.process();
                Point playerCentroid = Game.getInstance().room.player.centroid;
                Line line = new Line(centroid, playerCentroid);
                double radian = line.caculateRadian();
                rotate(direction - radian);
                direction = radian;
                if (line.length >= moveDistance) {
                    move(-speed * Math.cos(direction), speed * Math.sin(direction));
                } else if (Sniper.super.translate(speed * Math.cos(direction), -speed * Math.sin(direction))
                        .intersects(Game.getInstance().room)) {
                    move(-moveSpeed * Math.cos(direction), moveSpeed * Math.sin(direction));
                } else if (runDistance >= line.length) {
                    if (!Sniper.super.translate(speed * Math.cos(direction), -speed * Math.sin(direction))
                            .intersects(Game.getInstance().room.boundingBox(20))) {
                        move(speed * Math.cos(direction), -speed * Math.sin(direction));
                    }
                }

            }
        };
        timer.schedule(timertask, 0, Game.getInstance().delay);
        timertask = new TimerTask() {
            public void run() {
                Point playerCentroid = Game.getInstance().room.player.centroid;
                double length = new Line(centroid, playerCentroid).length;
                if (length > shootDistance) {
                    speed = moveSpeed;
                    return;
                }
                if (runDistance <= length && length <= shootDistance
                        || Sniper.super.translate(speed * Math.cos(direction), -speed * Math.sin(direction))
                                .intersects(Game.getInstance().room.boundingBox(20))) {
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

class Machine extends Enemy {
    int shootDistance;
    int moveDistance;

    Machine(Point[] points, int id) {
        super(points);
        this.id = id;
        orginalColor = Color.green;
        color = orginalColor;
        corpseLength = 700;
        try {
            Scanner data = new Scanner(new FileReader("data/enemies/machine" + id + ".txt"));
            asdf(data);
            weapon = new Weapon("machine" + id);
            shootDistance = (int) (Integer.parseInt(data.next()) * (0.2 * Math.random() + 1));
            moveDistance = (int) (Integer.parseInt(data.next()) * (0.2 * Math.random() + 0.8));
        } catch (IOException e) {
            System.out.println("Enemy Loading Error");
            System.exit(-1);
        }
    }

    Machine translate(double x, double y) {
        Point[] points = new Point[length];
        for (int i = 0; i < length; i++) {
            points[i] = this.points[i].translate(x, y);
        }
        Machine machine = new Machine(points, id);
        return machine;
    }

    void process() {
        TimerTask timertask = new TimerTask() {
            public void run() {
                Machine.super.process();
                Point playerCentroid = Game.getInstance().room.player.centroid;
                Line line = new Line(centroid, playerCentroid);
                double radian = line.caculateRadian();
                rotate(direction - radian);
                direction = radian;
                if (line.length >= moveDistance) {
                    move(-speed * Math.cos(direction), speed * Math.sin(direction));
                }
            }
        };
        timer.schedule(timertask, 0, Game.getInstance().delay);
        timertask = new TimerTask() {
            public void run() {
                Point playerCentroid = Game.getInstance().room.player.centroid;
                if (new Line(centroid, playerCentroid).length <= shootDistance) {
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

class Sharp extends Enemy {
    int shootDistance;
    int moveDistance;
    int strafing;

    Sharp(Point[] points, int id) {
        super(points);
        this.id = id;
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
            asdf(data);
            weapon = new Weapon("sharp" + id);
            shootDistance = (int) (Integer.parseInt(data.next()) * (0.1 * Math.random() + 1));
            moveDistance = (int) (Integer.parseInt(data.next()) * (0.1 * Math.random() + 0.9));
        } catch (IOException e) {
            System.out.println("Enemy Loading Error");
            System.exit(-1);
        }
    }

    Sharp translate(double x, double y) {
        Point[] points = new Point[length];
        for (int i = 0; i < length; i++) {
            points[i] = this.points[i].translate(x, y);
        }
        Sharp sharp = new Sharp(points, id);
        return sharp;
    }

    void process() {
        TimerTask timertask = new TimerTask() {
            public void run() {
                Sharp.super.process();
                Point playerCentroid = Game.getInstance().room.player.centroid;
                Line line = new Line(centroid, playerCentroid);
                double radian = line.caculateRadian();
                rotate(direction - radian);
                direction = radian;
                if (line.length >= moveDistance) {
                    move(-2 * speed / 3 * Math.cos(direction), 2 * speed / 3 * Math.sin(direction));
                    if (Game.getInstance().room.intersects(translate(-speed * Math.cos(direction + strafing * Math.PI / 2), speed * Math.sin(direction + strafing * Math.PI / 2)))) {
                        strafing *= -1;
                    }
                    move(-speed / 3 * Math.cos(direction + strafing * Math.PI / 2), speed / 3 * Math.sin(direction + strafing * Math.PI / 2));
                } else {
                    if (Game.getInstance().room.intersects(translate(-speed * Math.cos(direction + strafing * Math.PI / 2), speed * Math.sin(direction + strafing * Math.PI / 2)))) {
                        strafing *= -1;
                    } else if (Math.random() < 0.05) {
                        strafing *= -1;
                    }
                    move(-speed * Math.cos(direction + strafing * Math.PI / 2), speed * Math.sin(direction + strafing * Math.PI / 2));
                }
            }
        };
        timer.schedule(timertask, 0, Game.getInstance().delay);
        timertask = new TimerTask() {
            public void run() {
                Point playerCentroid = Game.getInstance().room.player.centroid;
                if (new Line(centroid, playerCentroid).length <= shootDistance) {
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