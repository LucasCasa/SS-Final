package simulation;

import particles.Door;
import particles.DoorParticle;
import particles.Particle;
import particles.Wall;
import target.PointTarget;
import target.Target;
import vector.Vector2;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utils {

    private static float MIN_RADIUS = 0.3f;

    private Utils() {

	}


	public static List<Wall> placeBoxWithOpening(List<Particle> particleList, float minX, float maxX, float minY, float maxY, float openCenter, float openRadius ) {
	    float openStart = openCenter - openRadius;
	    float openEnd = openCenter + openRadius;
	    float radius = 0.05f;
	    float step = 0.05f;
	    int count = 0;

	    List<Wall> walls = new ArrayList<>();
        count = drawLine(new Vector2(minX,minY), new Vector2(maxX,minY), radius, step, particleList, count); //  0,0 -> x,0
		walls.add(new Wall(minX, maxX, minY, minY));
        count = drawLine(new Vector2(minX,minY), new Vector2(minX,maxY), radius, step, particleList, count); // 0,0 -> 0, 10
		walls.add(new Wall(minX, minX, minY, maxY));
        count = drawLine(new Vector2(maxX,minY), new Vector2(maxX,maxY), radius, step, particleList, count); // x, 0 -> x, y
		walls.add(new Wall(maxX, maxX, minY, maxY));
        count = drawLine(new Vector2(minX,maxY), new Vector2(openStart,maxY), radius, step, particleList, count); // Open left
		walls.add(new Wall(minX, openStart, maxY, maxY));
		count = drawDoor(new Vector2(openStart,maxY), new Vector2(openStart + (openEnd - openStart) / 2,maxY), radius, step, particleList, count, -1, 2); // Open left
		walls.add(new Door(openStart, openStart + (openEnd - openStart) / 2,maxY, maxY, -1, 1, 2));
		count = drawDoor(new Vector2(openStart + (openEnd - openStart) / 2,maxY), new Vector2(openEnd,maxY), radius, step, particleList, count, 1, 2); // Open left
		walls.add(new Door(openStart + (openEnd - openStart) / 2, openEnd,maxY, maxY, 1, 1, 2));
		count = drawLine(new Vector2(openEnd,maxY), new Vector2(maxX,maxY), radius, step, particleList, count); // Open right
		walls.add(new Wall(openEnd, maxX, maxY, maxY));
		return walls;
	}

	public static List<Particle> placePeopleRandomly(int start, Vector2 center, float spawnRadius, int orientation, float drivingForce, List<Target> targets, boolean firstTargetSelf) {
        return placePeopleNotBlockingExit(start, center, spawnRadius, 0, orientation, drivingForce, targets, firstTargetSelf);
    }

    public static List<Particle> placePeopleNotBlockingExit(int start, Vector2 center, float spawnRadius, float doorRadius, int orientation, float drivingForce, List<Target> targets, boolean firstTargetSelf) {
        List<Particle> particles = new ArrayList<>();
	    Random r = new Random();
        for (int i = 0; i < 10; i++) {
            boolean validPos = false;
            Vector2 position = null;
            float radius = MIN_RADIUS + r.nextFloat() / 10;
            while (!validPos){
                validPos = true;
                position = new Vector2(r.nextFloat() * spawnRadius * 2 + center.x - spawnRadius ,r.nextFloat()*spawnRadius*orientation + orientation*radius + center.y);
                for (Particle particle : particles) {
                    if(position.dst(particle.getPosition()) <= particle.getCurrentRadius() + radius){
                        validPos = false;
                    }
                }
                if(doorRadius > 0 && position.x >= center.x - doorRadius - radius && position.x <= center.x + doorRadius + radius){
                    validPos = false;
                }
            }
            Particle p = new Particle(start + i, position, 1.95f, radius, radius, 80, drivingForce);
            if(firstTargetSelf)
            	p.addTarget(new PointTarget(p.getPosition()));
            targets.forEach(p::addTarget);
            particles.add(p);
        }
        return particles;
    }
	private static int drawDoor(Vector2 from, Vector2 to, float radius, float step, List<Particle> list, int startIndex, int orientation, float speed) {
		int particleCount = (int)(from.dst(to) / step);
		float relativeStep = 1f / particleCount;
		for (int i = 0; i < particleCount; i++) {
			list.add(new DoorParticle(startIndex, from.cpy().lerp(to, relativeStep * i), speed, radius, radius, 1, orientation));
		}
		return startIndex + particleCount;
	}

	private static int drawLine(Vector2 from, Vector2 to, float radius, float step, List<Particle> list, int startIndex) {
		int particleCount = (int)(from.dst(to) / step);
		float relativeStep = 1f / particleCount;
		for (int i = 0; i < particleCount; i++) {
			list.add(new Particle(startIndex, from.cpy().lerp(to, relativeStep * i), 0, radius, radius, 0, 0));
		}
		return startIndex + particleCount;
	}

	public static void writeToFile(Writer writer, Particle p) {
		try {
			writer.write(p.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeCaudalToFile(Writer writer, double caudal) {
		try {
			writer.write(String.valueOf(caudal));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Vector2 getClosestPoint(Vector2 p, float minX, float maxX, float minY, float maxY) {
		return new Vector2(Math.max(minX, Math.min(p.x, maxX)), Math.max(minY, Math.min(p.y, maxY)));
	}
}
