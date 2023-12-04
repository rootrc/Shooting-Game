import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import Geo.Line;
import Geo.Point;

class Weapon {

    int damage;
    int shots;
    int cooldown;
    int projectileWidth;
    int projectileLength;
    int projectileSpeed;

    Weapon(String name) {
        try {
            Scanner data = new Scanner(new FileReader("data/weapons/" + name + ".txt"));
            String type = data.nextLine();
            damage = Integer.parseInt(data.next());
            shots = Integer.parseInt(data.next());
            cooldown = Integer.parseInt(data.next()) * 100;
            projectileWidth = Integer.parseInt(data.next());
            projectileLength = Integer.parseInt(data.next());
            projectileSpeed = Integer.parseInt(data.next());
        } catch (IOException e) {
            System.out.println("Weapon Loading Error");
            System.exit(-1);
        }
    }

    void shoot(Point centroid, double direction) {
        Timer timer = new Timer();
        Point p1 = centroid.clone();
        Point p2 = centroid.translate(- projectileLength * Math.cos(direction), projectileLength * Math.sin(direction));
        Line line = new Line(p1, p2);
        line.setWidth(projectileWidth);
        TimerTask timertask = new TimerTask() {
            public void run() {
                p1.x -= projectileSpeed * Math.cos(direction);
                p1.y += projectileSpeed * Math.sin(direction);
                p2.x -= projectileSpeed * Math.cos(direction);
                p2.y += projectileSpeed * Math.sin(direction);
                if (Game.getInstance().room.intersects(line)) {
                    Game.getInstance().room.projectiles.remove(line);
                    timer.cancel();
                    timer.purge();
                }
            }
        };
        Game.getInstance().room.projectiles.add(line);
        timer.schedule(timertask, 0, Game.getInstance().delay);
    }
}
