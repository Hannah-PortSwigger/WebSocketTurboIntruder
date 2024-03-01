package script;

import ui.editor.WebSocketEditorPanel;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Script
{
    private final Path path;
    private final boolean isDefaultFilePath;

    public Script(Path path, boolean isDefaultFilePath)
    {
        this.path = path;
        this.isDefaultFilePath = isDefaultFilePath;
    }

    public String content()
    {
        if (isDefaultFilePath)
        {
            try (InputStream stream = WebSocketEditorPanel.class.getResourceAsStream(path.toString()))
            {
                if (stream == null)
                {
                    throw new IllegalStateException("Failed to retrieve Script content.");
                }

                return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            }
            catch (IOException e)
            {
                throw new UncheckedIOException(e);
            }
        }

        try
        {
            return Files.readString(path);
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public String toString()
    {
        return path.toString();
    }
}
