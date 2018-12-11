package simulation;

import particles.Particle;
import vector.Vector2;

public class State {
    float x;
    float y;
    float radius;

    public State(Particle p){
        this.x = p.getPosition().x;
        this.y = p.getPosition().y;
        this.radius = p.getCurrentRadius();
    }

    @Override
    public String toString() {
        return x + " " + y + " " + radius + "\n";
    }
}
