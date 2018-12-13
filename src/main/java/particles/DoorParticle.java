package particles;

import vector.Vector2;

import java.util.List;

public class DoorParticle extends Particle {

    float distanceMoved = 0;
    float distanceToMove;
    int orientation;
    float delay = 1;
    float elapsed = 0;
    float speed;

    public DoorParticle(int id, Vector2 position, float speed, float minRadius, float confortRadius, int mass, float distanceToMove) {
        super(id, position, speed, minRadius, confortRadius, mass, 0, 0);
        if(distanceToMove < 0) {
            orientation = -1;
            this.distanceToMove = -distanceToMove;
        } else {
            orientation = 1;
            this.distanceToMove = distanceToMove;
        }
        this.speed = speed;
        super.top = 3;
    }

    @Override
    public void update(List<Particle> particleList, List<Wall> w, float deltaTime){
        elapsed+= deltaTime;
        if(elapsed > delay) {
            if (distanceMoved < distanceToMove) {
                float delta = orientation * deltaTime * speed;
                position.add(delta, 0);
                distanceMoved += Math.abs(delta);
            }
        }
    }
    @Override
    public int applyVelocity() {
        return 0;
    }
}