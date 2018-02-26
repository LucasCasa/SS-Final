import java.awt.*;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class Utils {

	private Utils() {

	}


	public static List<Particle> placeObstacles(List<Particle> particleList) {
		for(int i = 0; i< 100; i++) {
			particleList.add(new Particle(10000,new Point(i,0),0,1,1));
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
