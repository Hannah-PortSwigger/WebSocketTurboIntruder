package utils;

import config.FileLocationConfiguration;
import ui.editor.WebSocketEditorPanel;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class ScriptLoader
{
    private final FileLocationConfiguration fileLocationConfiguration;

    public ScriptLoader(FileLocationConfiguration fileLocationConfiguration)
    {
        this.fileLocationConfiguration = fileLocationConfiguration;
    }

    public List<Path> getPathList()
    {
        String websocketScriptsPath = fileLocationConfiguration.getWebSocketScriptPath();
        List<Path> pathList = new ArrayList<>();

        if (fileLocationConfiguration.isDefault())
        {
            URL url = WebSocketEditorPanel.class.getResource(websocketScriptsPath);
            if (url != null)
            {
                Stream<Path> stream  = null;
                try
                {
                    URI uri = url.toURI();

                    try (FileSystem fs = FileSystems.newFileSystem(uri, new HashMap<>())) {
                        stream = Files.walk(Paths.get(uri));

                        stream.forEach(path -> {
                            if (path.toString().endsWith(".py"))
                            {
                                pathList.add(path);
                            }
                        });
                    }
                } catch (IOException | URISyntaxException e)
                {
                    throw new RuntimeException(e);
                } finally
                {
                    if (stream != null)
                    {
                        stream.close();
                    }
                }
            }
        }
        else
        {
            try (Stream<Path> stream = Files.walk(Paths.get(websocketScriptsPath)))
            {
                stream.forEach(path -> {
                    if (path.toString().endsWith(".py"))
                    {
                        pathList.add(path);
                    }
                });
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return pathList;
    }
}
