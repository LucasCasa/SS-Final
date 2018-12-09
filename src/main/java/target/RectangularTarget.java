package target;

import particles.Particle;
import simulation.Utils;
import vector.Vector2;

public class RectangularTarget implements Target {
    float minX;
    float maxX;
    float minY;
    float maxY;

    public RectangularTarget(float minX, float maxX, float minY, float maxY){
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    public RectangularTarget(Vector2 center, float radiusX, float radiusY){
        this.minX = center.x - radiusX;
        this.maxX = center.x + radiusX;
        this.minY = center.y - radiusY;
        this.maxY = center.y + radiusY;
    }


    @Override
    public boolean reachedTarget(Particle p) {
        Vector2 pos = p.getPosition();
        return pos.x >= minX && pos.x <= maxX && pos.y >= minY && pos.y <= maxY;
    }

    @Override
    public Vector2 getDesiredPoint(Particle p) {
        return Utils.getClosestPoint(p.getPosition(), minX, maxX, minY, maxY);
    }
}
