package com.noobanidus.brazier.gui;

import com.noobanidus.brazier.Brazier;
import com.noobanidus.brazier.BrazierConfig;
import com.noobanidus.brazier.init.Registrar;
import com.noobanidus.brazier.tiles.TileEntityBrazier;
import com.noobanidus.brazier.util.AspectUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;

import java.util.ArrayList;
import java.util.List;

public class GuiBrazier extends GuiContainer {
    private static final ResourceLocation FURNACE_GUI_TEXTURES = new ResourceLocation(Brazier.MODID, "textures/gui/brazier.png");

    private World world;
    private BlockPos pos;
    private TileEntityBrazier tile;
    private IInventory playerInventory;
    private ContainerBrazier container;

    public GuiBrazier(EntityPlayer player, Container inventorySlotsIn, World world, BlockPos pos, TileEntityBrazier tile) {
        super(inventorySlotsIn);
        this.world = world;
        this.pos = pos;
        this.tile = tile;
        this.playerInventory = player.inventory;
        this.container = (ContainerBrazier) inventorySlotsIn;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String s = I18n.format(Registrar.Blocks.brazier.getTranslationKey() + ".name");
        this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
        this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(FURNACE_GUI_TEXTURES);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);

        if (tile.getBurnTime() != 0)
        {
            int k = this.getBurnLeftScaled(13);
            this.drawTexturedModalRect(i + 36, j + 36 + 12 - k, 176, 12 - k, 14, k + 1);
        }

        int l = this.getCookProgressScaled(24);
        this.drawTexturedModalRect(i + 79, j + 34, 176, 14, l + 1, 16);
    }

    @Override
    public List<String> getItemToolTip(ItemStack stack) {
        List<String> lines = super.getItemToolTip(stack);

        if (stack.getItem() instanceof IEssentiaContainerItem) {
            double bonusTicks = ((BrazierConfig.cookTime / (double) BrazierConfig.essentiaCount) * BrazierConfig.complexBonus) / 20;
            IEssentiaContainerItem item = (IEssentiaContainerItem) stack.getItem();
            AspectList aspects = item.getAspects(stack);
            if (aspects != null) {
                Aspect[] aspectList = aspects.getAspects();
                if (aspectList.length != 0) {
                    if (AspectUtil.complexity(aspectList[0]) != 0) {
                        bonusTicks *= AspectUtil.complexity(aspectList[0]);
                        lines.add("");
                        lines.add(TextFormatting.LIGHT_PURPLE + I18n.format("gui.brazier.added_bonus", bonusTicks));
                    }
                }
            }
        }

        return lines;
    }

    @Override
    protected void renderHoveredToolTip(int x, int y) {
        super.renderHoveredToolTip(x, y);

        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;

        if (x >= i + 35 && x <= i + 36 + 14) {
            if (y >= j + 36 && y <= j + 36 + 14) {
                // Burn time left
                if (this.tile.getField(0) != 0) {
                    double progress = this.tile.getField(0) / 20.0;
                    drawHoveringText(String.format("%.1fs", progress), x, y);
                }
            }
        } else if (x >= i + 79 && x <= i + 79 + 25) {
            if (y >= j + 34 && y <= j + 35 + 16) {
                i = this.tile.getField(2);
                j = this.tile.getField(3);
                if (i != 0 && j != 0) {
                    List<String> lines = new ArrayList<>();
                    lines.add(i * 100 / j + "%");
                    lines.add(String.format("%.1fs", (j - (double) i) / 20));
                    drawHoveringText(lines, x, y);
                }
            }
        }
    }

    private int getCookProgressScaled(int pixels)
    {
        int i = this.tile.getField(2);
        int j = this.tile.getField(3);
        return j != 0 && i != 0 ? i * pixels / j : 0;
    }

    private int getBurnLeftScaled(int pixels)
    {
        int i = this.tile.getField(1);

        if (i == 0)
        {
            i = BrazierConfig.cookTime / BrazierConfig.essentiaCount;
        }

        return this.tile.getField(0) * pixels / i;
    }
}
