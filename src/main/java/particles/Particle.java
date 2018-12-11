package particles;

import target.Target;
import vector.Vector;
import vector.Vector2;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class Particle {

    private static final float TAU_SFM = 0.5f;
    private int id;
    protected Vector2 position;
    private Vector2 velocity;
    private Vector2 nextVelocity;
    private Vector2 nextPosition;
    private Vector2 acceleration;
    private Vector2 lastAcceleration = new Vector2(0, 0);
    private Vector2 savedVelocity; //Save the velocity to use in the correction stage
    private float speed = 0;
    int cellx = 0;
    int celly = 0;
    private LinkedList<Target> targets;

    private float drivingVelocity;
    private int mass;
    private float maxSpeed;
    private float minRadius;
    private float currentRadius;
    private float confortRadius;
    private final float BETA = 0.9f;
    private final float TAU = 0.5f; //Radius expansion constant
    boolean isPerson;

    private Target savedDoorTarget;

    public Particle(int id, Vector2 position, float speed, float minRadius, float confortRadius, int mass, float drivingVelocity) {
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
        isPerson = true;
    }

	private Vector2 calculateGF(Vector2 normal, float epsilon, Vector2 otherVel) {
        if(epsilon < 0){
            return Vector2.Zero.cpy();
        }

        //Componente tangencial de la Fuerza
        Vector2 tang = new Vector2(-normal.y, normal.x);
        float tangVel = tang.dot(otherVel) - tang.dot(velocity);
        tang.scl(tangVel*epsilon*240000);

        //Componente normal de la Fuerza
        normal.scl(epsilon*(120000)); //Kn = 1.2*10^5 N/m

        return normal.add(tang);
    }

	private Vector2 calculateSF(Vector2 direction, float epsilon){
        if(epsilon < -1) { // if > 0 then force < 0.007, we can avoid doing calculations.
            return Vector2.Zero.cpy();
        }
        return direction.nor().scl((float) (2000*Math.exp(epsilon/0.08))); //A = 2000 N; B = 0.08 m
    }

    private Vector2 getDrivingForce() {
        if(targets.isEmpty()){
            return Vector2.Zero.cpy();
        }
        Vector2 desired = targets.peek().getDesiredPoint(this);
		Vector2 direction = new Vector2(desired.x - position.x , desired.y - position.y);
		return direction.nor().scl(drivingVelocity).sub(velocity).scl(mass / TAU_SFM); //Velocidad deseada varia entre 0.8 y 6 m/s;
	}

    private Vector2 getForceAgainstWall(Wall wall) {
        Vector2 closestPoint = wall.getClosestPoint(this);
        Vector2 direction = new Vector2(position.x - closestPoint.x, position.y - closestPoint.y);
        float epsilon = getCurrentRadius() - direction.len();
        Vector2 normal = direction.nor();
        return calculateGF(normal.cpy(), epsilon, Vector2.Zero).add(calculateSF(normal, epsilon));
    }

    private Vector2 getForceAgainstParticle(Particle p) {
        Vector2 direction = new Vector2(position.x - p.position.x, position.y - p.position.y);
        float epsilon = p.getCurrentRadius() + getCurrentRadius() - direction.len();
        Vector2 normal = direction.nor();
        return calculateGF(normal.cpy(), epsilon, p.velocity).add(calculateSF(normal.cpy(), epsilon));
    }

    //Method to call from the outside
	public void update(List<Particle> particles, List<Wall> walls, float deltaTime){
        Vector2 acceleration = calculateAcceleration(particles, walls, deltaTime);
        updatePosition(acceleration, deltaTime);
        updateVelocity(acceleration, deltaTime);
        this.acceleration = acceleration;
        //Beeman
    }

    private Vector2 calculateAcceleration(List<Particle> particles, List<Wall> walls, float deltaTime) {
        Vector2 totalForce = new Vector2(0, 0);

        for (Particle p : particles) {
            if(p.id != id) {
                totalForce.add(getForceAgainstParticle(p));
            }
        }

        for(Wall wall: walls) {
            totalForce.add(getForceAgainstWall(wall));
        }
        //Convert total force to acceleration
        totalForce.add(getDrivingForce()).scl(1f/mass);
        return totalForce;
    }

	public int applyVelocity() {
        savedVelocity = velocity;
        velocity = nextVelocity;
        position = nextPosition;
        if(savedDoorTarget != null &&
                targets.peek() != savedDoorTarget &&
                !savedDoorTarget.reachedTarget(this) &&
                savedDoorTarget.getCenter().dst2(targets.peek().getCenter()) + 0.5f <= getPosition().dst2(targets.peek().getCenter())){
            targets.addFirst(savedDoorTarget);
            System.out.println("Added back");
            return -1;
        }
        if(targets.size() > 1 && targets.peek().reachedTarget(this)){
            Target t = targets.poll();
            if(t.isDoorTarget()){
                savedDoorTarget = t;
                return 1;
            }
        }
        return 0;
    }

    public void correctVelocity(List<Particle> particles, List<Wall> walls, float dt) {
        Vector2 nextAccel = calculateAcceleration(particles, walls, dt);
        float vx = savedVelocity.x + 0.33333f * nextAccel.x * dt + 0.83333f * acceleration.x - 0.166666f * lastAcceleration.x * dt;
        float vy = savedVelocity.y + 0.33333f * nextAccel.y * dt + 0.83333f * acceleration.y - 0.166666f * lastAcceleration.y * dt;

        lastAcceleration = acceleration;
    }

    private void updatePosition(Vector2 acceleration, float dt) {
        float rx = position.x + velocity.x * dt + 0.66666f * acceleration.x * dt * dt - 0.166666f * lastAcceleration.x * dt * dt;
        float ry = position.y + velocity.y * dt + 0.66666f * acceleration.y * dt * dt - 0.166666f * lastAcceleration.y * dt * dt;
        nextPosition = new Vector2(rx, ry);
    }

    private void updateVelocity(Vector2 acceleration, float dt) {
        float vx = velocity.x + 1.5f * acceleration.x * dt - 0.5f * lastAcceleration.x * dt;
        float vy = velocity.y + 1.5f * acceleration.y * dt - 0.5f * lastAcceleration.y * dt;
        nextVelocity = new Vector2(vx, vy);
    }

    public String toString() {
        return position.x + " " + position.y + " " + currentRadius + " " + targets.size() + "\n";
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

    public void addTarget(Target target) {
        targets.add(target);
    }

    @Override
    public int hashCode(){
        return id;
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof Particle)){
            return false;
        }
        return ((Particle) o).id == id;
    }
}
