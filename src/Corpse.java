import java.awt.Color;
import java.awt.Graphics2D;

import Geo.Polygon;

class Corpse extends Polygon {
    private Room room;
    private int time;

    Corpse(Entity entity, int time) {
        super(entity.getPoints());
        this.room = entity.getRoom();
        this.time = time;
        setColor(new Color(139, 0, 0));
        room.corpses.add(this);
    }

    protected void draw(Graphics2D g2d, int x, int y) {
        super.draw(g2d, x, y);
    }

    protected void fill(Graphics2D g2d, int x, int y) {
        super.fill(g2d, x, y);
    }

    private int frame = -10;

    public void process() {
        frame++;
        if (frame >= 0) {
            setColor(new Color(0, 0, 0, -255 * frame
                    * frame / time / time + 255));
            setBorderColor(new Color(0, 0, 0, -255 * frame
                    * frame / time / time + 255));
        }
        if (frame == time) {
            room.corpses.remove(this);
        }
    }
}