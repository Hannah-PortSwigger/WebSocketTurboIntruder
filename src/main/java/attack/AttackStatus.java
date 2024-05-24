package attack;

public interface AttackStatus
{
    boolean isRunning();

    boolean isCurrentAttackId(int attackId);

    int currentAttackId();
}
