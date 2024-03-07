package script;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

class FilepathScript implements Script
{
    private final Path path;

    FilepathScript(Path path)
    {
        this.path = path;
    }

    @Override
    public String content()
    {
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
