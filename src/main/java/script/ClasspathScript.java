package script;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

class ClasspathScript implements Script
{
    private final Path path;

    ClasspathScript(Path path)
    {
        this.path = path;
    }

    @Override
    public String content()
    {
        try (InputStream stream = this.getClass().getResourceAsStream(path.toString()))
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

    @Override
    public String toString()
    {
        return path.toString();
    }
}
