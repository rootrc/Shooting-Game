import java.awt.Color;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

class Enemy extends Entity {
    int id;
    boolean shoot, shooting;
    int moveSpeed;
    double rotationSpeed;

    Weapon weapon;

    Enemy(Point[] points) {
        super(points);
    }
    //for inheritance
    Enemy translate(double x, double y) {
        return this;
    }

}

class Chaser extends Enemy {
    Chaser(Point[] points, int id) {
        super(points);
        this.id = id;
        color = Color.yellow;
        corpseLength = 100;
        try {
            Scanner data = new Scanner(new FileReader("data/enemies/chaser" + id + ".txt"));
            int M = Integer.parseInt(data.next());
            for (int i = 0; i <= M; i++) {
                data.nextLine();
            }
            health = Integer.parseInt(data.next());
            moveSpeed = Integer.parseInt(data.next());
            rotationSpeed = Math.PI / Integer.parseInt(data.next());
        } catch (IOException e) {
            System.out.println("Enemy Loading Error");
            System.exit(-1);
        }
    }

    Chaser translate(double x, double y) {
        Point[] points = new Point[length];
        for (int i = 0; i < length; i++) {
            points[i] = this.points[i].translate(x, y);
        }
        Chaser chaser = new Chaser(points, id);
        return chaser;
    }

    void process() {
        super.process();
        Point playerCentroid = Game.getInstance().room.player.centroid;
        double radian = new Line(centroid, playerCentroid).caculateRadian();
        rotate(direction - radian);
        direction = radian;
        move(- moveSpeed * Math.cos(direction), moveSpeed * Math.sin(direction));
    }
}
