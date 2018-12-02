import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by root on 3/14/17.
 */
public class RegularGrid extends Grid {

    public RegularGrid(double w,double h, double cellSide){
        super(w,h, cellSide);
    }

    @Override
    public List<List<Particle>> checkNeighbors(double rc) {
        for(int i = 0; i< super.particles.size();i++){
            neigh.get(i).clear();
        }
        int count = 0;
        for(int i = 0; i<grid.length;i++){
            for(int j = 0; j<grid.length;j++){
                for(int k = 0;k<grid[i][j].getParticleList().size();k++){
                    Particle p = grid[i][j].getParticleList().get(k);
                    for(int l =k+1;l<grid[i][j].getParticleList().size();l++){
                        Particle p1 = grid[i][j].getParticleList().get(l);
                        count++;
                        if (Particle.dist2(p, p1) < (p.getCurrentRadius() + p1.getCurrentRadius() + rc)*(p.getCurrentRadius() + p1.getCurrentRadius() + rc)) {
                            neigh.get(p.getId()).add(p1);
                            neigh.get(p1.getId()).add(p);
                        }
                    }

                    if(i+1 <= grid.length -1) {
                        for (Particle p1 : grid[i + 1][j].getParticleList()) {
                            count++;
                            if (Particle.dist2(p, p1) <= (p.getCurrentRadius() + p1.getCurrentRadius() + rc)*(p.getCurrentRadius() + p1.getCurrentRadius() + rc)) {
                                neigh.get(p.getId()).add(p1);
                                neigh.get(p1.getId()).add(p);
                            }
                        }
                    }
                    if(i-1 >= 0 && j+1 <= grid.length-1) {
                        for (Particle p1 : grid[i - 1][j + 1].getParticleList()) {
                            count++;
                            if (Particle.dist2(p, p1) <= (p.getCurrentRadius() + p1.getCurrentRadius() + rc)*(p.getCurrentRadius() + p1.getCurrentRadius() + rc)) {
                                neigh.get(p.getId()).add(p1);
                                neigh.get(p1.getId()).add(p);
                            }
                        }

                    }
                    if(j+1 <= grid.length-1) {
                        for (Particle p1 : grid[i][j + 1].getParticleList()) {
                            count++;
                            if (Particle.dist2(p, p1) <= (p.getCurrentRadius() + p1.getCurrentRadius() + rc)*(p.getCurrentRadius() + p1.getCurrentRadius() + rc)) {
                                neigh.get(p.getId()).add(p1);
                                neigh.get(p1.getId()).add(p);
                            }
                        }
                    }
                    if(j+1 <= grid.length -1 && i+1 <= grid.length-1) {
                        for (Particle p1 : grid[i + 1][j + 1].getParticleList()) {
                            count++;
                            if (Particle.dist2(p, p1) <= (p.getCurrentRadius() + p1.getCurrentRadius() + rc)*(p.getCurrentRadius() + p1.getCurrentRadius() + rc)) {
                                neigh.get(p.getId()).add(p1);
                                neigh.get(p1.getId()).add(p);
                            }
                        }
                    }
                }
            }
        }
        return neigh;

    }
    public void updateParticle(Particle p){
        int newx = (int)(p.getPosition().x / cellSide);
        int newy = (int)(p.getPosition().y / cellSide);
        if(p.cellx != newx || newy != p.celly){
            Cell s = grid[p.cellx][p.celly];
            s.getParticleList().remove(p);
            grid[newx][newy].getParticleList().add(p);
            p.cellx = newx;
            p.celly = newy;
        }
    }


}
