package burp;

import logger.Logger;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static burp.WebSocketFuzzer.EXTENSION_NAME;
import static logger.LoggerLevel.DEBUG;

public class WebSocketFuzzerFrames
{
    private final List<JFrame> frames;
    private final Logger logger;

    public WebSocketFuzzerFrames(Logger logger)
    {
        this.logger = logger;
        this.frames = new ArrayList<>();
    }

    public void add(JFrame frame)
    {
        frames.add(frame);
    }

    public void close()
    {
        frames.forEach(f -> {
            f.setVisible(false);
            f.dispose();
        });

        logger.logOutput(DEBUG, "All " + EXTENSION_NAME + " windows closed.");
    }
}
