package com.noobanidus.brazier.gui;

import com.noobanidus.brazier.tiles.TileEntityBrazier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {
    public static final int BRAZIER = 1;

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == BRAZIER) {
            BlockPos pos = new BlockPos(x, y, z);
            return new ContainerBrazier(player, world, world.getTileEntity(pos), pos);
        }

        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == BRAZIER) {
            BlockPos pos = new BlockPos(x, y, z);
            TileEntityBrazier tile = (TileEntityBrazier) world.getTileEntity(pos);
            ContainerBrazier container = new ContainerBrazier(player, world, tile, pos);
            return new GuiBrazier(player, container, world, pos, tile);
        }

        return null;
    }
}
