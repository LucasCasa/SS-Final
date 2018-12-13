package simulation;

import particles.Particle;
import vector.Vector2;

public class State {
    float x;
    float y;
    float radius;
    int remainingTargets;
    int top;

    public State(Particle p){
        this.x = p.getPosition().x;
        this.y = p.getPosition().y;
        this.radius = p.getCurrentRadius();
        this.remainingTargets = p.targets.size();
        this.top = p.top;
    }

    @Override
    public String toString() {
        return x + " " + y + " " + radius + " " + remainingTargets + " " + top + "\n";
    }
}
