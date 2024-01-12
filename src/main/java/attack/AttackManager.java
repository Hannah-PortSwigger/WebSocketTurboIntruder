package attack;

import java.util.concurrent.atomic.AtomicBoolean;

public class AttackManager implements AttackStatus
{
    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    public void start()
    {
        isRunning.set(true);
    }

    public void stop()
    {
        isRunning.set(false);
    }

    @Override
    public boolean isRunning()
    {
        return isRunning.get();
    }
}
