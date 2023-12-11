package Geo;
import java.awt.Color;
import java.awt.Graphics2D;

abstract class Geo<T extends Geo<T>> implements Cloneable {
    public Color color = Color.black;
    public int width = 1;

    protected void move(int x, int y) {
        moveX(x);
        moveY(y);
    }

    protected void move(double x, double y) {
        moveX(x);
        moveY(y);
    }

    protected void moveX(int x) {
        moveX((double) x);
    }

    protected void moveY(int y) {
        moveY((double) y);
    }

    public void directionMove(int distance, double direction) {
        directionMove((double) distance, direction);
    }

    public void directionMove(double distance, double direction) {
        move(-distance * Math.cos(direction), distance * Math.sin(direction));
    }

    public T translate(int x, int y) {
        return translate((double) x, (double) y);
    }

    public T translate(double x, double y) {
        T t = clone();
        t.move(x, y);
        return t;
    }

    public T directionTranslate(int distance, double direction) {
        return directionTranslate((double) distance, direction);
    }

    public T directionTranslate(double distance, double direction) {
        T t = clone();
        t.directionMove(distance, direction);
        return t;
    }

    protected Color getColor() {
        return color;
    }

    protected int getWidth() {
        return width;
    }

    public abstract T clone();

    public abstract void draw(Graphics2D g2d, int x, int y);

    protected abstract void moveX(double x);

    protected abstract void moveY(double x);

    public abstract void setBorderColor(Color color);

    public abstract void setWidth(int width);

}
