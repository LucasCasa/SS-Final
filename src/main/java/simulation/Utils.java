package simulation;

import particles.*;
import target.PointTarget;
import target.RectangularTarget;
import target.Target;
import vector.Vector2;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utils {

    private static float MIN_RADIUS = 0.15f;
    private static float MAX_RADIUS = 0.2f;

    private Utils() {

	}


	public static List<Wall> placeBoxWithOpening(List<Particle> wallParticles, List<Particle> doorParticles,  float minX, float maxX, float minY, float maxY, List<Float> openCenter, float openRadius ) {
	    float openStart = openCenter.get(0) - openRadius;
	    float openEnd = openCenter.get(openCenter.size() - 1) + openRadius;
	    float radius = 0.05f;
	    float step = 0.05f;
	    int count = 0;

	    List<Wall> walls = new ArrayList<>();
        count = drawLine(new Vector2(minX,minY), new Vector2(maxX,minY), radius, step, wallParticles, count); //  0,0 -> x,0
		walls.add(new Wall(minX, maxX, minY, minY));
        count = drawLine(new Vector2(minX,minY), new Vector2(minX,maxY), radius, step, wallParticles, count); // 0,0 -> 0, 10
		walls.add(new Wall(minX, minX, minY, maxY));
        count = drawLine(new Vector2(maxX,minY), new Vector2(maxX,maxY), radius, step, wallParticles, count); // x, 0 -> x, y
		walls.add(new Wall(maxX, maxX, minY, maxY));
		count = drawLine(new Vector2(minX,maxY), new Vector2(openStart,maxY), radius, step, wallParticles, count); // Open left
		walls.add(new Wall(minX, openStart, maxY, maxY));
		for(int i = 0; i < openCenter.size();i++) {
			float openStartSegment = openCenter.get(i) - openRadius;
			float openEndSegment = openCenter.get(i) + openRadius;
			count = drawDoor(new Vector2(openStartSegment, maxY), new Vector2(openStartSegment + (openEndSegment - openStartSegment) / 2, maxY), radius, step, doorParticles, count, -1, 2); // Door left
			walls.add(new Door(openStartSegment, openStartSegment + (openEndSegment - openStartSegment) / 2, maxY, maxY, -1, 1, 2));
			count = drawDoor(new Vector2(openStartSegment + (openEndSegment - openStartSegment) / 2, maxY), new Vector2(openEndSegment, maxY), radius, step, doorParticles, count, 1, 2); // Door right
			walls.add(new Door(openStartSegment + (openEndSegment - openStartSegment) / 2, openEndSegment, maxY, maxY, 1, 1, 2));
			if(i + 1 < openCenter.size()) {
				float nextStart = openCenter.get(i + 1) - openRadius;
				count = drawLine(new Vector2(openEndSegment, maxY), new Vector2(nextStart,maxY), radius, step, wallParticles, count);
				walls.add(new Wall(openEndSegment, nextStart, maxY, maxY));
			}
		}
		count = drawLine(new Vector2(openEnd,maxY), new Vector2(maxX,maxY), radius, step, wallParticles, count); // Open right
		walls.add(new Wall(openEnd, maxX, maxY, maxY));
		return walls;
	}

	public static List<Particle> placePeopleRandomly(int count, int start, Vector2 center, float spawnRadius, int orientation, float drivingForce, float SFMagnitude, List<Target> targets, boolean waitOnSide, boolean firstTargetSelf) {
        return placePeopleNotBlockingExit(count, start, center, spawnRadius, 0, orientation, drivingForce, SFMagnitude, targets, waitOnSide, firstTargetSelf);
    }

    public static List<Particle> placePeopleNotBlockingExit(int count, int start, Vector2 center, float spawnRadius, float doorRadius, int orientation, float drivingForce, float SFMagnitude, List<Target> targets, boolean waitOnSide, boolean firstTargetSelf) {
        Target leftRectTarget  = new RectangularTarget(center.x - doorRadius - 0.5f, center.x - doorRadius - MAX_RADIUS, center.y + MIN_RADIUS, center.y + 2, false);
        Target rightRectTarget = new RectangularTarget(center.x + doorRadius + MAX_RADIUS, center.x + doorRadius + 0.5f, center.y + MIN_RADIUS, center.y + 2, false);
        List<Particle> particles = new ArrayList<>();
	    Random r = new Random();
        for (int i = 0; i < count; i++) {
            boolean validPos = false;
            Vector2 position = null;
            float radius = r.nextFloat()*(MAX_RADIUS - MIN_RADIUS) + MIN_RADIUS;
            boolean leftSide = false;
            while (!validPos){
                validPos = true;
                leftSide = r.nextFloat() > 0.5;
                if(leftSide) { //LEFT
                    position = new Vector2(center.x - doorRadius - r.nextFloat() * 1f ,r.nextFloat()*spawnRadius*orientation + orientation*radius + center.y);
                } else { //RIGHT
                    position = new Vector2(center.x + doorRadius + r.nextFloat() * 1f ,r.nextFloat()*spawnRadius*orientation + orientation*radius + center.y);
                }
                for (Particle particle : particles) {
                    if(position.dst(particle.getPosition()) <= particle.getCurrentRadius() + radius){
                        validPos = false;
                    }
                }
                if(doorRadius > 0 && position.x >= center.x - doorRadius - radius && position.x <= center.x + doorRadius + radius){
                    validPos = false;
                }
            }
            Particle p = new Particle(start + i, position, 1.95f, radius, radius, 80, drivingForce, SFMagnitude);
            if(waitOnSide) {
            	if (leftSide){
					p.addTarget(leftRectTarget);
				} else {
            		p.addTarget(rightRectTarget);
				}
			}
			if(firstTargetSelf) {
			    p.addTarget(new PointTarget(p.getPosition(), false));
            }
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
			list.add(new WallParticle(startIndex, from.cpy().lerp(to, relativeStep * i), radius, radius));
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
