package ui.editor;

import attack.AttackStarter;
import config.FileLocationConfiguration;
import data.AttackDetails;
import script.Script;
import script.ScriptLoaderFacade;
import ui.PanelSwitcher;

import java.io.File;
import java.util.List;

public class WebSocketEditorController
{
    private final AttackStarter attackStarter;
    private final PanelSwitcher panelSwitcher;
    private final FileLocationConfiguration fileLocationConfiguration;
    private final ScriptLoaderFacade scriptLoader;

    public WebSocketEditorController(AttackStarter attackStarter, PanelSwitcher panelSwitcher, FileLocationConfiguration fileLocationConfiguration, ScriptLoaderFacade scriptLoader)
    {
        this.attackStarter = attackStarter;
        this.panelSwitcher = panelSwitcher;
        this.fileLocationConfiguration = fileLocationConfiguration;
        this.scriptLoader = scriptLoader;
    }

    public void startAttack(AttackDetails attackDetails)
    {
        attackStarter.startAttack(attackDetails);

        panelSwitcher.showAttackPanel();
    }

    public Script[] loadScripts(File file)
    {
        fileLocationConfiguration.setWebSocketScriptPath(file.getAbsolutePath());

        return loadScripts();
    }

    public Script[] loadScripts()
    {
        List<Script> scriptList = scriptLoader.loadScripts();

        return scriptList.toArray(new Script[0]);
    }
}
