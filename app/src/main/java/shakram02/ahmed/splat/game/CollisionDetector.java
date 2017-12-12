package shakram02.ahmed.splat.game;

import shakram02.ahmed.shapelibrary.gl_internals.shapes.Point;

/**
 * Created by ahmed on 12/11/17.
 */

public class CollisionDetector {

    private final float targetRadius;

    public CollisionDetector(float targetRadius) {
        this.targetRadius = targetRadius;
    }

    public boolean collidesWith(Point firstPoint, Point secondPoint) {
        return distance(firstPoint, secondPoint) <= targetRadius;
    }

    private float distance(Point p1, Point p2) {
        float xDistance = (float) Math.pow((p1.getX() - p2.getX()), 2);
        float yDistance = (float) Math.pow((p1.getY() - p2.getY()), 2);

        return (float) Math.sqrt(xDistance + yDistance);
    }
}
