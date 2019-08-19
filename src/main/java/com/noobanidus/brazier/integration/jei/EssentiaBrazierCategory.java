package com.noobanidus.brazier.integration.jei;

import com.noobanidus.brazier.Brazier;
import com.noobanidus.brazier.BrazierConfig;
import com.noobanidus.brazier.init.Registrar;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.items.ItemsTC;

import javax.annotation.Nullable;

public class EssentiaBrazierCategory implements IRecipeCategory<EssentiaBrazierWrapper> {
	private final IDrawable background;
	private final IDrawable icon;

	public EssentiaBrazierCategory (IGuiHelper helper) {
		this.background = helper.createDrawable(new ResourceLocation(Brazier.MODID, "textures/gui/jei.png"), 0, 0, 160, 76);
		this.icon = helper.createDrawableIngredient(new ItemStack(Registrar.Items.brazier));
	}

	@Override
	public String getUid () {
		return JEIBrazier.ESSENTIA_BURNER;
	}

	@Override
	public String getTitle () {
		return I18n.format("tile.brazier.name");
	}

	@Override
	public String getModName () {
		return Brazier.MODNAME;
	}

	@Override
	public IDrawable getBackground () {
		return this.background;
	}

	@Nullable
	@Override
	public IDrawable getIcon () {
		return this.icon;
	}

	@Override
	public void setRecipe (IRecipeLayout recipeLayout, EssentiaBrazierWrapper recipeWrapper, IIngredients ingredients) {
		IGuiItemStackGroup group = recipeLayout.getItemStacks();
		group.init(0, true, 47, 29);
		group.set(0, recipeWrapper.getContainerInputs());
		group.init(1, false, 107, 29);
		group.set(1, new ItemStack(ItemsTC.curio, 1, BrazierConfig.meta));
	}
}
