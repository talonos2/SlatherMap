package me.talonos.slathermap.world;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import me.talonos.slathermap.FUNCS;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.gen.ChunkProviderOverworld;

public class ChunkProviderImageBased extends ChunkProviderOverworld 
{

	private short[][] heightMapArray;
    private IBlockState WATER = Blocks.WATER.getDefaultState();

	public ChunkProviderImageBased(World worldIn, long seed, boolean mapFeaturesEnabledIn, String chunkProviderSettingsJSONIn) 
	{
		super(worldIn, seed, mapFeaturesEnabledIn, chunkProviderSettingsJSONIn);
		this.heightMapArray = heightMapFromGrayscaleImage(FUNCS.loadImage("MapImages/heightmap.png"));
	}
	
    private short[][] heightMapFromGrayscaleImage(BufferedImage imageToOperate) 
    {
    	//We could make this more memory efficient in large worlds by making this a byte, but
    	//I don't want to have to deal with converting signed bytes to unsigned and vice versa.
    	short[][] toReturn = new short[imageToOperate.getWidth(null)][imageToOperate.getHeight(null)];
    	WritableRaster r = imageToOperate.getRaster();
    	for (int z = 0; z < toReturn[0].length; z++)
    	{
    		for (int x = 0; x < toReturn.length; x++)
    		{
    			toReturn[x][z]=(short)r.getSample(x, z, 0);
    		}
    	}
		return toReturn;
	}
    
	public void setBlocksInChunk(int x, int z, ChunkPrimer primer)
    {
		
        for (int z1 = 0; z1 < 16; ++z1)
        {
            for (int x1 = 0; x1 < 16; ++x1)
            {
            	int realX = x*16+x1; 
            	int realZ = z*16+z1;
            	int height = 20;
            	if (realX < 0 || realZ < 0 || realX >= heightMapArray.length || realZ >= heightMapArray[0].length)
            	{
            	}
            	else
            	{
            		height = heightMapArray[realX][realZ];
            	}
            	
            	int toFill = Math.max(height, 63); //Should be this.settings.sealevel, but that's not visible...?
                for (int y1 = 0; y1 < toFill; ++y1)
                {
                	if (y1 > height)
                	{
                		primer.setBlockState(x1, y1, z1, WATER);                		
                	}
                	else
                	{
                		primer.setBlockState(x1, y1, z1, STONE);
                	}
                }
            }
        }
    }
}
