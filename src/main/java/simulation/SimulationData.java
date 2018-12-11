package simulation;

import particles.Particle;

import java.util.ArrayList;
import java.util.List;

public class SimulationData {
    List<List<State>> particlesData;
    double exitTime;
    double enterTime;

    public SimulationData(){
        particlesData = new ArrayList<>();
    }

    public void setExitTime(double exitTime){
        this.exitTime = exitTime;
    }

    public void setEnterTime(double enterTime) {
        this.enterTime = enterTime;
    }

    public void addState(List<Particle> particles, List<Particle> wallParticles, List<Particle> doorParticles) {
        List<State> s = new ArrayList<>();
        for (int i = 0; i < particles.size(); i++) {
            s.add(new State(particles.get(i)));
        }
        for (int i = 0; i < wallParticles.size(); i++) {
            s.add(new State(wallParticles.get(i)));
        }
        for (int i = 0; i < doorParticles.size(); i++) {
            s.add(new State(doorParticles.get(i)));
        }
        particlesData.add(s);
    }



}
