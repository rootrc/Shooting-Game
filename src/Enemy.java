import java.awt.Color;
import java.awt.Graphics2D;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import Geo.Line;
import Geo.Point;

abstract class Enemy<T extends Enemy<T>> extends Entity {
    private static HashMap<String, Enemy<?>> map = new HashMap<>();
    protected int id;
    protected double speed;
    protected double moveSpeed;
    protected double rotationSpeed;
    private int value;

    private Enemy(Room room, Point[] points) {
        super(room, points);
    }

    Enemy(Room room, int id) {
        super(room);
        this.id = id;
    }

    void scan(Scanner data) throws IOException {
        int length = Game.parseInt(data);
        Point[] points = new Point[length];
        for (int j = 0; j < length; j++) {
            points[j] = Game.parsePoint(data);
        }
        set(points);
        value = Game.parseInt(data) * 10;
        health = Game.parseInt(data);
        moveSpeed = Game.parseDouble(data);
        speed = moveSpeed;
    }

    protected void draw(Graphics2D g2d, int x, int y) {
        super.draw(g2d, x, y);
    }

    protected void shoot() {
        speed = moveSpeed * weapon.getShootMoveSpeed();
        weapon.shoot();
    }

    protected double turnToPlayer() {
        Point playerCentroid = getRoom().player.getCentroid();
        Line line = new Line(getCentroid(), playerCentroid);
        double radian = line.caculateRadian();
        rotate(direction - radian);
        direction = radian;
        return line.getLength();
    }    

    protected double distanceToPlayer() {
        Point playerCentroid = getRoom().player.getCentroid();
        Line line = new Line(getCentroid(), playerCentroid);
        return line.getLength();
    }

    protected void hit() {
        setColor(new Color(139, 0, 0));
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

    protected void death() {
        getRoom().entities.remove(this);
        timer.cancel();
        timer.purge();
        getRoom().increaseScore(value);
        new Corpse(this, corpseTime);
    }

    public T translate(int x, int y) {
        return translate((double) x, (double) y);
    }

    public T translate(double x, double y) {
        T t = clone();
        t.move(x, y);
        return t;
    }

    void directionMove() {
        directionMove(speed, direction);
    }

    static Enemy<?> createEnemy(Room room, String name) {
        if (map.containsKey(name)) {
            return map.get(name);
        }
        switch (name.substring(0, name.length() - 1)) {
            case "chaser":
                map.put(name, new Chaser(room, name.charAt(name.length() - 1) - '0'));
                break;
            case "rifle":
                map.put(name, new Rifle(room, name.charAt(name.length() - 1) - '0'));
                break;
            case "sniper":
                map.put(name, new Sniper(room, name.charAt(name.length() - 1) - '0'));
                break;
            case "machine":
                map.put(name, new Machine(room, name.charAt(name.length() - 1) - '0'));
                break;
            case "sharp":
                map.put(name, new Sharp(room, name.charAt(name.length() - 1) - '0'));
                break;
        }
        return map.get(name);
    }
    public abstract T clone();

    abstract void process();

}

class Chaser extends Enemy<Chaser> {
    Chaser(Room room, int id) {
        super(room, id);
        orginalColor = Color.yellow;
        setColor(orginalColor);
        corpseTime = 600;
        try {
            Scanner data = new Scanner(new FileReader("data/enemies/chaser" + id + ".txt"));
            scan(data);
        } catch (IOException e) {
            System.out.println("Enemy Loading Error");
            System.exit(-1);
        }
    }

    private Chaser(Room room, Point[] points, int id) {
        this(room, id);
        set(points);
    }

    public Chaser clone() {
        Chaser chaser = new Chaser(getRoom(), getPoints(), id);
        return chaser;
    }
    
    void process() {
        TimerTask timertask = new TimerTask() {
            public void run() {
                turnToPlayer();
                directionMove();
            }
        };
        timer.schedule(timertask, 0, Game.delay);
    }
}

class Rifle extends Enemy<Rifle> {
    private double shootDistance;
    private double moveDistance;

    Rifle(Room room, int id) {
        super(room, id);
        orginalColor = Color.blue;
        setColor(orginalColor);
        corpseTime = 600;
        try {
            Scanner data = new Scanner(new FileReader("data/enemies/rifle" + id + ".txt"));
            scan(data);
            weapon = Weapon.createWeapon(this, "rifle" + id);
            shootDistance = Game.parseInt(data) * (0.2 * Math.random() + 1);
            moveDistance = Game.parseInt(data) * (0.2 * Math.random() + 0.8);
        } catch (IOException e) {
            System.out.println("Enemy Loading Error");
            System.exit(-1);
        }
    }

    private Rifle(Room room, Point[] points, int id) {
        this(room, id);
        set(points);
    }

    public Rifle clone() {
        Rifle rifle = new Rifle(getRoom(), getPoints(), id);
        rifle.shootDistance = shootDistance;
        rifle.moveDistance = moveDistance;
        return rifle;
    }

    void process() {
        TimerTask timertask = new TimerTask() {
            public void run() {
                double distance = turnToPlayer();
                if (distance >= moveDistance) {
                    directionMove();
                }
            }
        };
        timer.schedule(timertask, 0, Game.delay);
        shoot();
    }

    protected void shoot() {
        TimerTask timertask = new TimerTask() {
            public void run() {
                double distance = distanceToPlayer();
                if (distance <= shootDistance) {
                    Rifle.super.shoot();
                } else {
                    speed = moveSpeed;
                }
            }
        };
        timer.schedule(timertask, 0, weapon.getCooldown());
    }
}

class Sniper extends Enemy<Sniper> {
    private double shootDistance;
    private double moveDistance;
    private double runDistance;

    Sniper(Room room, int id) {
        super(room, id);
        orginalColor = Color.white;
        setColor(orginalColor);
        corpseTime = 600;
        try {
            Scanner data = new Scanner(new FileReader("data/enemies/sniper" + id + ".txt"));
            scan(data);
            weapon = Weapon.createWeapon(this, "sniper" + id);
            shootDistance = Game.parseInt(data) * (0.2 * Math.random() + 1);
            moveDistance = Game.parseInt(data) * (0.2 * Math.random() + 0.8);
            runDistance = Game.parseInt(data) * (0.2 * Math.random() + 0.9);
        } catch (IOException e) {
            System.out.println("Enemy Loading Error");
            System.exit(-1);
        }
    }

    private Sniper(Room room, Point[] points, int id) {
        this(room, id);
        set(points);
    }

    public Sniper clone() {
        Sniper sniper = new Sniper(getRoom(), getPoints(), id);
        sniper.shootDistance = shootDistance;
        sniper.moveDistance = moveDistance;
        sniper.runDistance = runDistance;
        return sniper;
    }

    void process() {
        TimerTask timertask = new TimerTask() {
            public void run() {
                double distance = turnToPlayer();
                if (distance >= moveDistance) {
                    directionMove();
                } else if (Sniper.this.directionTranslate(-speed, direction)
                        .intersects(getRoom().boundingBox(20))) {
                    directionMove(moveSpeed, direction);
                } else if (runDistance >= distance) {
                    if (!Sniper.this.directionTranslate(-speed, direction)
                            .intersects(getRoom().boundingBox(30))) {
                        directionMove(-speed, direction);
                    }
                }

            }
        };
        timer.schedule(timertask, 0, Game.delay);
        shoot();
    }

    protected void shoot() {
        TimerTask timertask = new TimerTask() {
            public void run() {
                double distance = distanceToPlayer();
                if (distance > shootDistance) {
                    speed = moveSpeed;
                    return;
                }
                if (runDistance <= distance && distance <= shootDistance
                        || Sniper.this.directionTranslate(-speed, direction)
                                .intersects(getRoom().boundingBox(30))) {
                    Sniper.super.shoot();
                } else {
                    speed = moveSpeed;
                }
            }
        };
        timer.schedule(timertask, 0, weapon.getCooldown());
    }
}

class Machine extends Enemy<Machine> {
    private double shootDistance;
    private double moveDistance;

    Machine(Room room, int id) {
        super(room, id);
        orginalColor = Color.green;
        setColor(orginalColor);
        corpseTime = 700;
        try {
            Scanner data = new Scanner(new FileReader("data/enemies/machine" + id + ".txt"));
            scan(data);
            weapon = Weapon.createWeapon(this, "machine" + id);
            shootDistance = Game.parseInt(data) * (0.2 * Math.random() + 1);
            moveDistance = Game.parseInt(data) * (0.2 * Math.random() + 0.8);
        } catch (IOException e) {
            System.out.println("Enemy Loading Error");
            System.exit(-1);
        }
    }

    private Machine(Room room, Point[] points, int id) {
        this(room, id);
        set(points);
    }

    public Machine clone() {
        Machine machine = new Machine(getRoom(), getPoints(), id);
        machine.shootDistance = shootDistance;
        machine.moveDistance = moveDistance;
        return machine;
    }

    void process() {
        TimerTask timertask = new TimerTask() {
            public void run() {
                double distance = turnToPlayer();
                if (distance >= moveDistance) {
                    directionMove();
                }
            }
        };
        timer.schedule(timertask, 0, Game.delay);
        shoot();
    }

    protected void shoot() {
        TimerTask timertask = new TimerTask() {
            public void run() {
                double distance = distanceToPlayer();
                if (distance <= shootDistance) {
                    Machine.super.shoot();
                } else {
                    speed = moveSpeed;
                }
            }
        };
        timer.schedule(timertask, 0, weapon.getCooldown());
    }
}

class Sharp extends Enemy<Sharp> {
    private double shootDistance;
    private double moveDistance;
    private int strafing;

    Sharp(Room room, int id) {
        super(room, id);
        orginalColor = Color.gray;
        setColor(orginalColor);
        corpseTime = 1000;
        if (Math.random() < 0.5) {
            strafing = 1;
        } else {
            strafing = -1;
        }
        try {
            Scanner data = new Scanner(new FileReader("data/enemies/sharp" + id + ".txt"));
            scan(data);
            weapon = Weapon.createWeapon(this, "sharp" + id);
            shootDistance = Game.parseInt(data) * (0.1 * Math.random() + 1);
            moveDistance = Game.parseInt(data) * (0.1 * Math.random() + 0.9);
        } catch (IOException e) {
            System.out.println("Enemy Loading Error");
            System.exit(-1);
        }
    }

    private Sharp(Room room, Point[] points, int id) {
        this(room, id);
        set(points);
    }

    public Sharp clone() {
        Sharp sharp = new Sharp(getRoom(), getPoints(), id);
        sharp.shootDistance = shootDistance;
        sharp.moveDistance = moveDistance;
        sharp.strafing = strafing;
        return sharp;
    }

    void process() {
        TimerTask timertask = new TimerTask() {
            public void run() {
                double distance = turnToPlayer();
                if (distance >= moveDistance) {
                    directionMove(2 * speed / 3, direction);
                    if (getRoom()
                            .intersects(directionTranslate(speed, direction + strafing * Math.PI / 2))) {
                        strafing *= -1;
                    }
                    directionMove(speed / 3, direction);
                } else {
                    if (getRoom()
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
        shoot();
    }

    protected void shoot() {
        TimerTask timertask = new TimerTask() {
            public void run() {
                double distance = distanceToPlayer();
                if (distance <= shootDistance) {
                    shoot();
                } else {
                    speed = moveSpeed;
                }
            }
        };
        timer.schedule(timertask, 0, weapon.getCooldown());
    }
}