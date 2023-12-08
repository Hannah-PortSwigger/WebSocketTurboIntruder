package burp;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class WebSocketFuzzerFrames
{
    private final List<JFrame> frames = new ArrayList<>();

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
    }
}
