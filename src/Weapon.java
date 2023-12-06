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
                    bullet.random = Integer.parseInt(data.next());
                    projectile = bullet;
                    break;
                }
        } catch (IOException e) {
            System.out.println("Weapon Loading Error");
            System.exit(-1);
        }
    }

    void shoot(Point centroid, double direction) {
        direction += Math.PI / projectile.random * Math.random() - Math.PI / 2 / projectile.random;
        projectile.p1 = centroid.translate(-projectile.length * Math.cos(direction),
                projectile.length * Math.sin(direction));
        projectile.p2 = centroid.translate(-2 * projectile.length * Math.cos(direction),
                2 * projectile.length * Math.sin(direction));
        projectile.shoot(direction);
    }
}
