package burp;

import burp.api.montoya.extension.ExtensionUnloadingHandler;

import javax.swing.*;
import java.util.List;

import static utils.Utilities.closeAllFrames;

public class WebSocketExtensionUnloadingHandler implements ExtensionUnloadingHandler
{
    private final List<JFrame> frameList;

    public WebSocketExtensionUnloadingHandler(List<JFrame> frameList)
    {
        this.frameList = frameList;
    }

    @Override
    public void extensionUnloaded()
    {
        closeAllFrames(frameList);
    }
}
