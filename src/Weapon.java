import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import Geo.Point;

class Weapon {
    int cooldown;
    int shotCount;
    int shotCooldown;
    double shootMoveSpeed;
    int recoil;
    private double accuracy;
    Projectile projectile;

    Weapon(Entity entity, String name) {
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

    void shoot(Point centroid, double direction) {
        projectile.shoot(centroid, direction, accuracy);
    }
}
