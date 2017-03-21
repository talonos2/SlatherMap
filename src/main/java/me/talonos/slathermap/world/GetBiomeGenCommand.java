package me.talonos.slathermap.world;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import me.talonos.slathermap.SlatherMapMod;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class GetBiomeGenCommand extends CommandBase {
	
    private final List<String> aliases;

    public GetBiomeGenCommand(){
        aliases = Lists.newArrayList(SlatherMapMod.MODID, "sl-getBiomes", "sl-getbiomes");
    }

    @Override
    @Nonnull
    public String getCommandName() {
        return "sl-getBiomes";
    }

    @Override
    @Nonnull
    public String getCommandUsage(@Nonnull ICommandSender sender) {
        return "sl-getBiomes";
    }

    @Override
    @Nonnull
    public List<String> getCommandAliases() {
        return aliases;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException 
    {
    	Biome[] biomes = sender.getEntityWorld().getBiomeProvider().getBiomesForGeneration(null, sender.getPosition().getX(), sender.getPosition().getZ(), 5, 5);
    	for (Biome b : biomes)
    	{
    		System.out.println(b.getBiomeName());
    	}
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    @Nonnull
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        return Collections.emptyList();
    }
}