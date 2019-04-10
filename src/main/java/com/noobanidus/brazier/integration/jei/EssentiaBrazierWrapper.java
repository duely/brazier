package com.noobanidus.brazier.integration.jei;

import com.noobanidus.brazier.Brazier;
import com.noobanidus.brazier.BrazierConfig;
import com.noobanidus.brazier.util.AspectUtil;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.items.consumables.ItemPhial;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EssentiaBrazierWrapper implements IRecipeWrapper {
    private final int containerType;
    private final int tier;

    public String burnTime () {
        if (containerType == 1) {
            return String.format("%.1fs/10 points", ((BrazierConfig.cookTime / (double) BrazierConfig.essentiaCount) * 10) / 20);
        } else {
            return String.format("%.1fs/1 point", ((BrazierConfig.cookTime / (double) BrazierConfig.essentiaCount)) / 20);
        }
    }

    public String bonusTime () {
        if (tier == 0) return "";

        return String.format("%.1fs bonus per point", (((BrazierConfig.cookTime / (double) BrazierConfig.essentiaCount) * BrazierConfig.complexBonus) * tier) / 20);
    }

    public EssentiaBrazierWrapper(BrazierRecipe recipe) {
        this.containerType = recipe.containerType;
        this.tier = recipe.tier;
    }

    public List<ItemStack> getContainerInputs () {
        List<Aspect> aspects = AspectUtil.getTier(this.tier);
        IEssentiaContainerItem setter = (IEssentiaContainerItem) ItemsTC.phial;
        ItemStack container;

        switch (containerType) {
            case 0:
                container = new ItemStack(ItemsTC.crystalEssence);
                break;
            case 1:
                container = new ItemStack(ItemsTC.phial);
                break;
            case 2:
                container = new ItemStack(BlocksTC.jarNormal);
                break;
            default:
                throw new IllegalArgumentException("Tier must be 0-3");
        }

        List<ItemStack> containers = new ArrayList<>();

        for (Aspect aspect : aspects) {
            ItemStack copy = container.copy();
            if (containerType == 1) {
                containers.add(ItemPhial.makeFilledPhial(aspect));
            } else {
                AspectList list = new AspectList();
                list.add(aspect, 1);
                setter.setAspects(copy, list);
                containers.add(copy);
            }
        }

        return containers;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        List<ItemStack> containers = getContainerInputs();
        ingredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(containers));
        ingredients.setOutput(VanillaTypes.ITEM, new ItemStack(ItemsTC.curio, 1, BrazierConfig.meta));
    }

    @Override
    public void drawInfo(Minecraft mc, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        String string1 = burnTime();
        String string2 = bonusTime();

        int width = mc.fontRenderer.getStringWidth(string1);
        int x = (recipeWidth - width) / 2;
        int y = 56;

        mc.fontRenderer.drawString(string1, x, y, Color.GRAY.getRGB());

        if (!string2.isEmpty()) {
            width = mc.fontRenderer.getStringWidth(string2);
            x = (recipeWidth - width) / 2;
            y = 65;
            mc.fontRenderer.drawString(string2, x, y, Color.GRAY.getRGB());
        }

        String string3 = I18n.format("gui.brazier.jei.time_total",BrazierConfig.cookTime / 20.0d);
        width = mc.fontRenderer.getStringWidth(string3);
        x = (recipeWidth - width) / 2;
        y = 10;
        mc.fontRenderer.drawString(string3, x, y, Color.GRAY.getRGB());
    }
}
