package simulation;

import java.io.*;
import java.util.*;

public class Main {

	public static void main(String[] args) throws IOException {
        boolean save = true;
		FileOutputStream f;
		OutputStreamWriter out;
		Writer writer = null;
		List<Double> tte = new ArrayList<>();
		List<Double> ttl = new ArrayList<>();
		if (save) {
			 f = new FileOutputStream("test.txt");
			 out = new OutputStreamWriter(f, "utf-8");
			 writer = new BufferedWriter(out);
		}
		SimulationData simData = null;
		for (int i = 0; i < 1; i++) {
			Simulation s = new Simulation();
			s.loadAllParticles(6, 6, 3500, 10, 10, 0.5f, true); //Particle count is per door, and side, son 10 is actually 60 particles total
			System.out.println("Starting Simulation");
			simData = s.simulate(2.5f, save);
			tte.add(simData.enterTime);
			ttl.add(simData.exitTime);
		}
		System.out.println("Time to leave");
		ttl.forEach(System.out::println);
		System.out.println("Time to enter");
		tte.forEach(System.out::println);

		Writer w = writer;
		if(save){
			simData.particlesData.forEach(frame -> {
				try {
					w.write(frame.size() - 1 + "\n");
					frame.forEach(state -> {
						try {
							w.write(state.toString());
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
				} catch (IOException e) {
					e.printStackTrace();
				}

			});
		}
		if(save)
			writer.close();

	}
}
