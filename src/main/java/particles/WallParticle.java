package particles;

import vector.Vector2;

import java.util.List;

public class WallParticle  extends Particle {

    public WallParticle(int id, Vector2 position, float speed, float minRadius, float confortRadius) {
        super(id, position, speed, minRadius, confortRadius, 1, 0, 0);
        isPerson = false;
    }


    @Override
    public void update(List<Particle> particles, List<Wall> w, float deltaTime) {

    }

    @Override
    public int applyVelocity(){
        return 0;
    }
}