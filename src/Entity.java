import java.awt.Color;
import java.util.Timer;

class Entity extends Polygon {
    Timer timer = new Timer();
    double direction;
    int health;
    int value;
    Color orginalColor;
    int corpseLength;

    Entity(Point[] points) {
        super(points);
    }

    public Entity clone() {
        Point[] points = new Point[length];
        for (int i = 0; i < length; i++) {
            points[i] = this.points[i].clone();
        }
        Entity entity = new Entity(points);
        entity.color = color;
        entity.health = health;
        entity.corpseLength = corpseLength;
        return entity;
    }

    void process() {
        for (Projectile projectile : Game.getInstance().room.projectiles) {
            if (getClass() != Player.class && !projectile.isPlayer) {
                continue;
            }
            if (intersects(projectile)) {
                health -= projectile.damage;
                if (!Game.getInstance().room
                        .intersects(this.translate(-projectile.knockback * Math.cos(projectile.caculateRadian()),
                                projectile.knockback * Math.sin(projectile.caculateRadian())))) {
                    move(-projectile.knockback * Math.cos(projectile.caculateRadian()),
                            projectile.knockback * Math.sin(projectile.caculateRadian()));
                } else {
                    int l = 0;
                    int r = projectile.knockback;
                    while (l < r) {
                        int m = (l + r + 1) / 2;
                        if (!Game.getInstance().room
                                .intersects(this.translate(-m * Math.cos(projectile.caculateRadian()),
                                        m * Math.sin(projectile.caculateRadian())))) {
                            l = m;
                        } else {
                            r = m - 1;
                        }
                    }
                    move(-l * Math.cos(projectile.caculateRadian()),
                            l * Math.sin(projectile.caculateRadian()));
                }
                if (health > 0) {
                    hit();
                } else {
                    death();
                }
                projectile.piercing--;
                if (projectile.piercing <= 0) {
                    Game.getInstance().room.projectiles.remove(projectile);
                }
            }
        }
    }

    // for inheritance
    void hit() {

    }

    void death() {

    }
}
