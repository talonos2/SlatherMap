package me.talonos.slathermap;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class FUNCS 
{
    public static BufferedImage loadImage(String fileName)
    {
        System.out.println(fileName);
        File checkIfItsHere = new File(fileName);
        if (!checkIfItsHere.exists())
        {
            System.err.println("Warning! Missing Resource: "+fileName);
            System.err.println("Expecting it to be at: "+checkIfItsHere.getAbsolutePath());
        }
        try 
        { 
        	BufferedImage toReturn = ImageIO.read(checkIfItsHere); 
        	return toReturn;
        } 
        catch (IOException e) 
        { 
        	System.err.println("Warning! Exception Getting: "+fileName); 
        }
        return new BufferedImage(BufferedImage.TYPE_BYTE_GRAY, 1, 1);
    }
}
