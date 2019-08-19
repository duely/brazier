package com.noobanidus.brazier.integration.jei;

// Only used for JEI
public class BrazierRecipe {
	public int containerType; // 0-3
	public int tier; // 0-5

	public BrazierRecipe (int containerType, int tier) {
		this.containerType = containerType;
		this.tier = tier;
	}
}
