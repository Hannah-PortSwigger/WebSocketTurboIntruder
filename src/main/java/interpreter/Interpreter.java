package interpreter;

import logger.Logger;
import org.python.util.PythonInterpreter;

import java.io.Closeable;

public class Interpreter implements Closeable
{
    private final PythonInterpreter interpreter;

    public Interpreter(Logger logger)
    {
        interpreter = new PythonInterpreter();

        interpreter.setOut(logger.outputStream());
        interpreter.setErr(logger.errorStream());
    }

    public void setVariable(String name, Object value)
    {
        interpreter.set(name, value);
    }

    public void execute(String code)
    {
        interpreter.exec(code);
    }

    @Override
    public void close()
    {
        interpreter.close();
    }
}
