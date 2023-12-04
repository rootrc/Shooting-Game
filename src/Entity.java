class Entity extends Polygon {
    double direction;
    int health;
    Entity (Point[] points) {
        super(points);
    }
    void process() {
        for (Projectile projectile : Game.getInstance().room.projectiles) {
            if (this.intersects(projectile)) {
                health -= projectile.damage;
                if (health <= 0) {
                    Game.getInstance().room.entities.remove(this);
                }
                projectile.piercing--;
                if (projectile.piercing <= 0) {
                    Game.getInstance().room.projectiles.remove(projectile);
                }
            }
        }
    }
}
