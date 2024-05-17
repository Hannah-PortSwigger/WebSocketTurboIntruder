package attack;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class AttackState implements AttackStatus
{
    private final AtomicBoolean isRunning;
    private final AtomicInteger attackId;

    public AttackState()
    {
        isRunning = new AtomicBoolean();
        attackId = new AtomicInteger();
    }

    @Override
    public boolean isRunning()
    {
        return isRunning.get();
    }

    @Override
    public boolean isCurrentAttackId(int attackId)
    {
        return this.attackId.get() == attackId;
    }

    @Override
    public int currentAttackId()
    {
        return attackId.get();
    }

    void newAttack()
    {
        isRunning.set(true);
        attackId.incrementAndGet();
    }

    void endAttack()
    {
        isRunning.set(false);
    }
}
