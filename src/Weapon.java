import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

class Weapon {
    Entity entity;
    int cooldown;
    int shotCount;
    int shotCooldown;
    double shootMovementSpeed;
    int recoil;
    double random;
    Projectile projectile;

    Weapon(Entity entity, String name) {
        this.entity = entity;
        try {
            Scanner data = new Scanner(new FileReader("data/weapons/" + name + ".txt"));
            cooldown = Integer.parseInt(data.next()) * 100;
            shotCount = Integer.parseInt(data.next());
            if (shotCount != 1) {
                shotCooldown = Integer.parseInt(data.next());
            }
            shootMovementSpeed = Double.parseDouble(data.next());
            recoil = Integer.parseInt(data.next());
            random = Math.PI / Integer.parseInt(data.next());
            String projectileType = data.next();
            switch (projectileType) {
                case "bullet":
                    Bullet bullet = new Bullet(entity.room);
                    bullet.damage = Integer.parseInt(data.next());
                    bullet.piercing = Integer.parseInt(data.next());
                    bullet.setWidth(Integer.parseInt(data.next()));
                    bullet.length = Integer.parseInt(data.next());
                    bullet.speed = Integer.parseInt(data.next());
                    bullet.knockback = Integer.parseInt(data.next());
                    projectile = bullet;
                    break;
                case "limitedBullet":
                    LimitedBullet limitedBullet = new LimitedBullet(entity.room);
                    limitedBullet.damage = Integer.parseInt(data.next());
                    limitedBullet.piercing = Integer.parseInt(data.next());
                    limitedBullet.setWidth(Integer.parseInt(data.next()));
                    limitedBullet.length = Integer.parseInt(data.next());
                    limitedBullet.speed = Integer.parseInt(data.next());
                    limitedBullet.knockback = Integer.parseInt(data.next());
                    limitedBullet.duration = Integer.parseInt(data.next());
                    projectile = limitedBullet;
                    break;
            }
        } catch (IOException e) {
            System.out.println("Weapon Loading Error");
            System.exit(-1);
        }
    }

    void shoot(Point centroid, double direction) {
        direction += random * Math.random() - random / 2;
        projectile.setP1(centroid.directionTranslate(20, direction));
        projectile.setP2(centroid.directionTranslate(20 + projectile.length, direction));
        projectile.shoot(direction);
    }
}
