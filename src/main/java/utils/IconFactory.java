package utils;

import burp.api.montoya.ui.UserInterface;

import javax.swing.*;

import static java.awt.Image.SCALE_SMOOTH;

public class IconFactory
{
    private final UserInterface userInterface;

    public IconFactory(UserInterface userInterface)
    {
        this.userInterface = userInterface;
    }

    public Icon scaledIconFor(IconType iconType, int size)
    {
        return new ImageIcon(iconType.image(userInterface.currentTheme()).getScaledInstance(size, size, SCALE_SMOOTH));
    }
}