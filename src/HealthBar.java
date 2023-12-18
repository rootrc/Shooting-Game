import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

class HealthBar {
    private Entity entity;
    private int totalHealth;

    HealthBar(Entity entity, int totalHealth) {
        this.entity = entity;
        this.totalHealth = totalHealth;
    }

    void draw(Graphics2D g2d, int x, int y) {
        g2d.setStroke(new BasicStroke(1));
        int width = (int) (entity.size * 1.4);
        int height = (int) (entity.size * 0.1);
        double ratio = (double) entity.health / totalHealth;
        g2d.setColor(new Color(83, 233, 21));
        g2d.fillRect((int) (entity.getCentroid().getX() - width / 2) + x,
                (int) (entity.getCentroid().getY() - entity.size * 1.3) + y, (int) (width * ratio), height);
        g2d.setColor(Color.red);
        g2d.fillRect((int) (entity.getCentroid().getX() - width / 2 + width * ratio) + x - 1,
                (int) (entity.getCentroid().getY() - entity.size * 1.3) + y, (int) (width * (1 - ratio)), height);
    }
}