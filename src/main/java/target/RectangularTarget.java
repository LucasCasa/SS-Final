package target;

import particles.Particle;
import simulation.Utils;
import vector.Vector2;

public class RectangularTarget implements Target {
    float minX;
    float maxX;
    float minY;
    float maxY;
    Vector2 center;
    boolean doorTarget;

    public RectangularTarget(float minX, float maxX, float minY, float maxY, boolean doorTarget){
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.center = new Vector2(minX + maxX / 2, minY + maxY / 2);
        this.doorTarget = doorTarget;
    }

    public RectangularTarget(Vector2 center, float radiusX, float radiusY, boolean doorTarget){
        this.minX = center.x - radiusX;
        this.maxX = center.x + radiusX;
        this.minY = center.y - radiusY;
        this.maxY = center.y + radiusY;
        this.center = center;
        this.doorTarget = doorTarget;
    }


    @Override
    public boolean reachedTarget(Particle p) {
        Vector2 pos = p.getPosition();
        return Utils.getClosestPoint(pos, minX, maxX, minY, maxY).dst2(pos) <= p.getCurrentRadius() * p.getCurrentRadius();
    }

    @Override
    public Vector2 getDesiredPoint(Particle p) {
        return Utils.getClosestPoint(p.getPosition(), minX, maxX, minY, maxY);
    }

    @Override
    public boolean isDoorTarget() {
        return doorTarget;
    }

    @Override
    public Vector2 getCenter() {
        return center;
    }


}
