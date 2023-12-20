import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

class HealthBar {
    private Entity entity;
    private int totalHealth;

    HealthBar(Entity entity) {
        this.entity = entity;
    }

    void draw(Graphics2D g2d, int x, int y) {
        g2d.setStroke(new BasicStroke(1));
        int width = (int) (entity.size * 1.8);
        int height = (int) (entity.size * 0.2);
        double ratio = (double) entity.health / totalHealth;
        g2d.setColor(new Color(83, 233, 21));
        g2d.fillRect((int) Math.round(entity.getCentroid().getX() - width / 2) + x,
                (int) Math.round(entity.getCentroid().getY() - entity.size * 1.6) + y, (int) Math.round(width * ratio), height);
        g2d.setColor(Color.red);
        g2d.fillRect((int) Math.round(entity.getCentroid().getX() - width / 2) + x + (int) Math.round(width * ratio),
                (int) Math.round(entity.getCentroid().getY() - entity.size * 1.6) + y, width - (int) Math.round(width * ratio), height);
    }

    void setTotalHealth(int totalHealth) {
        this.totalHealth = totalHealth;
    }
}