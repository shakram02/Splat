package shakram02.ahmed.splat.game;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import shakram02.ahmed.shapelibrary.gl_internals.shapes.Point;

/**
 * Created by ahmed on 12/11/17.
 */

public class LocationTracker {
    private final float deltaY;
    private ArrayList<Point> enemyLocations;
    private int index;
    private boolean doneLoop;

    public LocationTracker(float deltaY) {
        this.deltaY = deltaY;
        enemyLocations = new ArrayList<>();
    }

    public boolean hasEnemies() {
        return !enemyLocations.isEmpty();
    }

    public void addEnemy(float xLocation) {
        enemyLocations.add(new Point(xLocation, 1));
    }

    public void addEnemy(Collection<Float> locations) {
        for (Float p : locations) {
            this.addEnemy(p);
        }
    }


    public void addEnemy(float[] locations) {
        for (Float p : locations) {
            this.addEnemy(p);
        }
    }


    public boolean isFrameDone() {
        boolean temp = doneLoop;

        if (doneLoop) {
            doneLoop = false;
        }

        return temp;
    }

    @NonNull
    public Point getNextEnemyLocation() {
        if (index >= enemyLocations.size() - 1) {
            index = 0;
            doneLoop = true;
        } else {
            index++;
            doneLoop = false;
        }

        Point enemyLocation = enemyLocations.get(index);

        Point returnedVal = enemyLocation;
        enemyLocation = updateEnemyLocation(enemyLocation);
        enemyLocations.set(index, enemyLocation);

        if (Float.compare(enemyLocation.getY(), -2f) < 0) {
            enemyLocations.remove(index);
        }

        return returnedVal;
    }

    private Point updateEnemyLocation(Point currentLocation) {
        return new Point(currentLocation.getX(), currentLocation.getY() - deltaY);
    }
}
