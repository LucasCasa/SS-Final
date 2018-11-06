

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 3/14/17.
 */
public abstract class Grid {
    Cell[][] grid;
    double w;
    double h;
    int ammount;
    double cellSide;
    List<Particle> particles;
    List<List<Particle>> neigh = new ArrayList<>();

    public Grid(double w,double h, double cellSide){
        this.grid = new Cell[(int)(w / cellSide)+2][(int)(h / cellSide)+2];
        this.w = w;
        this.h = h;
        this.cellSide = cellSide;
        System.out.println("Celda: x: " + ((int)(w / cellSide)+1) + " y: " + ((int)(h / cellSide)+1)  );
    }

    public void setCells(List<Particle> particles){
        for(int i=0; i < (int)(w / cellSide)+2; i++){
            for(int j=0; j< (int)(h / cellSide)+2; j++){
                grid[i][j] = new Cell(cellSide);
            }
        }
        int x;
        int y;
        ammount = particles.size();
        this.particles = particles;
        for(Particle p: particles){

            //System.out.println("LA POSICION EN X VALE: " + p.getX());
            //System.out.println("LA POSICION EN Y VALE: " + p.getY());
            x = (int)(p.getPosition().x / cellSide);
            y = (int)(p.getPosition().y / cellSide);
            /*if(x < 0){
                x = 0;
            }
            if(x >= grid.length){
                x = grid.length -1;
            }
            if(y < 0){
                y = 0;
            }
            if(y >= grid[0].length){
                y = grid.length-1;
            }*/
            //System.out.println("CELDA X: " + x);
            //System.out.println("CELDA Y: " + y);

            this.grid[x][y].particleList.add(p);
            p.cellx = x;
            p.celly = y;
            neigh.add(new ArrayList<>());
        }

    }



    public abstract List<List<Particle>> checkNeighbors(double rc);

    public void print(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i<grid.length;i++){
            for(int j = 0; j<grid[0].length;j++){
                System.out.println("Posicion: " + i + ", " + j);
                for(Particle p: grid[i][j].getParticleList()){
                    System.out.print(p + "  ");
                }
                System.out.println("");
            }
        }
    }

    public abstract void updateParticle(Particle p);


    public void remove(List<Particle> p) {
        for (Particle pa : p) {
            grid[pa.cellx][pa.celly].getParticleList().remove(pa);
        }
        particles.removeAll(p);
    }
}
