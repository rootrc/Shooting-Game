

import java.awt.Color;
import java.awt.Graphics2D;

class Polygon {
    int length;
    Point[] points;
    Line[] lines;
    Point centroid;
    Color color = Color.black;

    Polygon(Point[] points) {
        this.length = points.length;
        this.points = points.clone();
        lines = new Line[length];
        for (int i = 0; i < length; i++) {
            lines[i] = new Line(points[i], points[(i + 1) % length]);
        }
        centroid = new Point(0, 0);
        computeCentroid();
    }

    void setBorderColor(Color color) {
        for (Line line : lines) {
            line.color = color;
        }
        for (Point point : points) {
            point.color = color;
        }
    }
    void setColor(Color color) {
        this.color = color;
    }

    void setWidth(int width) {
        for (Line line : lines) {
            line.width = width;
        }
        for (Point point : points) {
            point.width = width;
        }
    }

    void draw(Graphics2D g2d) {
        for (int i = 0; i < length; i++) {
            lines[i].draw(g2d);
        }
    }

    void fill(Graphics2D g2d) {
        int[] xPoints = new int[length];
        int[] yPoints = new int[length];
        for (int i = 0; i < length; i++) {
            xPoints[i] = (int) Math.round(points[i].x);
            yPoints[i] = (int) Math.round(points[i].y);
        }
        g2d.setColor(color);
        g2d.fillPolygon(xPoints, yPoints, length);
    }

    public Polygon clone() {
        Point[] points = new Point[length];
        for (int i = 0; i < length; i++) {
            points[i] = this.points[i].clone();
        }
        Polygon polygon = new Polygon(points);
        polygon.color = color; 
        return polygon;
    }

    Polygon translate(int x, int y) {
        Point[] points = new Point[length];
        for (int i = 0; i < length; i++) {
            points[i] = this.points[i].translate(x, y);
        }
        Polygon polygon = new Polygon(points);
        polygon.color = color; 
        return polygon;
    }

    Polygon translate(double x, double y) {
        Point[] points = new Point[length];
        for (int i = 0; i < length; i++) {
            points[i] = this.points[i].translate(x, y);
        }
        centroid.x += x;
        centroid.y += y;
        return new Polygon(points);
    }

    void move(int x, int y) {
        moveX(x);
        moveY(y);
    }

    void move(double x, double y) {
        moveX(x);
        moveY(y);
    }

    void moveX(int x) {
        for (Point point : points) {
            point.moveX(x);
        }
        centroid.x += x;
    }

    void moveX(double x) {
        for (Point point : points) {
            point.moveX(x);
        }
        centroid.x += x;
    }

    void moveY(int y) {
        for (Point point : points) {
            point.moveY(y);
        }
        centroid.y += y;
    }

    void moveY(double y) {
        for (Point point : points) {
            point.moveY(y);
        }
        centroid.y += y;
    }

    private Point computeCentroid() {
        double signedArea = 0.0;
        double a = 0.0;
        for (Line line : lines) {
            a = line.p1.x * line.p2.y - line.p2.x * line.p1.y;
            signedArea += a;
            centroid.x += (line.p1.x + line.p2.x) * a;
            centroid.y += (line.p1.y + line.p2.y) * a;
        }
        signedArea /= 2;
        centroid.x /= 6.0 * signedArea;
        centroid.y /= 6.0 * signedArea;
        return centroid;
    }

    boolean intersects(Line line) {
        for (Line line2 : lines) {
            if (line2.intersects(line)) {
                return true;
            }
        }
        return false;
    }

    boolean intersects(Polygon polygon) {
        for (Line line1 : lines) {
            for (Line line2 : polygon.lines) {
                if (line1.intersects(line2)) {
                    return true;
                }
            }
        }
        return false;
    }

    void rotate(double radian) {
        for (Point point : points) {
            point.rotate(radian, centroid);
        }
    }

    Polygon getRotation(double radian) {
        Point[] points = new Point[length];
        for (int i = 0; i < length; i++) {
            points[i] = this.points[i].getRotation(radian, centroid);
        }
        return new Polygon(points);
    }
}
