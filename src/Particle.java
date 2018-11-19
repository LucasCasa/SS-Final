import jdk.internal.util.xml.impl.Pair;
import vector.Vector2;

import java.util.List;

public class Particle {

	private int id;
	private int mass;
	private Vector2 position;
	//private Vector2 direction;
	private float speed = 0;
	private float minRadius;
	private float currentRadius;
	private float confortRadius;
	int cellx = 0;
	int celly = 0;
	private float maxSpeed;
	private final float TAU = 0.5f; //Radius expansion constant
	private final float BETA = 0.9f;
	private Vector2 target;

	public Particle(int id, int mass, Vector2 position, float speed, float minRadius, float confortRadius){
		this.id = id;
		this.mass = mass;
		this.position = position;
		this.maxSpeed = speed;
		this.minRadius = minRadius;
		this.currentRadius = confortRadius;
		this.confortRadius = confortRadius;
	}

	public void update(float deltaTime, List<Particle> neigh){
		if(id <= 100) {
			return;
		}
		if(neigh.isEmpty()) { //No external force
			Vector2 direction = new Vector2(target.x - position.x, target.y - position.y).nor();
			position.add(direction.x * speed * deltaTime, direction.y * speed * deltaTime);
			speed = maxSpeed * (float) Math.pow((currentRadius - minRadius) / (confortRadius - minRadius), BETA);
			currentRadius = (currentRadius < confortRadius)? currentRadius + (confortRadius * deltaTime) / TAU : confortRadius;
		} else {
			currentRadius = minRadius;
			speed = maxSpeed;
			Vector2 direction = getEscapeVector(neigh);
			position.add(direction.x * speed * deltaTime, direction.y * speed * deltaTime);
		}
	}

	private Vector2 getEscapeVector(List<Particle> neigh) {
		Vector2 dir = new Vector2(0 ,0);
		for(Particle neighbour : neigh) {
			dir.add(position.x - neighbour.position.x, position.y - neighbour.position.y);
		}
		return dir.nor();
	}

	public Vector2[] getGranularForce(Particle p) {
		Vector2 direction = new Vector2(position.x - p.position.x, position.y - p.position.y);
		double epsilon = direction.len() - p.getCurrentRadius() + getCurrentRadius();
		if(epsilon < 0){
			return new Vector2[]{new Vector2(0,0),new Vector2(0,0)};
		}
		direction.nor();

		//Componente normal de la Fuerza
		direction.scl((float) (-epsilon*(1.2*100000))); //Kn = 1.2*10^5 N/m

		//Componente tangencial de la Fuerza
		//Vector2 relativeVelocity = vectorVelocidad - p.velocity
		//Vector2 tangencial = new Vector2(-direction.y, direction.x)
		//double velTangencial = relativeVelocity.x*tangencial.x + relativeVelocity.y*tangencial.y;
		//tangencial.scl((float)(-(2.4*100000)*epsilon*velTangencial)) // Kt = 2.4*10^5 kg/(m/s)
		Vector2 tang = new Vector2(0,0);

		return new Vector2[]{direction, tang}; //direction seria la direccion normal. tang habria que modificarlo solucionando lo que esta comentado.
	}

	public Vector2 getSocialForce(Particle p) {
		Vector2 direction = new Vector2(position.x - p.position.x, position.y - p.position.y);
		double epsilon = direction.len() - p.getCurrentRadius() - getCurrentRadius();

		if(epsilon > 1) {
			return new Vector2(0,0);
		}

		direction.nor();
		Vector2 socialForce = direction.cpy();
		socialForce.scl((float) (2000*Math.exp(-epsilon/0.08))); //A = 2000 N; B = 0.08 m

		return socialForce;
	}

	public Vector2 getDrivingForce() {

		//HABRIA QUE HACER ALGUN CHEQUEO ANTES CON RESPECTO A LA POSICION ?

		Vector2 direction = new Vector2(position.x - target.x, position.y - target.y);
		direction.nor();
		Vector2 aux = direction.cpy();
		aux.scl((float) 0.8); //Velocidad deseada varia entre 0.8 y 6 m/s;

		Vector2 drivingForce =  new Vector2(aux.x - speed, aux.y - speed); //en vez de speed tiene que ir el vector velocidad
		drivingForce.scl(mass/TAU);

		return drivingForce;
	}

	public Vector2[] getForce(Particle p) {
		Vector2[] granularForce = getGranularForce(p);
		Vector2 socialForce = getSocialForce(p);
		Vector2 normal = new Vector2(granularForce[0].x + socialForce.x, granularForce[0].y + socialForce.y);

		return new Vector2[]{normal, granularForce[1]};
	}

	public String toString() {
		return position.x + " "+ position.y + " " + currentRadius + "\n";
	}

	public int getMass() {
		return mass;
	}

	public void setMass(int mass) {
		this.mass = mass;
	}

	public Vector2 getPosition() {
		return position;
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

	public float getMinRadius() {
		return minRadius;
	}

	public void setMinRadius(float radius) {
		this.minRadius = radius;
	}

	public float getConfortRadius() {
		return confortRadius;
	}

	public void setConfortRadius(float confortRadius) {
		this.confortRadius = confortRadius;
	}

	public float getCurrentRadius() {
		return currentRadius;
	}

	public void setCurrentRadius(float currentRadius) {
		this.currentRadius = currentRadius;
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

	public void setTarget(Vector2 target) {
		this.target = target;
	}
}
