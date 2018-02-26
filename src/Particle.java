import java.awt.*;

public class Particle {

	private int mass;
	private Point position;
	private float speed;
	private float minRadius;
	private float currentRadius;
	private float confortRadius;

	public Particle(int mass, Point position, float speed, float radius, float confortRadius){
		this.mass = mass;
		this.position = position;
		this.speed = speed;
		this.minRadius = radius;
		this.currentRadius = confortRadius;
		this.confortRadius = confortRadius;
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

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
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
}
