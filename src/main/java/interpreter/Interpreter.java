package interpreter;

import logger.Logger;
import org.python.util.PythonInterpreter;

public class Interpreter
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
}
