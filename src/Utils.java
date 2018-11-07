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
			particleList.add(new Particle(i, 10000,new Vector2(i * 0.2f,0),0,0.2f,0.2f));
			if(i*0.2f < 3 - 0.2f || i * 0.2f > 3 + 0.2f)
				particleList.add(new Particle(i, 10000,new Vector2(i * 0.2f,5),0,0.2f,0.2f));
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
