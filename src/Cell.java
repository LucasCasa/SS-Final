
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by root on 3/11/17.
 */
public class Cell {
    double side;
    Cell E;
    Cell SE;
    Cell S;
    List<Particle> particleList = new ArrayList<>();

    public Cell(double side){
        this.side = side;
    }

    public double getArea(){
        return side*side;
    }

    public double getSide(){
        return side;
    }

    public List<Particle> getParticleList(){
        return particleList;
    }


    public void setSouth(Cell south) {
        this.S = south;
    }

    public void setEast(Cell east) {
        this.E = east;
    }

    public void setSouthEast(Cell southEast) {
        this.SE = southEast;
    }
}
