import java.awt.Color;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.TimerTask;

class Enemy extends Entity {
    int id;
    boolean shoot, shooting;
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
    }

}

class Chaser extends Enemy {
    Chaser(Point[] points, int id) {
        super(points);
        this.id = id;
        color = Color.yellow;
        corpseLength = 100;
        try {
            Scanner data = new Scanner(new FileReader("data/enemies/chaser" + id + ".txt"));
            asdf(data);
            health = Integer.parseInt(data.next());
            moveSpeed = Integer.parseInt(data.next());
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
    Weapon weapon;
    int shootDistance;
    int moveDistance;

    Rifle(Point[] points, int id) {
        super(points);
        this.id = id;
        color = Color.blue;
        corpseLength = 100;
        try {
            Scanner data = new Scanner(new FileReader("data/enemies/rifle" + id + ".txt"));
            asdf(data);
            weapon = new Weapon("rifle" + id);
            health = Integer.parseInt(data.next());
            moveSpeed = Integer.parseInt(data.next());
            shootDistance = (int) (Integer.parseInt(data.next()) * (0.2 * Math.random() + 1));
            moveDistance = (int) (Integer.parseInt(data.next()) * (0.2 * Math.random() + 0.8));
        } catch (

        IOException e) {
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
                    weapon.shoot(centroid, direction);
                } else {
                    speed = moveSpeed;
                }
            }
        };
        timer.schedule(timertask, 0, weapon.cooldown);
    }
}

class Sniper extends Enemy {
    Weapon weapon;
    int shootDistance;
    int moveDistance;
    int runDistance;

    Sniper(Point[] points, int id) {
        super(points);
        this.id = id;
        color = Color.white;
        corpseLength = 100;
        try {
            Scanner data = new Scanner(new FileReader("data/enemies/sniper" + id + ".txt"));
            asdf(data);
            weapon = new Weapon("sniper" + id);
            health = Integer.parseInt(data.next());
            moveSpeed = Integer.parseInt(data.next());
            shootDistance = (int) (Integer.parseInt(data.next()) * (0.2 * Math.random() + 1));
            moveDistance = (int) (Integer.parseInt(data.next()) * (0.2 * Math.random() + 0.8));
            runDistance = (int) (Integer.parseInt(data.next()) * (0.2 * Math.random() + 0.9));
        } catch (

        IOException e) {
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
                    weapon.shoot(centroid, direction);
                } else {
                    speed = moveSpeed;
                }
            }
        };
        timer.schedule(timertask, 0, weapon.cooldown);
    }
}