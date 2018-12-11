package target;

import particles.Particle;
import vector.Vector2;

public class CircleTarget implements Target {
    private Vector2 center;
    private float radius;
    boolean doorTarget;

    public CircleTarget(Vector2 center, float radius, boolean doorTarget){
        this.center = center;
        this.radius = radius;
        this.doorTarget = doorTarget;
    }

    @Override
    public boolean reachedTarget(Particle p) {
        return p.getPosition().dst2(center) <= (radius + p.getCurrentRadius()) * (radius + p.getCurrentRadius());
    }

    @Override
    public Vector2 getDesiredPoint(Particle p) {
        return center;
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
