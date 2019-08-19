package com.noobanidus.brazier.init;

import com.noobanidus.brazier.Brazier;
import com.noobanidus.brazier.blocks.BlockBrazier;
import com.noobanidus.brazier.tiles.TileEntityBrazier;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = Brazier.MODID)
public class Registrar {
	public static class Blocks {
		public static Block brazier, lit_brazier;
	}

	public static class Items {
		public static Item brazier;
	}

	@SubscribeEvent
	public static void onBlockRegister (RegistryEvent.Register<Block> event) {
		event.getRegistry().register(Blocks.brazier = new BlockBrazier("brazier", Material.ROCK, false).setCreativeTab(Brazier.tab));
		event.getRegistry().register(Blocks.lit_brazier = new BlockBrazier("lit_brazier", Material.ROCK, true).setLightLevel(0.9f).setCreativeTab(Brazier.tab));

		GameRegistry.registerTileEntity(TileEntityBrazier.class, new ResourceLocation(Brazier.MODID, "brazier"));
	}

	@SubscribeEvent
	public static void onItemRegister (RegistryEvent.Register<Item> event) {
		event.getRegistry().register(Items.brazier = new ItemBlock(Blocks.brazier).setRegistryName(Blocks.brazier.getRegistryName()).setCreativeTab(Brazier.tab));
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onModelRegister (ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(Items.brazier, 0, new ModelResourceLocation(Items.brazier.getRegistryName(), "inventory"));
	}
}
