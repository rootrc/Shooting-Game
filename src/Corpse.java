import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Timer;
import java.util.TimerTask;

import Geo.Polygon;

class Corpse extends Polygon {
    private Room room;
    private Timer timer = new Timer();
    private int time;

    Corpse(Entity entity, int time) {
        super(entity.getPoints());
        this.room = entity.getRoom();
        this.time = time;
        process();
    }

    protected void draw(Graphics2D g2d, int x, int y) {
        super.draw(g2d, x, y);
    }

    protected void fill(Graphics2D g2d, int x, int y) {
        super.fill(g2d, x, y);
    }

    private void process() {
        setColor(new Color(139, 0, 0));
        TimerTask timertask = new TimerTask() {
            int count = 0;

            public void run() {
                count++;
                setColor(new Color(0, 0, 0, -255 * count
                        * count / time / time + 255));
                setBorderColor(new Color(0, 0, 0, -255 * count
                        * count / time / time + 255));
                if (count == time) {
                    room.corpses.remove(Corpse.this);
                    timer.cancel();
                    timer.purge();
                }
            }
        };
        room.corpses.add(this);
        timer.schedule(timertask, 30 * Game.delay, Game.delay);
    }
}