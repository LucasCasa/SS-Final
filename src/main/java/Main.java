import com.google.common.collect.ImmutableList;
import vector.Vector;
import vector.Vector2;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
			List<Particle> particlesToBottom = new ArrayList<>();
			List<Particle> walls = new ArrayList<>();
			List<Particle> doors = new ArrayList<>();

			Vector2 center = new Vector2(3, 4);
			Vector2 targetTop2 = new Vector2(center.x,center.y + 0.5f);
            Vector2 targetTop = new Vector2(center.x,center.y + 6);
            Vector2 targetBottom = new Vector2(center.x,center.y - 3);
			Vector2 targetBottom2 = new Vector2(center.x,center.y - 0.5f);

			float doorRadius = 0.8f;

			Utils.placeBoxWithOpening(particles, 0, 6, 0, center.y, center.x, doorRadius);
			int start = particles.size();
			long time = System.currentTimeMillis();
            int i;
			List<Particle> escapingParticles = Utils.placePeopleRandomly(start, center, 2, -1, 10, ImmutableList.of(targetTop2, targetTop), false);
			particles.addAll(escapingParticles);

			particlesToBottom = Utils.placePeopleNotBlockingExit(start + escapingParticles.size(), center, 3, doorRadius, 1, 2, ImmutableList.of(), true);

			//particlesToBottom = Utils.placePeopleRandomly(start + escapingParticles.size(), center, 3, 1, 4, ImmutableList.of(targetBottom2, targetBottom), false);

			particles.addAll(particlesToBottom);


			System.out.println("Starting Simulation");
			RegularGrid g = new RegularGrid(100, 100, 4);
			g.setCells(particles);
			int totalTime = (int)(1 / DELTA_TIME * 20);
			int step = totalTime / (60 * 20);
			int count = step;
			double timeElapsed = 0.0;
			AtomicInteger escapeCounter = new AtomicInteger();
			AtomicInteger enterCounter = new AtomicInteger();
			double timeToEscape = 0.0;
			double timeToEnter = 0.0;
			for (i = 0; i < totalTime; i++) {
				//List<List<Particle>> n = g.checkNeighbors(0);

				particles.forEach(pa -> {
					pa.update(particles, DELTA_TIME);
				});

				particles.forEach(pa -> {
 					if(pa.applyVelocity()){
 						if(escapeCounter.get() < 10){
							escapeCounter.addAndGet(1);
						}else{
 							if(escapeCounter.get() == 11 &&  enterCounter.get() < 10){
 								enterCounter.addAndGet(1);
							}
						}
					};
					//g.updateParticle(pa);
				});

				if(escapeCounter.get() == 10){
					timeToEscape = timeElapsed;
					particlesToBottom.forEach(pa -> {
						pa.addTarget(targetBottom2);
						pa.addTarget(targetBottom);});
					escapeCounter.addAndGet(1);
				}

				if(enterCounter.get() == 10){
						timeToEnter = timeElapsed;
						enterCounter.addAndGet(1);
				}

				if(step == count){
					writer.write(particles.size() - 1 + "\n");
					particles.forEach(pa -> {
						Utils.writeToFile(writer, pa);
						//g.updateParticle(pa);
					});
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
