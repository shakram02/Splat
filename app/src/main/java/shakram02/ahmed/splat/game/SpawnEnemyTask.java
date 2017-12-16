package shakram02.ahmed.splat.game;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ahmed on 12/12/17.
 */

public class SpawnEnemyTask extends TimerTask {
    private final int intermediateDelayMs;
    private final Timer spawnTimer;
    private final LocationTracker locationTracker;
    private final int minDelay;
    private final int maxDelay;

    public SpawnEnemyTask(int intermediateDelayMs, Timer spawnTimer, LocationTracker locationTracker, int minDelay, int maxDelay) {
        this.intermediateDelayMs = intermediateDelayMs;
        this.spawnTimer = spawnTimer;
        this.locationTracker = locationTracker;
        this.minDelay = minDelay;
        this.maxDelay = maxDelay;
    }

    @Override
    public void run() {
        float xLocation = (float) (-0.7 + (Math.random() * (0.7 + 0.7)));
        locationTracker.addEnemy(xLocation);

        long nextEnemyAfter = (long) (intermediateDelayMs + (Math.random() * (maxDelay - minDelay)));
        spawnTimer.schedule(
                new SpawnEnemyTask(intermediateDelayMs, spawnTimer, locationTracker,
                        minDelay, maxDelay)
                , nextEnemyAfter);
    }
}
