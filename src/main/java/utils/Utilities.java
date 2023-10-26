package utils;

import javax.swing.*;
import java.util.List;

public class Utilities
{
    public static void closeAllFrames(List<JFrame> frameList)
    {
        for (JFrame frame : frameList)
        {
            frame.dispose();
        }
    }
}
