package ui.editor;

import attack.AttackStarter;
import data.AttackDetails;
import ui.PanelSwitcher;

public class WebSocketEditorController
{
    private final AttackStarter attackStarter;
    private final PanelSwitcher panelSwitcher;

    public WebSocketEditorController(AttackStarter attackStarter, PanelSwitcher panelSwitcher)
    {
        this.attackStarter = attackStarter;
        this.panelSwitcher = panelSwitcher;
    }

    public void startAttack(AttackDetails attackDetails)
    {
        attackStarter.startAttack(attackDetails);

        panelSwitcher.showAttackPanel();
    }
}
