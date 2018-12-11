package simulation;

import com.google.common.collect.ImmutableList;
import particles.Particle;
import particles.Wall;
import target.RectangularTarget;
import target.Target;
import vector.Vector2;

import javax.rmi.CORBA.Util;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {


	//tema de la puerta. HACERLO MAS feo.
	private static float DELTA_TIME = 0.001f;
    private static float MIN_RADIUS = 0.30f;
	public static void main(String[] args) {
        Random r = new Random(3);
		try {
			FileOutputStream f = new FileOutputStream("test.txt");
			OutputStreamWriter out = new OutputStreamWriter(f, "utf-8");
			Writer writer = new BufferedWriter(out);
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

			Vector2 center1 = new Vector2(3, 4);
			Vector2 center2 = new Vector2(7, 4);
			Vector2 center3 = new Vector2(11, 4);

			float doorRadius = 0.7f;

			Target door1Target = new RectangularTarget(center1, doorRadius - 0.1f, 0.1f, true);
			Target target1Top = new RectangularTarget(new Vector2(center1.x,center1.y + 6f), doorRadius * 2, 0.5f, false);
            Target target1Bottom = new RectangularTarget(new Vector2(center1.x,center1.y - 3), 3, 0.5f, false);

			Target door2Target = new RectangularTarget(center2, doorRadius - 0.1f, 0.1f, true);
			Target target2Top = new RectangularTarget(new Vector2(center2.x,center2.y + 6f), doorRadius * 2, 0.5f, false);
			Target target2Bottom = new RectangularTarget(new Vector2(center2.x,center2.y - 3), 3, 0.5f, false);

			Target door3Target = new RectangularTarget(center3, doorRadius - 0.1f, 0.1f, true);
			Target target3Top = new RectangularTarget(new Vector2(center3.x,center3.y + 6f), doorRadius * 2, 0.5f, false);
			Target target3Bottom = new RectangularTarget(new Vector2(center3.x,center3.y - 3), 3, 0.5f, false);


			walls = Utils.placeBoxWithOpening(wallParticles, doorParticles, 0, 14, 0, center1.y, ImmutableList.of(center1.x,center2.x,center3.x), doorRadius);
			int start = particles.size();
			long time = System.currentTimeMillis();
            int i;
			List<Particle> escapingParticles1 = Utils.placePeopleRandomly(start, center1, 1, -1, 6, ImmutableList.of(door1Target, target1Top), false);
			start += escapingParticles1.size();
			List<Particle> escapingParticles2 = Utils.placePeopleRandomly(start, center2, 1, -1, 6, ImmutableList.of(door2Target, target2Top), false);
			start += escapingParticles2.size();
			List<Particle> escapingParticles3 = Utils.placePeopleRandomly(start, center3, 1, -1, 6, ImmutableList.of(door3Target, target3Top), false);
			start += escapingParticles3.size();

            particlesToTop.addAll(escapingParticles1);
            particlesToTop.addAll(escapingParticles2);
            particlesToTop.addAll(escapingParticles3);
			particles.addAll(escapingParticles1);
			particles.addAll(escapingParticles2);
			particles.addAll(escapingParticles3);

			particlesToBottom1 = Utils.placePeopleNotBlockingExit(start, center1, 1, doorRadius, 1, 6, ImmutableList.of(), true);
			start += particlesToBottom1.size();
			particlesToBottom2 = Utils.placePeopleNotBlockingExit(start, center2, 1, doorRadius, 1, 6, ImmutableList.of(), true);
			start += particlesToBottom2.size();
			particlesToBottom3 = Utils.placePeopleNotBlockingExit(start, center3, 1, doorRadius, 1, 6, ImmutableList.of(), true);

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

			System.out.println("Starting Simulation");
			float patience = 2;
			int simulationSeconds = 30;
			boolean move = false;
			int totalTime = (int)(1 / DELTA_TIME * simulationSeconds);
			int step = totalTime / (60 * simulationSeconds);
			int count = step;
			double timeElapsed = 0.0;
			AtomicInteger escapeCounter = new AtomicInteger();
			AtomicInteger enterCounter = new AtomicInteger();
			double timeToEscape = 0.0;
			double timeToEnter = 0.0;
			for (i = 0; i < totalTime; i++) {
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

				if(step == count){
					writer.write((particles.size() + wallParticles.size() + doorParticles.size() - 1) + "\n");
					wallParticles.forEach(pa -> Utils.writeToFile(writer, pa));
					doorParticles.forEach(pa -> Utils.writeToFile(writer, pa));
					particles.forEach(pa -> Utils.writeToFile(writer, pa));
					count = 0;
				}
				count++;
				timeElapsed += DELTA_TIME;
			}
			System.out.println("Time to escape: " + timeToEscape);
			System.out.println("Time to enter: " + timeToEnter);
			writer.close();
            System.out.println("Simulation Time: " + (System.currentTimeMillis() - time));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
