import java.awt.Color;
import java.awt.Graphics2D;

import Geo.Circle;
import Geo.Line;

class Explosion extends Circle {
    private Room room;
    private double size;
    private int damage;
    private int knockback;

    Explosion(Grenade grenade) {
        super(grenade.getP2(), 0);
        room = grenade.getRoom();
        size = grenade.size;
        damage = grenade.damage;
        knockback = grenade.knockback;
        setColor(Color.red);
        setBorderColor(Color.red);
        room.explosions.add(this);
    }

    protected void draw(Graphics2D g2d, int x, int y) {
        super.draw(g2d, x, y);
    }

    protected void fill(Graphics2D g2d, int x, int y) {
        super.fill(g2d, x, y);
    }

    private double speed = 8;

    public void process() {
        r += speed;
        speed -= 0.1;
        for (Entity entity : room.entities) {
            if (entity.isInside(this)) {
                entity.health -= damage;
                double direction = new Line(center, entity.getCentroid()).caculateRadian();
                entity.attemptMove(-knockback, direction);
                if (entity.health > 0) {
                    entity.hit();
                } else {
                    entity.death();
                }
            }
        }
        if (r >= size) {
            room.explosions.remove(this);
        }
    }
}
