import vector.Vector2;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class Utils {

	private Utils() {

	}


	public static List<Particle> placeBoxWithOpening(List<Particle> particleList, int minX, int maxX, int minY, int maxY, float openCenter, float openRadius ) {
	    float openStart = openCenter - openRadius;
	    float openEnd = openCenter + openRadius;
	    float radius = 0.05f;
	    float step = 0.05f;
	    int count = 0;
        count = drawLine(new Vector2(minX,minY), new Vector2(maxX,minY), radius, step, particleList, count); //  0,0 -> x,0
        count = drawLine(new Vector2(minX,minY), new Vector2(minX,maxY), radius, step, particleList, count); // 0,0 -> 0, 10
        count = drawLine(new Vector2(maxX,minY), new Vector2(maxX,maxY), radius, step, particleList, count); // x, 0 -> x, y
        count = drawLine(new Vector2(minX,maxY), new Vector2(openStart,maxY), radius, step, particleList, count); // Open left
		count = drawDoor(new Vector2(openStart,maxY), new Vector2(openStart + (openEnd - openStart) / 2,maxY), radius, step, particleList, count, -1); // Open left
		count = drawDoor(new Vector2(openStart + (openEnd - openStart) / 2,maxY), new Vector2(openEnd,maxY), radius, step, particleList, count, 1); // Open left
        count = drawLine(new Vector2(openEnd,maxY), new Vector2(maxX,maxY), radius, step, particleList, count); // Open right
		return particleList;
	}

	private static int drawDoor(Vector2 from, Vector2 to, float radius, float step, List<Particle> list, int startIndex, int orientation) {
		int particleCount = (int)(from.dst(to) / step);
		float relativeStep = 1f / particleCount;
		for (int i = 0; i < particleCount; i++) {
			list.add(new DoorParticle(startIndex++, from.cpy().lerp(to, relativeStep * i), 0, radius, radius, 1, orientation));
		}
		return startIndex;
	}

	private static int drawLine(Vector2 from, Vector2 to, float radius, float step, List<Particle> list, int startIndex) {
		int particleCount = (int)(from.dst(to) / step);
		float relativeStep = 1f / particleCount;
		for (int i = 0; i < particleCount; i++) {
			list.add(new WallParticle(startIndex++, from.cpy().lerp(to, relativeStep * i), 0, radius, radius));
		}
		return startIndex;
	}

	public static void writeToFile(Writer writer, Particle p) {
		try {
			writer.write(p.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeCaudalToFile(Writer writer, double caudal) {
		try {
			writer.write(String.valueOf(caudal));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
