import java.awt.Color;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import Geo.Point;

abstract class Weapon {
    protected Entity entity;
    protected int cooldown;
    protected int shotCount;
    protected int shotCooldown;
    protected double shootMoveSpeed;
    protected int recoil;
    protected double accuracy;
    protected Projectile projectile;

    public Weapon(Entity entity) {
        this.entity = entity;
    }

    static Weapon createWeapon(Entity entity, String name) {
        Weapon weapon = null;
        try {
            Scanner data = new Scanner(new FileReader("data/weapons/" + name + ".txt"));
            String type = Game.parseStr(data);
            switch (type) {
                case "gun":
                    weapon = new Gun(entity);
                    break;
                case "limitedGun":
                    weapon = new LimitedGun(entity);
                    break;
                case "grenade":
                    weapon = new GrenadeLauncher(entity);
                    break;
            }
            weapon.cooldown = Game.parseInt(data);
            weapon.shotCount = Game.parseInt(data);
            if (weapon.shotCount != 1) {
                weapon.shotCooldown = Game.parseInt(data);
            }
            weapon.shootMoveSpeed = Game.parseDouble(data);
            weapon.recoil = Game.parseInt(data);
            weapon.accuracy = Math.PI / Game.parseInt(data);
            switch (type) {
                case "gun":
                    Bullet bullet = new Bullet(entity.getRoom());
                    bullet.damage = Game.parseInt(data);
                    bullet.setWidth(Game.parseInt(data));
                    bullet.length = Game.parseInt(data);
                    bullet.knockback = Game.parseInt(data);
                    bullet.pierce = Game.parseInt(data);
                    bullet.speed = Game.parseInt(data);
                    weapon.projectile = bullet;
                    break;
                case "limitedGun":
                    LimitedBullet limitedBullet = new LimitedBullet(entity.getRoom());
                    limitedBullet.damage = Game.parseInt(data);
                    limitedBullet.setWidth(Game.parseInt(data));
                    limitedBullet.length = Game.parseInt(data);
                    limitedBullet.knockback = Game.parseInt(data);
                    limitedBullet.pierce = Game.parseInt(data);
                    limitedBullet.speed = Game.parseInt(data);
                    limitedBullet.duration = Game.parseInt(data);
                    weapon.projectile = limitedBullet;
                    break;
                case "grenade":
                    Grenade grenade = new Grenade(entity.getRoom());
                    grenade.damage = Game.parseInt(data);
                    grenade.setWidth(Game.parseInt(data));
                    grenade.length = Game.parseInt(data);
                    grenade.knockback = Game.parseInt(data);
                    grenade.speed = Game.parseInt(data);
                    grenade.size = Game.parseInt(data);
                    grenade.maxDistance = Game.parseInt(data);
                    weapon.projectile = grenade;
                    break;
            }
        } catch (IOException e) {
            System.out.println("Weapon Loading Error");
            System.exit(-1);
        }
        weapon.frame = (int) ((0.2 + 0.4 * Math.random()) * weapon.cooldown);
        if (name.contains("player")) {
            weapon.projectile.isPlayer = true;
        }
        return weapon;
    }

    protected int frame;

    public void shoot() {
        frame = 0;
        shot(entity.getCentroid(), entity.direction);
        if (shotCount == 1) {
            return;
        }
        if (shotCooldown == 0) {
            for (int i = 1; i < shotCount; i++) {
                shot(entity.getCentroid(), entity.direction);
            }
            return;
        }
        Timer timer = new Timer();
        TimerTask timertask = new TimerTask() {
            int count = 0;

            public void run() {
                count++;
                shot(entity.getCentroid(), entity.direction);
                if (count == shotCount - 1) {
                    timer.cancel();
                    timer.purge();
                }
            }
        };
        timer.schedule(timertask, shotCooldown * Game.delay,
                shotCooldown * Game.delay);
    }

    double getShootMoveSpeed() {
        return shootMoveSpeed;
    }

    public boolean canShoot() {
        frame++;
        return frame >= cooldown;
    }

    protected abstract void shot(Point centroid, double direction);
}

class Gun extends Weapon {
    Gun(Entity entity) {
        super(entity);
    }

    protected void shot(Point centroid, double direction) {
        direction += accuracy * Math.random() - accuracy / 2;
        entity.attemptMove(recoil, direction);
        Casing casing = new Casing(entity.getRoom(), centroid,
                direction + Math.PI / 2 + Math.PI / 10 * Math.random() - Math.PI / 20, 25, 50, 4, 40, 300);
        casing.setBorderColor(new Color(175, 156, 96));
        casing.setWidth(3);
        new MuzzleFlash(entity, projectile.length);
        projectile.shoot(centroid, direction);
    }
}

class LimitedGun extends Weapon {
    LimitedGun(Entity entity) {
        super(entity);
    }

    protected void shot(Point centroid, double direction) {
        direction += accuracy * Math.random() - accuracy / 2;
        entity.attemptMove(recoil, direction);
        Casing casing = new Casing(entity.getRoom(), centroid,
                direction + Math.PI / 2 + Math.PI / 6 * Math.random() - Math.PI / 12, 20, 35, 4, 24, 200);
        casing.setBorderColor(new Color(175, 156, 96));
        casing.setWidth(2);
        new MuzzleFlash(entity, projectile.length);
        projectile.shoot(centroid, direction);
    }
}

class GrenadeLauncher extends Weapon {
    GrenadeLauncher(Entity entity) {
        super(entity);
    }

    protected void shot(Point centroid, double direction) {
        direction += accuracy * Math.random() - accuracy / 2;
        entity.attemptMove(recoil, direction);
        projectile.shoot(centroid, direction);
    }
}