import java.awt.Color;
import java.awt.Graphics2D;

public abstract class Geo<T extends Geo<T>> implements Cloneable {
    protected Color color = Color.black;
    protected int width = 1;

    void move(int x, int y) {
        moveX(x);
        moveY(y);
    }

    void move(double x, double y) {
        moveX(x);
        moveY(y);
    }

    void moveX(int x) {
        moveX((double) x);
    }

    void moveY(int y) {
        moveY((double) y);
    }

    void directionMove(int distance, double direction) {
        directionMove((double) distance, direction);
    }

    void directionMove(double distance, double direction) {
        move(-distance * Math.cos(direction), distance * Math.sin(direction));
    }

    T translate(int x, int y) {
        return translate((double) x, (double) y);
    }

    T translate(double x, double y) {
        T t = clone();
        t.move(x, y);
        return t;
    }

    T directionTranslate(int distance, double direction) {
        return directionTranslate((double) distance, direction);
    }

    T directionTranslate(double distance, double direction) {
        T t = clone();
        t.directionMove(distance, direction);
        return t;
    }

    Color getColor() {
        return color;
    }

    int getWidth() {
        return width;
    }

    abstract public T clone();

    abstract void draw(Graphics2D g2d);

    abstract void moveX(double x);

    abstract void moveY(double x);

    abstract void setBorderColor(Color color);

    abstract void setWidth(int width);

}
