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
            switch(type) {
                case "gun":  
                    weapon = new Gun(entity);
                    break;
                case "limitedGun":
                    weapon = new LimitedGun(entity);
                    break;
            }
            weapon.cooldown = Game.parseInt(data) * 100;
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
                    bullet.piercing = Game.parseInt(data);
                    bullet.setWidth(Game.parseInt(data));
                    bullet.length = Game.parseInt(data);
                    bullet.knockback = Game.parseInt(data);
                    bullet.speed = Game.parseInt(data);
                    weapon.projectile = bullet;
                    break;
                case "limitedGun":
                    LimitedBullet limitedBullet = new LimitedBullet(entity.getRoom());
                    limitedBullet.damage = Game.parseInt(data);
                    limitedBullet.piercing = Game.parseInt(data);
                    limitedBullet.setWidth(Game.parseInt(data));
                    limitedBullet.length = Game.parseInt(data);
                    limitedBullet.knockback = Game.parseInt(data);
                    limitedBullet.speed = Game.parseInt(data);
                    limitedBullet.duration = Game.parseInt(data);
                    weapon.projectile = limitedBullet;
                    break;
            }
        } catch (IOException e) {
            System.out.println("Weapon Loading Error");
            System.exit(-1);
        }
        if (name.contains("player")) {
            weapon.projectile.isPlayer = true;
        }
        return weapon;
    }

    public void shoot() {
        shot(entity.getCentroid(), entity.direction);
        if (shotCount == 1) {
            return;
        }
        Timer timer2 = new Timer();
        TimerTask timertask2 = new TimerTask() {
            int count = 0;

            public void run() {
                count++;
                shot(entity.getCentroid(), entity.direction);
                if (count == shotCount - 1) {
                    timer2.cancel();
                    timer2.purge();
                }
            }
        };
        timer2.schedule(timertask2, shotCooldown * Game.delay,
                shotCooldown * Game.delay);
    }

    int getCooldown() {
        return cooldown;
    }

    double getShootMoveSpeed() {
        return shootMoveSpeed;
    }
    protected abstract void shot(Point centroid, double direction);
}

class Gun extends Weapon {
    Gun (Entity entity) {
        super(entity);
    }
    protected void shot(Point centroid, double direction) {
        direction += accuracy * Math.random() - accuracy / 2;
        entity.attemptMove(recoil, direction);
        Casing casing = new Casing(entity.getRoom(), centroid,
                direction + Math.PI / 2 + Math.PI / 10 * Math.random() - Math.PI / 20, 25, 50, 4, 40, 200);
        casing.setBorderColor(new Color(175, 156, 96));
        casing.setWidth(3);
        new MuzzleFlash(entity, projectile.length);
        projectile.shoot(centroid, direction);
    }
}

class LimitedGun extends Weapon {
    LimitedGun (Entity entity) {
        super(entity);
    }
    protected void shot(Point centroid, double direction) {
        direction += accuracy * Math.random() - accuracy / 2;
        entity.attemptMove(recoil, direction);
        Casing casing = new Casing(entity.getRoom(), centroid,
                direction + Math.PI / 2 + Math.PI / 10 * Math.random() - Math.PI / 20, 25, 45, 4, 32, 300);
        casing.setBorderColor(new Color(175, 156, 96));
        casing.setWidth(3);
        new MuzzleFlash(entity, projectile.length);
        projectile.shoot(centroid, direction);
    }
}