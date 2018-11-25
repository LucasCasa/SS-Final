import jdk.internal.util.xml.impl.Pair;
import vector.Vector;
import vector.Vector2;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

class Particle {

    private static final float TAU_SFM = 0.5f;
    private int id;
    private Vector2 position;
    private Vector2 velocity;
    private Vector2 nextVelocity;
    private Vector2 nextPosition;
    private Vector2 lastAcceleration = new Vector2(0, 0);
    private float speed = 0;
    int cellx = 0;
    int celly = 0;
    private Queue<Vector2> targets;

    private float drivingVelocity;
    private int mass;
    private float maxSpeed;
    private float minRadius;
    private float currentRadius;
    private float confortRadius;
    private final float BETA = 0.9f;
    private final float TAU = 0.5f; //Radius expansion constant
    private State nextState = new State();

    public Particle(int id, Vector2 position, float speed, float minRadius, float confortRadius, int mass, int drivingVelocity) {
        this.id = id;
        this.mass = mass;
        this.position = position;
        this.maxSpeed = speed;
        this.minRadius = minRadius;
        this.currentRadius = confortRadius;
        this.confortRadius = confortRadius;
        this.drivingVelocity = drivingVelocity;
        targets = new LinkedList<>();
        velocity = new Vector2(0,0);
        nextVelocity = new Vector2(0,0);
    }

    public void nextState(float deltaTime, List<Particle> neigh) {
        neigh = neigh.stream().filter(p -> p.getPosition().dst2(position) <= (currentRadius + p.getCurrentRadius()) * (currentRadius + p.getCurrentRadius())).collect(Collectors.toList());
        //System.out.println("Targeting ->" + targets.peek());
        if (neigh.isEmpty()) { //No external force
            if(targets.isEmpty()){
                return;
            }
            Vector2 direction = new Vector2(targets.peek().x - position.x, targets.peek().y - position.y).nor();
            nextState.position = position.cpy().add(direction.x * speed * deltaTime, direction.y * speed * deltaTime);
            nextState.speed = maxSpeed * (float) Math.pow((currentRadius - minRadius) / (confortRadius - minRadius), BETA);
            nextState.radius = (currentRadius < confortRadius) ? currentRadius + (confortRadius * deltaTime) / TAU : confortRadius;
        } else { //Escaping contact, min radius, max speed, normal direction from enemy
            nextState.radius = minRadius;
            nextState.speed = maxSpeed;
            Vector2 direction = getEscapeVector(neigh);
            nextState.position = position.cpy().add(direction.x * speed * deltaTime, direction.y * speed * deltaTime);
        }
        if(!targets.isEmpty() && targets.peek().dst2(position) < 0.05f) {
            targets.poll();
        }
    }

    public void applyNextState(){
        speed = nextState.speed;
        currentRadius = nextState.radius;
        position = nextState.position;
    }

    private Vector2 getEscapeVector(List<Particle> neigh) {
        Vector2 dir = new Vector2(0, 0);
        for (Particle neighbour : neigh) {
            dir.add(position.x - neighbour.position.x, position.y - neighbour.position.y);
        }
        return dir.nor();
    }

	public Vector2 getGranularForce(Particle p) {
		Vector2 direction = new Vector2(position.x - p.position.x, position.y - p.position.y);
		float epsilon = direction.len() - p.getCurrentRadius() - getCurrentRadius();
		if(epsilon > 0){
			return new Vector2(0,0);
		}
        direction.nor();

        //Componente tangencial de la Fuerza
		Vector2 tang = new Vector2(-direction.y, direction.x);
		float tangVel = tang.dot(velocity) - tang.dot(p.velocity);
        tang.scl(tangVel*epsilon*120000);

		//Componente normal de la Fuerza
		direction.scl(-epsilon*(240000)); //Kn = 1.2*10^5 N/m

		return direction.add(tang);
	}

	public Vector2 getSocialForce(Particle p) {
		Vector2 direction = new Vector2(position.x - p.position.x, position.y - p.position.y);
		double epsilon = direction.len() - p.getCurrentRadius() - getCurrentRadius();

		if(epsilon > 1) { // if > 0 then force < 0.007, we can avoid doing calculations.
			return new Vector2(0,0);
		}
		return direction.nor().scl((float) (2000*Math.exp(-epsilon/0.08))); //A = 2000 N; B = 0.08 m
	}

    public Vector2 getDrivingForce() {
		Vector2 direction = new Vector2(targets.peek().x - position.x , targets.peek().y - position.y);
		return direction.nor().scl(drivingVelocity).sub(velocity).scl(mass / TAU_SFM); //Velocidad deseada varia entre 0.8 y 6 m/s;
	}

	public void nextStateSFM(List<Particle> particles, float deltaTime) {
        Vector2 totalForce = new Vector2(0, 0);

        for (Particle p : particles) {
            if(p.id != id) {
                totalForce.add(getGranularForce(p));
                totalForce.add(getSocialForce(p));
            }
        }
        totalForce.add(getDrivingForce()).scl(1f/mass);
        updatePosition(totalForce, deltaTime);
        updateVelocity(totalForce, deltaTime);
//        nextVelocity = velocity.cpy().add(totalForce);
//        nextPosition = position.cpy().add(nextVelocity.cpy().scl(deltaTime)).add(totalForce.scl(deltaTime/2));
        //Beeman
        lastAcceleration = totalForce;
	}

	public void applyVelocity(float deltaTime) {
        velocity = nextVelocity;
        position = nextPosition;
    }

    private void updatePosition(Vector2 acceleration, float dt) {
        float rx = position.x + velocity.x * dt + 2.0f/3 * acceleration.x * dt * dt - 1.0f / 6 * lastAcceleration.x * dt * dt;
        float ry = position.y + velocity.y * dt + 2.0f/3 * acceleration.y * dt * dt - 1.0f / 6 * lastAcceleration.y * dt * dt;
        nextPosition = new Vector2(rx, ry);
    }

    private void updateVelocity(Vector2 acceleration, float dt) {
        float vx = velocity.x + 2.0f/3 * acceleration.x * dt - 1.0f / 6 * lastAcceleration.x * dt;
        float vy = velocity.y + 2.0f/3 * acceleration.y * dt - 1.0f / 6 * lastAcceleration.y * dt;
        nextVelocity = new Vector2(vx, vy);
    }

    public String toString() {
        return position.x + " " + position.y + " " + currentRadius + " " + id + "\n";
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getCurrentRadius() {
        return currentRadius;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public static double dist2(Particle p1, Particle p2) {
        return (p1.position.x - p2.position.x) * (p1.position.x - p2.position.x) +
                (p1.position.y - p2.position.y) * (p1.position.y - p2.position.y);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void addTarget(Vector2 target) {
        targets.add(target);
    }

    private class State {
        float speed;
        float radius;
        Vector2 position;

        public State(){

        }

        public State(Vector2 position, float speed, float radius){
            this.position = position;
            this.speed = speed;
            this.radius = radius;
        }

    }

}
