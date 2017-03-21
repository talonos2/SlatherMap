package me.talonos.slathermap.world;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.List;

import javax.annotation.Nullable;

import me.talonos.slathermap.FUNCS;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.init.Biomes;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.layer.IntCache;
import net.minecraft.world.storage.WorldInfo;

/**
 * This is a biome provider that reads from an image in your gameplay folder to
 * provide biomes, ignoring the genBiomes entirely. All methods that query
 * the genBiomes member are overridden
 * @author Brennan
 *
 */
public class ImageBasedBiomeProvider extends BiomeProvider 
{

	public ImageBasedBiomeProvider(WorldInfo worldInfo) 
	{
		super(worldInfo);
		this.biomeArray = biomesFromColorImage(FUNCS.loadImage("MapImages/biomeMap.png"));
	}
	
	private short[][] biomeArray;
	
	/**
	 * Generates a two-dimensional array of shorts that represent biome IDs on a per-block
	 * basis.
	 * @param imageToOperate the image to read in.
	 * @return an array of BiomeIDs.
	 */
    private short[][] biomesFromColorImage(BufferedImage imageToOperate) 
    {
    	//We could make this more memory efficient in large worlds by making this a byte, but
    	//I don't want to have to deal with converting signed bytes to unsigned and vice versa.
    	short[][] toReturn = new short[imageToOperate.getWidth(null)][imageToOperate.getHeight(null)];
    	WritableRaster r = imageToOperate.getRaster();
    	for (int z = 0; z < toReturn[0].length; z++)
    	{
    		for (int x = 0; x < toReturn.length; x++)
    		{
    			short biome = (short)((r.getSample(x, z, 0)/16) + (short)((r.getSample(x, z, 1)/16)*16));
    			toReturn[x][z]=biome;
    		}
    	}
		return toReturn;
	}
	
    /**
     * Modified Vanilla method: Gets a list of biomes for the specified blocks.
     */
    @Override
    public Biome[] getBiomes(@Nullable Biome[] listToReuse, int x, int z, int width, int length, boolean cacheFlag)
    {
        IntCache.resetIntCache();

        if (listToReuse == null || listToReuse.length < width * length)
        {
            listToReuse = new Biome[width * length];
        }

        if (cacheFlag && width == 16 && length == 16 && (x & 15) == 0 && (z & 15) == 0)
        {
        	//Punt back to the BiomeProvider. There's some sort of biome caching scheme I
        	//don't understand, and this ensures we use it.
            return super.getBiomes(listToReuse, x, z, width, length, cacheFlag);
        }
        else
        {
        	//The most important change:
            int[] aint = this.getBiomesFromImage(x, z, width, length);

            for (int i = 0; i < width * length; ++i)
            {
                listToReuse[i] = Biome.getBiome(aint[i], Biomes.DEFAULT);
            }

            return listToReuse;
        }
    }

    /**
     * Gets a list of biomes in the vicinity of a given point.
     * 
     * This MIGHT be off by width/2 and height/2 blocks. Further testing is required.
     * @param x the x position of the queried block
     * @param z the z position of the queried block
     * @param width the width to query.
     * @param length the length to query.
     * @return an array of width*length BiomeID elements that represents a convolution
     * of a w*h identity kernel over the biome map. I'm still not sure why Minecraft
     * wants such data...
     */
	private int[] getBiomesFromImage(int x, int z, int width, int length) 
	{
		int[] toReturn= new int[width*length];
		for (int v = 0; v < length; ++v)
		{
			for (int u = 0; u < width; ++u)
			{
				int realX = x + u; int realZ = z + v;
				if (realX < 0 || realZ < 0 || realX >= biomeArray.length || realZ >= biomeArray.length)
				{
					toReturn[u+v*width] = Biome.getIdForBiome(Biomes.OCEAN);
				}
            	else
            	{
            		toReturn[u+v*width] = biomeArray[realX][realZ];
            	}
			}			
		}
		return toReturn;
	}
	
	//FYI, the forge source for BiomeProvider is *also* inconsistent as to whether the fifth argument is "height" or "length."
	//Also FYI, this method is never called by my code, but if some mod uses this *instead* of getViableBiomes (or so-forth)
	//then we cover that case. For example, Ethereal Blooms in 1.7.10 called this code, I think.
	/**
	 * Fetches the biomes as they would have been at generation time. Used for biome reversion, if needed.
	 */
	@Override
    public Biome[] getBiomesForGeneration(Biome[] biomes, int x, int z, int width, int height)
    {
        IntCache.resetIntCache();
        if (biomes == null || biomes.length < width * height)
        {
            biomes = new Biome[width * height];
        }

        //Most of this is Mojang/Forge's code. This is the only line I changed.  
        int[] aint = this.getBiomesFromImage(x, z, width, height);

        try
        {
            for (int i = 0; i < width * height; ++i)
            {
                biomes[i] = Biome.getBiome(aint[i], Biomes.DEFAULT);
            }

            return biomes;
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("RawBiomeBlock");
            crashreportcategory.addCrashSection("biomes[] size", Integer.valueOf(biomes.length));
            crashreportcategory.addCrashSection("x", Integer.valueOf(x));
            crashreportcategory.addCrashSection("z", Integer.valueOf(z));
            crashreportcategory.addCrashSection("w", Integer.valueOf(width));
            crashreportcategory.addCrashSection("h", Integer.valueOf(height));
            throw new ReportedException(crashreport);
        }
    }
    
    /**
     * Checks given Chunk's Biomes against List of allowed ones. Needs to be overridden to
     * provide support for some structure gen, such as villages.
     */
    @Override
    public boolean areBiomesViable(int x, int z, int radius, List<Biome> allowed)
    {
        IntCache.resetIntCache();
        int i = x - radius >> 2;
        int j = z - radius >> 2;
        int k = x + radius >> 2;
        int l = z + radius >> 2;
        int i1 = k - i + 1;
        int j1 = l - j + 1;
        
        //Most of this is Mojang/Forge's code. This is the only line I changed.  
        int[] aint = this.getBiomesFromImage(i, j, i1, j1);

        try
        {
            for (int k1 = 0; k1 < i1 * j1; ++k1)
            {
                Biome biome = Biome.getBiome(aint[k1]);

                if (!allowed.contains(biome))
                {
                    return false;
                }
            }

            return true;
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Layer");
            //line deleted here, because genBiomeLayer is private.
            crashreportcategory.addCrashSection("x", Integer.valueOf(x));
            crashreportcategory.addCrashSection("z", Integer.valueOf(z));
            crashreportcategory.addCrashSection("radius", Integer.valueOf(radius));
            crashreportcategory.addCrashSection("allowed", allowed);
            throw new ReportedException(crashreport);
        }
    }

}
