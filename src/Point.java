
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

class Point extends Geo<Point> {
    private double x;
    private double y;

    Point(int x, int y) {
        this((double) x, (double) y);
    }

    Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    Point() {

    }

    void set(int x, int y) {
        set((double) x, (double) y);
    }

    void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point clone() {
        Point point = new Point(x, y);
        point.setBorderColor(color);
        point.setWidth(width);
        return point;
    }

    void draw(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(width));
        g2d.drawLine((int) Math.round(this.x) + Game.getInstance().room.xAdjust,
                (int) Math.round(this.y) + Game.getInstance().room.yAdjust,
                (int) Math.round(this.x) + Game.getInstance().room.xAdjust,
                (int) Math.round(this.y) + Game.getInstance().room.yAdjust);
    }

    double getX() {
        return x;
    }

    double getY() {
        return y;
    }

    void setX(double x) {
        this.x = x;
    }

    void setY(double y) {
        this.y = y;
    }

    void moveX(double x) {
        this.x += x;
    }

    void moveY(double y) {
        this.y += y;
    }

    void setBorderColor(Color color) {
        this.color = color;
    }

    void setWidth(int width) {
        this.width = width;
    }

    void rotate(double radian, Point pivot) {
        double s = Math.sin(radian);
        double c = Math.cos(radian);
        x -= pivot.x;
        y -= pivot.y;
        double xnew = x * c - y * s;
        double ynew = x * s + y * c;
        x = xnew + pivot.x;
        y = ynew + pivot.y;
    }

    Point getRotation(double radian, Point pivot) {
        Point point = clone();
        point.rotate(radian, pivot);
        return point;
    }
}
