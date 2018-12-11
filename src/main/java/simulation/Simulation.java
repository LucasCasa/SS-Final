package simulation;

import com.google.common.collect.ImmutableList;
import particles.Particle;
import particles.Wall;
import target.RectangularTarget;
import target.Target;
import vector.Vector2;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Simulation {

	private static float DELTA_TIME = 0.001f;
	List<Particle> particles = new ArrayList<>();
	List<Particle> wallParticles = new ArrayList<>();
	List<Particle> doorParticles = new ArrayList<>();
	List<Wall> walls;
	List<Particle> particlesToBottom1;
	List<Particle> particlesToBottom2;
	List<Particle> particlesToBottom3;
	List<Particle> particlesToBottom = new ArrayList<>();
	List<Particle> particlesToTop = new ArrayList<>();
	Map<Particle, Boolean> isFree = new HashMap<>();
	List<Particle> doors = new ArrayList<>();
	Target target1Bottom;
	Target target2Bottom;
	Target target3Bottom;

	Target door1Target;
	Target door2Target;
	Target door3Target;

	public Simulation() {
		Random r = new Random(3);
	}

	public SimulationData simulate(float patience, boolean saveState) throws IOException {
		SimulationData data = new SimulationData();
		long time = System.currentTimeMillis();
		int simulationSeconds = 20;
		boolean move = false;
		int totalTime = (int)(1 / DELTA_TIME * simulationSeconds);
		int step = totalTime / (60 * simulationSeconds);
		int count = step;
		double timeElapsed = 0.0;
		double timeToEscape = -1.0;
		double timeToEnter = -1.0;
		for (int i = 0; i < totalTime && !(timeToEnter != -1 && timeToEscape != -1); i++) {
			if(timeElapsed > patience && !move){
				move = true;
				particlesToBottom1.forEach(p -> {p.addTarget(door1Target); p.addTarget(target1Bottom);});
				particlesToBottom2.forEach(p -> {p.addTarget(door2Target); p.addTarget(target2Bottom);});
				particlesToBottom3.forEach(p -> {p.addTarget(door3Target); p.addTarget(target3Bottom);});
			}
			//Calculate next state
			particles.forEach(pa -> pa.update(particles, walls, DELTA_TIME));
			doorParticles.forEach(pa -> pa.update(particles, walls, DELTA_TIME));
			walls.forEach(pa -> pa.update(DELTA_TIME));

			//Update all particles at the same time
			particles.forEach(pa -> {
				int result = pa.applyVelocity();
				if(result == 1){
					isFree.put(pa, true);
				} else if(result == -1){
					isFree.put(pa, false);
				};
				//g.updateParticle(pa);
			});
			boolean allEnter = true;
			for (int i1 = 0; (i1 < particlesToBottom.size()) && allEnter; i1++) {
				allEnter = isFree.get(particlesToBottom.get(i1));
			}
			if(allEnter && timeToEnter == -1) {
				timeToEnter = timeElapsed;
			} else if (!allEnter){
				timeToEnter = -1;
			}
			boolean allExit = true;
			for (int i1 = 0; (i1 < particlesToTop.size()) && allExit; i1++) {
				allExit = isFree.get(particlesToTop.get(i1));
			}
			if(allExit && timeToEscape == -1) {
				timeToEscape = timeElapsed;
			} else if (!allExit){
				timeToEscape = -1;
			}
			// Do a correction on the calculated velocity
			particles.forEach(pa -> pa.correctVelocity(particles, walls, DELTA_TIME));

			if(step == count && saveState){
				data.addState(particles, wallParticles, doorParticles);
				count = 0;
			}
			count++;
			timeElapsed += DELTA_TIME;
		}
		data.setEnterTime(timeToEnter);
		data.setExitTime(timeToEscape);
		System.out.println("Time to escape: " + timeToEscape);
		System.out.println("Time to enter: " + timeToEnter);
		System.out.println("Simulation Time: " + (System.currentTimeMillis() - time));
		return data;
	}

	public void loadAllParticles(float drivingForceEnter, float drivingForceExit, float SFMagnitude, float doorTargetRadius){

		Vector2 center1 = new Vector2(3, 4);
		Vector2 center2 = new Vector2(7, 4);
		Vector2 center3 = new Vector2(11, 4);

		float doorRadius = 0.7f;

		door1Target = new RectangularTarget(center1, doorTargetRadius, 0.1f, true);
		Target target1Top = new RectangularTarget(new Vector2(center1.x,center1.y + 6f), doorRadius * 2, 0.5f, false);
		target1Bottom = new RectangularTarget(new Vector2(center1.x,center1.y - 3), 3, 0.5f, false);

		door2Target = new RectangularTarget(center2, doorTargetRadius, 0.1f, true);
		Target target2Top = new RectangularTarget(new Vector2(center2.x,center2.y + 6f), doorRadius * 2, 0.5f, false);
		target2Bottom = new RectangularTarget(new Vector2(center2.x,center2.y - 3), 3, 0.5f, false);

		door3Target = new RectangularTarget(center3, doorTargetRadius, 0.1f, true);
		Target target3Top = new RectangularTarget(new Vector2(center3.x,center3.y + 6f), doorRadius * 2, 0.5f, false);
		target3Bottom = new RectangularTarget(new Vector2(center3.x,center3.y - 3), 3, 0.5f, false);


		walls = Utils.placeBoxWithOpening(wallParticles, doorParticles, 0, 14, 0, center1.y, ImmutableList.of(center1.x,center2.x,center3.x), doorRadius);
		int start = particles.size();
		List<Particle> escapingParticles1 = Utils.placePeopleRandomly(start, center1, 1, -1, drivingForceExit, SFMagnitude, ImmutableList.of(door1Target, target1Top), false);
		start += escapingParticles1.size();
		List<Particle> escapingParticles2 = Utils.placePeopleRandomly(start, center2, 1, -1, drivingForceExit, SFMagnitude, ImmutableList.of(door2Target, target2Top), false);
		start += escapingParticles2.size();
		List<Particle> escapingParticles3 = Utils.placePeopleRandomly(start, center3, 1, -1, drivingForceExit, SFMagnitude, ImmutableList.of(door3Target, target3Top), false);
		start += escapingParticles3.size();

		particlesToTop.addAll(escapingParticles1);
		particlesToTop.addAll(escapingParticles2);
		particlesToTop.addAll(escapingParticles3);
		particles.addAll(escapingParticles1);
		particles.addAll(escapingParticles2);
		particles.addAll(escapingParticles3);

		particlesToBottom1 = Utils.placePeopleNotBlockingExit(start, center1, 1, doorRadius, 1, drivingForceEnter, SFMagnitude, ImmutableList.of(), true);
		start += particlesToBottom1.size();
		particlesToBottom2 = Utils.placePeopleNotBlockingExit(start, center2, 1, doorRadius, 1, drivingForceEnter, SFMagnitude, ImmutableList.of(), true);
		start += particlesToBottom2.size();
		particlesToBottom3 = Utils.placePeopleNotBlockingExit(start, center3, 1, doorRadius, 1, drivingForceEnter, SFMagnitude, ImmutableList.of(), true);

		//particlesToBottom = simulation.Utils.placePeopleRandomly(start + escapingParticles.size(), center, 3, 1, 4, ImmutableList.of(doorTarget, targetBottom), false);

		particlesToBottom.addAll(particlesToBottom1);
		particlesToBottom.addAll(particlesToBottom2);
		particlesToBottom.addAll(particlesToBottom3);
		particles.addAll(particlesToBottom1);
		particles.addAll(particlesToBottom2);
		particles.addAll(particlesToBottom3);

		escapingParticles1.forEach(p -> isFree.put(p, false));
		escapingParticles2.forEach(p -> isFree.put(p, false));
		escapingParticles3.forEach(p -> isFree.put(p, false));

		particlesToBottom1.forEach(p -> isFree.put(p, false));
		particlesToBottom2.forEach(p -> isFree.put(p, false));
		particlesToBottom3.forEach(p -> isFree.put(p, false));

	}
}
