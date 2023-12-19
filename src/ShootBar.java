import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

class ShootBar {
    private Entity entity;

    ShootBar(Entity entity) {
        this.entity = entity;
    }

    void draw(Graphics2D g2d, int x, int y) {
        if (entity.weapon == null) {
            return;
        }
        g2d.setStroke(new BasicStroke(1));
        int width = (int) (entity.size * 1.8);
        int height = (int) (entity.size * 0.2);
        double ratio = Math.min(1, (double) entity.weapon.frame / entity.weapon.cooldown);
        g2d.setColor(Color.orange);
        g2d.fillRect((int) Math.round(entity.getCentroid().getX() - width / 2) + x,
                (int) Math.round(entity.getCentroid().getY() - entity.size * 1.4) + y, (int) Math.round(width * ratio), height);
        g2d.setColor(Color.magenta);
        g2d.fillRect((int) Math.round(entity.getCentroid().getX() - width / 2) + x + (int) Math.round(width * ratio),
                (int) Math.round(entity.getCentroid().getY() - entity.size * 1.4) + y, width - (int) Math.round(width * ratio), height);
    }
}