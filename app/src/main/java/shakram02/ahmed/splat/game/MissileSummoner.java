package shakram02.ahmed.splat.game;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Summons missiles at bounded random intervals
 */

public class MissileSummoner {
    private Timer timer;
    private final int minDelay;
    private final int maxDelay;
    private final LocationTracker locationTracker;
    private boolean isRunning;
    private static int INTERMEDIATE_DELAY_MS = 10;

    public MissileSummoner(int minDelay, int maxDelay, LocationTracker locationTracker) {
        this.minDelay = minDelay;
        this.maxDelay = maxDelay;
        this.locationTracker = locationTracker;

    }

    // TODO use locking or whatever to release a lock somewhere to fire the missile
    public void run() {
        if (isRunning) return;
        isRunning = true;

        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timer.schedule(new SpawnEnemyTask(INTERMEDIATE_DELAY_MS, timer, locationTracker,
                        minDelay, maxDelay), INTERMEDIATE_DELAY_MS);
            }
        }, INTERMEDIATE_DELAY_MS);
    }

    public void stop() {
        if (!isRunning) return;
        isRunning = false;

        timer.cancel();
    }

}
