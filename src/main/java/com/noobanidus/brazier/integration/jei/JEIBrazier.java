package com.noobanidus.brazier.integration.jei;

import com.noobanidus.brazier.Brazier;
import mezz.jei.api.*;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;

import java.util.ArrayList;
import java.util.List;

@JEIPlugin
public class JEIBrazier implements IModPlugin {
    public static final String ESSENTIA_BURNER = Brazier.MODID + ".brazier";

    public IJeiRuntime runtime;

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper helper = registry.getJeiHelpers().getGuiHelper();
        registry.addRecipeCategories(new EssentiaBrazierCategory(helper));
    }

    @Override
    public void register(IModRegistry registry) {
        registry.handleRecipes(BrazierRecipe.class, EssentiaBrazierWrapper::new, ESSENTIA_BURNER);

        List<BrazierRecipe> recipes = new ArrayList<>();

        for (int i = 0; i < 3; i++) { // Types
            for (int j = 0; j < 6; j++) { // Essentia categories
                recipes.add(new BrazierRecipe(i, j));
            }
        }

        registry.addRecipes(recipes, ESSENTIA_BURNER);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        this.runtime = jeiRuntime;
    }
}
