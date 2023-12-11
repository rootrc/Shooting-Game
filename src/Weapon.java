import java.awt.Color;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import Geo.Point;

class Weapon {
    private Entity entity;
    int cooldown;
    private int shotCount;
    private int shotCooldown;
    double shootMoveSpeed;
    private int recoil;
    private double accuracy;
    Projectile projectile;

    public Weapon(Entity entity, String name) {
        this.entity = entity;
        try {
            Scanner data = new Scanner(new FileReader("data/weapons/" + name + ".txt"));
            cooldown = Game.parseInt(data) * 100;
            shotCount = Game.parseInt(data);
            if (shotCount != 1) {
                shotCooldown = Game.parseInt(data);
            }
            shootMoveSpeed = Game.parseDouble(data);
            recoil = Game.parseInt(data);
            accuracy = Math.PI / Game.parseInt(data);
            String projectileType = Game.parseStr(data);
            switch (projectileType) {
                case "bullet":
                    Bullet bullet = new Bullet(entity.room);
                    bullet.damage = Game.parseInt(data);
                    bullet.piercing = Game.parseInt(data);
                    bullet.setWidth(Game.parseInt(data));
                    bullet.length = Game.parseInt(data);
                    bullet.knockback = Game.parseInt(data);
                    bullet.speed = Game.parseInt(data);
                    projectile = bullet;
                    break;
                case "limitedBullet":
                    LimitedBullet limitedBullet = new LimitedBullet(entity.room);
                    limitedBullet.damage = Game.parseInt(data);
                    limitedBullet.piercing = Game.parseInt(data);
                    limitedBullet.setWidth(Game.parseInt(data));
                    limitedBullet.length = Game.parseInt(data);
                    limitedBullet.knockback = Game.parseInt(data);
                    limitedBullet.speed = Game.parseInt(data);
                    limitedBullet.duration = Game.parseInt(data);
                    projectile = limitedBullet;
                    break;
            }
        } catch (IOException e) {
            System.out.println("Weapon Loading Error");
            System.exit(-1);
        }
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

    private void shot(Point centroid, double direction) {
        direction += accuracy * Math.random() - accuracy / 2;
        entity.attemptMove(recoil, direction);
        Casing casing = new Casing(entity.room, entity.getCentroid(),
                direction + Math.PI / 2 + Math.PI / 10 * Math.random() - Math.PI / 20, 25, 50, 4, 40, 200);
        casing.setBorderColor(new Color(175, 156, 96));
        casing.setWidth(3);
        new MuzzleFlash(entity);
        projectile.shoot(centroid, direction);
    }
}