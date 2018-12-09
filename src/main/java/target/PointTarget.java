package target;

import particles.Particle;
import vector.Vector;
import vector.Vector2;

public class PointTarget implements Target {

    private Vector2 position;

    public PointTarget(Vector2 position){
        this.position = position;
    }


    @Override
    public boolean reachedTarget(Particle p) {
        return p.getPosition().dst2(position) <= p.getCurrentRadius() * p.getCurrentRadius();
    }

    @Override
    public Vector2 getDesiredPoint(Particle p) {
        return position;
    }
}
