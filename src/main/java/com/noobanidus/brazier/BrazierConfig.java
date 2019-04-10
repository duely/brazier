package com.noobanidus.brazier;

import net.minecraftforge.common.config.Config;

@Config(modid = Brazier.MODID)
public class BrazierConfig {
    @Config.Comment("Specify the number of essentia required to gain a curiosity")
    @Config.Name("Essentia per curiosity")
    @Config.RangeInt(min = 1)
    public static int essentiaCount = 150;

    @Config.Comment("Specify the multiplier to be applied for complex essentia, rounded down")
    @Config.Name("Essentia bonus")
    @Config.RangeDouble(min = 0)
    public static double complexBonus = 0.15;

    @Config.Comment("Total time it takes to grow a curiosity in ticks")
    @Config.Name("Total cook time")
    public static int cookTime = 6000;

    @Config.Comment("Meta value for curio (0-5)")
    @Config.Name("Curio meta")
    @Config.RangeInt(min=1, max=5)
    public static int meta;
}
