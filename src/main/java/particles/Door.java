package particles;

import vector.Vector2;

import java.util.List;

public class Door extends Wall {

    float distanceMoved = 0;
    float distanceToMove;
    int orientation;
    float delay = 1;
    float elapsed = 0;
    float speed;

    public Door(float minX, float maxX, float minY, float maxY, int orientation, float distanceToMove, float speed){
        super(minX, maxX, minY, maxY);
        this.orientation = orientation;
        this.distanceToMove = distanceToMove;
        this.speed = speed;
    }

    public Door(Vector2 center, float radiusX, float radiusY, int orientation, float distanceToMove, float speed){
        super(center.x - radiusX, center.x + radiusX, center.y - radiusY, center.y + radiusY);
        this.orientation = orientation;
        this.distanceToMove = distanceToMove;
        this.speed = speed;
    }

    @Override
    public void update(float deltaTime) {
        elapsed+= deltaTime;
        if(elapsed > delay) {
            if (distanceMoved < distanceToMove) {
                float delta = orientation * deltaTime * speed;
                minX += delta;
                maxX += delta;
                distanceMoved += Math.abs(delta);
            }
        }
    }
}
