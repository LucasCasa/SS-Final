import vector.Vector2;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) {

		try {
			FileOutputStream f = new FileOutputStream("test.txt");
			OutputStreamWriter out = new OutputStreamWriter(f, "utf-8");
			Writer writer = new BufferedWriter(out);
			List<Particle> particles = new ArrayList<>();
			Utils.placeObstacles(particles);
			int start = particles.size();
			for (int i = 0; i < 50; i++) {
				Particle p = new Particle(start + i,1,new Vector2((i / 5),(i % 5)), 1.95f, 0.15f,0.32f);
				p.setTarget(new Vector2(3,6));
				particles.add(p);
			}
			RegularGrid g = new RegularGrid(100, 100, 4);
			g.setCells(particles);

			for (int i = 0; i < 60 * 10; i++) {
				writer.write(particles.size() - 1 + "\n");
				List<List<Particle>> n = g.checkNeighbors(0);
 				particles.forEach(pa -> {
					pa.update(1 / 60f, n.get(pa.getId()));
					Utils.writeToFile(writer, pa);
					g.updateParticle(pa);
					if(pa.getPosition().dst2(3,6) < 1) {
						pa.setTarget(new Vector2(3, 15));
					}
				});
			}
				writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
