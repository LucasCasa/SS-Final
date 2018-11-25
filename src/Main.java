import vector.Vector2;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class Main {

	private static float DELTA_TIME = 0.001f;
    private static float MIN_RADIUS = 0.15f;
	public static void main(String[] args) {
        Random r = new Random(3);
		try {
			FileOutputStream f = new FileOutputStream("test.txt");
			OutputStreamWriter out = new OutputStreamWriter(f, "utf-8");
			Writer writer = new BufferedWriter(out);
			List<Particle> particles = new ArrayList<>();
			Vector2 target = new Vector2(2,3);
            Vector2 targetTop = new Vector2(2,6);
            Vector2 targetBottom = new Vector2(2,1);
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
				p.addTarget(target);
                p.addTarget(targetTop);
				particles.add(p);
			}
            start+= i;
            for (i = 0; i < 10; i++) {
                boolean validPos = false;
                Vector2 position = null;
                while (!validPos){
                    validPos = true;
                    position = new Vector2(r.nextFloat() * 1 + 1.5f,r.nextFloat()*1.5f + 3);
                    for (Particle particle : particles) {
                        if(position.dst(particle.getPosition()) < 2 * MIN_RADIUS){
                            validPos = false;
                        }
                    }
                }
                Particle p = new Particle(start + i, position, 1.95f, MIN_RADIUS,MIN_RADIUS, 80, 5);
                p.addTarget(target);
                p.addTarget(targetBottom);
                particles.add(p);
            }
			System.out.println("Starting Simulation");
			RegularGrid g = new RegularGrid(100, 100, 4);
			g.setCells(particles);
			int totalTime = (int)(1 / DELTA_TIME * 20);
			int step = totalTime / (60 * 20);
			int count = step;
			for (i = 0; i < totalTime; i++) {
				//List<List<Particle>> n = g.checkNeighbors(0);

				particles.forEach(pa -> {
					pa.update(particles, DELTA_TIME);
				});

				particles.forEach(pa -> {
 					pa.applyVelocity();
					//g.updateParticle(pa);
				});
				if(step == count){
					writer.write(particles.size() - 1 + "\n");
					particles.forEach(pa -> {
						Utils.writeToFile(writer, pa);
						//g.updateParticle(pa);
					});
					count = 0;
				}
				count++;
			}
			writer.close();
            System.out.println(System.currentTimeMillis() - time);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
