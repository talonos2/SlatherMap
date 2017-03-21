package me.talonos.slathermap;

import me.talonos.slathermap.world.GetBiomeGenCommand;
import me.talonos.slathermap.world.ImageBasedWorldType;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = SlatherMapMod.MODID, version = SlatherMapMod.VERSION)
public class SlatherMapMod
{
    public static final String MODID = "slathermap";
    public static final String NAME = "Slather-Map";
    public static final String VERSION = "0.1";
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // some example code
        WorldType imageBased = new ImageBasedWorldType();
    }
    
    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) 
    {
        event.registerServerCommand(new GetBiomeGenCommand());
    }
}
