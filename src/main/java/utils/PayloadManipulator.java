package utils;

public class PayloadManipulator
{
    public String modify(String payload, String replacement)
    {
        if (payload == null)
        {
            return "";
        }

        return replacement == null ? payload : payload.replace("%s", replacement);
    }
}
