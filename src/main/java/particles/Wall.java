package particles;

import simulation.Utils;
import vector.Vector2;

import java.util.List;

public class Wall {
    float minX;
    float maxX;
    float minY;
    float maxY;

    public Wall(float minX, float maxX, float minY, float maxY){
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    public Wall(Vector2 center, float radiusX, float radiusY){
        this.minX = center.x - radiusX;
        this.maxX = center.x + radiusX;
        this.minY = center.y - radiusY;
        this.maxY = center.y + radiusY;
    }

    public void update(float deltaTime){

    }
    public Vector2 getClosestPoint(Particle p){
        return Utils.getClosestPoint(p.getPosition(), minX, maxX, minY, maxY);
    }

}
