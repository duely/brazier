package com.noobanidus.brazier.util;

import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.common.items.consumables.ItemPhial;

import java.util.Arrays;
import java.util.List;

public class AspectUtil {
    public static List<Aspect> tier0 = Arrays.asList(Aspect.AIR,Aspect.ORDER,Aspect.EARTH,Aspect.FIRE,Aspect.WATER,Aspect.ENTROPY);
    public static List<Aspect> tier1 = Arrays.asList(Aspect.LIFE,Aspect.DEATH,Aspect.LIGHT,Aspect.VOID,Aspect.COLD,Aspect.MOTION,Aspect.EXCHANGE,Aspect.ENERGY,Aspect.METAL,Aspect.CRYSTAL);
    public static List<Aspect> tier2 = Arrays.asList(Aspect.FLIGHT,Aspect.MAGIC,Aspect.PLANT,Aspect.TRAP);
    public static List<Aspect> tier3 = Arrays.asList(Aspect.ALCHEMY,Aspect.BEAST,Aspect.DARKNESS,Aspect.SOUL,Aspect.FLUX,Aspect.AURA,Aspect.TOOL,Aspect.UNDEAD);
    public static List<Aspect> tier4 = Arrays.asList(Aspect.SENSES,Aspect.PROTECT,Aspect.MIND,Aspect.AVERSION);
    public static List<Aspect> tier5 = Arrays.asList(Aspect.MECHANISM,Aspect.DESIRE,Aspect.CRAFT,Aspect.ELDRITCH,Aspect.MAN);

    public static List<Aspect> getTier (int tier) {
        switch (tier) {
            case 0: return tier0;
            case 1: return tier1;
            case 2: return tier2;
            case 3: return tier3;
            case 4: return tier4;
            case 5: return tier5;
            default:
                return null;
        }
    }

    public static int complexity (Aspect aspect) {
        if (tier0.contains(aspect)) return 0;
        else if (tier1.contains(aspect)) return 1;
        else if (tier2.contains(aspect)) return 2;
        else if (tier3.contains(aspect)) return 3;
        else if (tier4.contains(aspect)) return 4;
        else if (tier5.contains(aspect)) return 5;
        else return -1;
    }

    public static boolean valid (ItemStack stack) {
        return stack.getItem() instanceof IEssentiaContainerItem;
    }
}
