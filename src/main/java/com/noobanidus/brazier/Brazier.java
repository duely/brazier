package com.noobanidus.brazier;

import com.noobanidus.brazier.gui.GuiHandler;
import com.noobanidus.brazier.init.Registrar;
import com.noobanidus.brazier.proxy.ISidedProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber
@Mod(modid = Brazier.MODID, name = Brazier.MODNAME, version = Brazier.VERSION, dependencies = Brazier.DEPENDS)
@SuppressWarnings("WeakerAccess")
public class Brazier {
    public static final String MODID = "brazier";
    public static final String MODNAME = "Essentia Brazier";
    public static final String VERSION = "GRADLE:VERSION";
    public static final String DEPENDS = "required-after:thaumcraft;";

    @SuppressWarnings("unused")
    public final static Logger LOG = LogManager.getLogger(MODID);

    @Mod.Instance(Brazier.MODID)
    public static Brazier instance;

    @SidedProxy(clientSide = "com.noobanidus.brazier.proxy.ClientProxy", modId = MODID, serverSide = "com.noobanidus.brazier.proxy.CommonProxy")
    public static ISidedProxy proxy;

    public static GuiHandler GUI_HANDLER = new GuiHandler();

    public static CreativeTabs tab = new CreativeTabs(MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Registrar.Blocks.brazier);
        }
    };

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        proxy.loadComplete(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }

    @Mod.EventHandler
    public void serverStarted(FMLServerStartedEvent event) {
        proxy.serverStarted(event);
    }

}
