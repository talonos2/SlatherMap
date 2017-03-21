# SlatherMap
A minecraft mod that will let you generate maps by painting heightmaps and biomes.

To use:

 - Place this in your mods folder.
 - Create a new folder in your modpack directory called "mapImages"
 - In there, place two images: "heightmap.png" and "biomemap.png"
   - heightmap.png should be a single channel (grayscale) png. The channel's magnitude is the height of the map at that point. (Whiter = higher)
   - biomemap.png is more complicated. Find the Hex code for the biomeID you want. Take the first letter and multiply it by 16: That's what number should be in the green channel. The second letter * 16 is what should be in the red channel. You can put whatever you want in the blue channel. This will be used as a lookup by the mod to determine the biomes.
 - The upper-right corner of the images represent Minecraft coords 0,0. Your map will extend out into the positive X and Z directions.
 - Anything outside your map will be ocean biome with a height of 20.
 - Why is this better than Worldpainter? Because worldpainter doesn't change the biomeGenBase of the map, it only overwrites the biomes. This means if you leave chunks undecorated, Minecraft will populate them with incongruous features (villages in oceans, ocean monuments hidden under deserts, etc.) Also, Worldpainter's cave generation algorithm is junk.
 - Why is this better than TerrainControl? Because TerrainControl does not handle topography well, and it can sometimes clobber other mod decorators and populaters.
 - Why is this so hard to use? Because I made it for me, and it works for me. :P If you want to make it better, for general use, fork it and do so. This is LGPL.