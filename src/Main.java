import vector.Vector2;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {


	//tema de la puerta. HACERLO MAS feo.
	private static float DELTA_TIME = 0.001f;
    private static float MIN_RADIUS = 0.15f;
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
			Vector2 target = new Vector2(2,3);
			Vector2 targetTop2 = new Vector2(2,3.3f);
            Vector2 targetTop = new Vector2(2,6);
            Vector2 targetBottom = new Vector2(2,1);
            Vector2 targetBottom2 = new Vector2(2,2.7f);
			Utils.placeBoxWithOpening(particles, 0, 5, 0, 3, 2, 0.4f);
			int start = particles.size();
			long time = System.currentTimeMillis();
            int i;
			for (i = 0; i < 10; i++) {
			    boolean validPos = false;
                Vector2 position = null;
			    while (!validPos){
			        validPos = true;
			        position = new Vector2(r.nextFloat() * 1 + 1.5f,r.nextFloat()*1.5f + 1.5f);
                    for (Particle particle : particles) {
                        if(position.dst(particle.getPosition()) < 2 * MIN_RADIUS){
                            validPos = false;
                        }
                    }
                }
				Particle p = new Particle(start + i, position, 1.95f, MIN_RADIUS,MIN_RADIUS, 80, 15);
				p.addTarget(targetTop2);
                p.addTarget(targetTop);
				particles.add(p);
			}
            start+= i;
            for (i = 0; i < 10; i++) {
                boolean validPos = false;
                Vector2 position = null;
                while (!validPos){
                    validPos = true;

                    position = new Vector2(r.nextFloat() * 2.5f + 0.5f,r.nextFloat()*1f + 3);
                    for (Particle particle : particles) {
                        if(position.dst(particle.getPosition()) < 2 * MIN_RADIUS){
                            validPos = false;
                        }
                    }

                    if(position.x >= 1.45f && position.x <= 2.55f){
						validPos = false;
					}
                }
                Particle p = new Particle(start + i, position, 1.95f, MIN_RADIUS,MIN_RADIUS, 80, 5);

                p.addTarget(new Vector2(position.x, position.y));
                particles.add(p);
                particlesToBottom.add(p);
            }
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