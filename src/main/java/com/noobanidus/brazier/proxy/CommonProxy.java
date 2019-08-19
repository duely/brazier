package com.noobanidus.brazier.proxy;

import com.noobanidus.brazier.Brazier;
import com.noobanidus.brazier.init.Registrar;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.ShapedArcaneRecipe;

public class CommonProxy implements ISidedProxy {
	public void preInit (FMLPreInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(Brazier.instance, Brazier.GUI_HANDLER);
	}

	public void init (FMLInitializationEvent event) {
		ThaumcraftApi.registerResearchLocation(new ResourceLocation(Brazier.MODID, "research/brazier"));
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(Brazier.MODID, "brazier"), new ShapedArcaneRecipe(new ResourceLocation(Brazier.MODID, "brazier"), "BRAZIER", 50, new AspectList().add(Aspect.ORDER, 10).add(Aspect.FIRE, 10).add(Aspect.EARTH, 5), new ItemStack(Registrar.Items.brazier), "AIA", "IFI", "AIA", 'A', BlocksTC.stoneArcane, 'I', "plateIron", 'F', Blocks.FURNACE));
	}

	public void postInit (FMLPostInitializationEvent event) {
	}

	public void loadComplete (FMLLoadCompleteEvent event) {
		Brazier.LOG.info("Load Complete.");
	}

	public void serverStarting (FMLServerStartingEvent event) {
	}

	public void serverStarted (FMLServerStartedEvent event) {
	}
}
