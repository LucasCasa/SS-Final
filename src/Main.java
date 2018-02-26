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
			writer.write("100\n");
			particles.forEach(p -> Utils.writeToFile(writer,p));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
