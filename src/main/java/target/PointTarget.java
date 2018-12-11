package target;

import particles.Particle;
import vector.Vector;
import vector.Vector2;

public class PointTarget implements Target {

    private Vector2 position;
    boolean doorTarget;

    public PointTarget(Vector2 position, boolean doorTarget){
        this.position = position;
        this.doorTarget = doorTarget;
    }


    @Override
    public boolean reachedTarget(Particle p) {
        return p.getPosition().dst2(position) <= p.getCurrentRadius() * p.getCurrentRadius();
    }

    @Override
    public Vector2 getDesiredPoint(Particle p) {
        return position;
    }

    @Override
    public boolean isDoorTarget() {
        return doorTarget;
    }

    @Override
    public Vector2 getCenter() {
        return position;
    }
}
