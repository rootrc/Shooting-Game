import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

class Weapon {

    int cooldown;
    int shots;
    int shotCooldown;
    double shootMovementSpeed;

    Projectile projectile;

    Weapon(String name) {
        try {
            Scanner data = new Scanner(new FileReader("data/weapons/" + name + ".txt"));
            cooldown = Integer.parseInt(data.next()) * 100;
            shots = Integer.parseInt(data.next());
            if (shots != 1) {
                shotCooldown = Integer.parseInt(data.next());
            }
            shootMovementSpeed = Double.parseDouble(data.next());
            String projectileType = data.next();
            switch (projectileType) {
                case "bullet":
                    Bullet bullet = new Bullet(new Point(0, 0), new Point(0, 0));
                    bullet.damage = Integer.parseInt(data.next());
                    bullet.piercing = Integer.parseInt(data.next());
                    bullet.setWidth(Integer.parseInt(data.next()));
                    bullet.length = Integer.parseInt(data.next());
                    bullet.speed = Integer.parseInt(data.next());
                    bullet.random = Math.PI / Integer.parseInt(data.next());
                    projectile = bullet;
                    break;
                case "limitedBullet":
                    LimitedBullet limitedBullet = new LimitedBullet(new Point(0, 0), new Point(0, 0));
                    limitedBullet.damage = Integer.parseInt(data.next());
                    limitedBullet.piercing = Integer.parseInt(data.next());
                    limitedBullet.setWidth(Integer.parseInt(data.next()));
                    limitedBullet.length = Integer.parseInt(data.next());
                    limitedBullet.speed = Integer.parseInt(data.next());
                    limitedBullet.duration = Integer.parseInt(data.next());
                    limitedBullet.random = Math.PI / Integer.parseInt(data.next());
                    projectile = limitedBullet;
                    break;
                }
        } catch (IOException e) {
            System.out.println("Weapon Loading Error");
            System.exit(-1);
        }
    }

    void shoot(Point centroid, double direction) {
        direction +=  projectile.random * Math.random() - projectile.random / 2;
        projectile.p1 = centroid.translate(-20 * Math.cos(direction),
                20 * Math.sin(direction));
        projectile.p2 = centroid.translate((-20 - projectile.length) * Math.cos(direction),
                (20 + projectile.length) * Math.sin(direction));
        projectile.shoot(direction);
    }
}
