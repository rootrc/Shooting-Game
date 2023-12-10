import java.awt.Color;
import java.awt.Graphics2D;

class Polygon extends Geo<Polygon> {
    int length;
    Point[] points;
    Line[] lines;
    Point centroid = new Point(0, 0);

    Polygon(Point[] points) {
        if (points == null) {
            return;
        }
        this.length = points.length;
        this.points = points.clone();
        lines = new Line[length];
        for (int i = 0; i < length; i++) {
            lines[i] = new Line(points[i], points[(i + 1) % length]);
        }
        computeCentroid();
    }

    public Polygon clone() {
        Point[] points = new Point[length];
        for (int i = 0; i < length; i++) {
            points[i] = this.points[i].clone();
        }
        Polygon polygon = new Polygon(points);
        polygon.setColor(color);
        return polygon;
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
            xPoints[i] = (int) Math.round(points[i].getX() + Game.getInstance().panel.xAdjust);
            yPoints[i] = (int) Math.round(points[i].getY() + Game.getInstance().panel.yAdjust);
        }
        g2d.setColor(color);
        g2d.fillPolygon(xPoints, yPoints, length);
    }

    void moveX(double x) {
        for (Point point : points) {
            point.moveX(x);
        }
        centroid.moveX(x);
    }

    void moveY(double y) {
        for (Point point : points) {
            point.moveY(y);
        }
        centroid.moveY(y);
    }

    void setBorderColor(Color color) {
        for (Line line : lines) {
            line.setBorderColor(color);
        }
    }

    void setColor(Color color) {
        this.color = color;
    }

    void setWidth(int width) {
        for (Line line : lines) {
            line.setWidth(width);
        }
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

    void rotate(double radian, Point pivot) {
        for (Point point : points) {
            point.rotate(radian, pivot);
        }
    }

    void rotate(double radian) {
        rotate(radian, centroid);
    }

    Polygon getRotation(double radian) {
        Polygon polygon = clone();
        polygon.rotate(radian);
        return new Polygon(points);
    }

    Point computeCentroid() {
        double signedArea = 0.0;
        double a = 0.0;
        for (Line line : lines) {
            a = line.getP1().getX() * line.getP2().getY() - line.getP2().getX() * line.getP1().getY();
            signedArea += a;
            centroid.moveX((line.getP1().getX() + line.getP2().getX()) * a);
            centroid.moveY((line.getP1().getY() + line.getP2().getY()) * a);
        }
        signedArea /= 2;
        centroid.setX(centroid.getX() / (6.0 * signedArea));
        centroid.setY(centroid.getY() / (6.0 * signedArea));
        return centroid;
    }
}
