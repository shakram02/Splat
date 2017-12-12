package shakram02.ahmed.splat.game;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ahmed on 12/12/17.
 */

public class SpawnEnemyTask extends TimerTask {
    private final Timer spawnTimer;
    private final LocationTracker locationTracker;
    private final int minDelay;
    private final int maxDelay;

    public SpawnEnemyTask(Timer spawnTimer, LocationTracker locationTracker, int minDelay, int maxDelay) {
        this.spawnTimer = spawnTimer;
        this.locationTracker = locationTracker;
        this.minDelay = minDelay;
        this.maxDelay = maxDelay;
    }

    @Override
    public void run() {
        float xLocation = (float) (-0.7 + (Math.random() * (0.7 + 0.7)));
        locationTracker.addEnemy(xLocation);

        long nextEnemyAfter = (long) (1000 + (Math.random() * (maxDelay - minDelay)));
        spawnTimer.schedule(
                new SpawnEnemyTask(spawnTimer, locationTracker,
                        minDelay, maxDelay)
                , nextEnemyAfter);
    }
}
