package me.talonos.slathermap.world;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.IChunkGenerator;

public class ImageBasedWorldType extends WorldType {

	public ImageBasedWorldType() 
	{
		super("imageBasedWorld");
	}
	
    @Override
    public BiomeProvider getBiomeProvider(World world)
    {
        {
            return new ImageBasedBiomeProvider(world.getWorldInfo());
        }
    }
    
    @Override
    public IChunkGenerator getChunkGenerator(World world, String generatorOptions)
    {
        return new ChunkProviderImageBased(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled(), generatorOptions);
    }

}
