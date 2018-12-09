package target;

import particles.Particle;
import vector.Vector2;

public interface Target {

    boolean reachedTarget(Particle p);

    Vector2 getDesiredPoint(Particle p);
}
