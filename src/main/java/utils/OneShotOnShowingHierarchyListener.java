package utils;

import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;

import static java.awt.event.HierarchyEvent.SHOWING_CHANGED;

public class OneShotOnShowingHierarchyListener implements HierarchyListener
{
    private final Runnable runnable;

    public OneShotOnShowingHierarchyListener(Runnable runnable)
    {
        this.runnable = runnable;
    }

    @Override
    public void hierarchyChanged(HierarchyEvent e)
    {
        if (e.getChangeFlags() != SHOWING_CHANGED || !e.getComponent().isShowing())
        {
            return;
        }

        runnable.run();

        e.getComponent().removeHierarchyListener(this);
    }
}
