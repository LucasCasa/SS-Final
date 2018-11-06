import vector.Vector2;

import java.awt.*;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class Utils {

	private Utils() {

	}


	public static List<Particle> placeObstacles(List<Particle> particleList) {
		for(int i = 0; i< 100; i++) {
			particleList.add(new Particle(i, 10000,new Vector2(i,0),0,1,1));
			if(i > 15 || i < 11)
				particleList.add(new Particle(i, 10000,new Vector2(i,20),0,1,1));
		}
		return particleList;
	}

	public static void writeToFile(Writer writer, Particle p) {
		try {
			writer.write(p.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
