import java.awt.Color;
import java.awt.Graphics2D;

import Geo.Circle;

public class SpawnCircle extends Circle {
    private Room room;
    private Enemy<?> enemy;

    SpawnCircle(Enemy<?> enemy) {
        super(enemy.getCentroid(), 0);
        this.enemy = enemy;
        room = enemy.getRoom();
        setColor(new Color(238, 210, 2));
        room.spawnCircles.add(this);
    }

    protected void fill(Graphics2D g2d, int x, int y) {
        super.fill(g2d, x, y);
    }

    private int frame = 0;
    final private int multiplier = 50;

    void process() {
        frame++;
        r+= enemy.size / (multiplier * enemy.id);
        if (frame == multiplier * enemy.id) {
            room.entities.add(enemy);
            room.spawnCircles.remove(this);
        }
    }
}
