package com.noobanidus.brazier.gui;

import com.noobanidus.brazier.tiles.TileEntityBrazier;
import com.noobanidus.brazier.util.AspectUtil;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class ContainerBrazier extends Container {
    private World world;
    private BlockPos pos;
    private TileEntityBrazier tile;
    private TileWrapper wrapper;
    private ItemStackHandler inventory;

    // Stuff for the synchronisation of stuff
    public int progress = 0;
    public int modifiedDuration = 0;
    public int burnTime = 0;
    private int itemTime = 0;

    private Slot input;
    private Slot output;

    public ContainerBrazier(EntityPlayer player, World world, TileEntity tile, BlockPos pos) {
        super();
        this.world = world;
        this.pos = pos;
        this.tile = (TileEntityBrazier) tile;
        this.inventory = this.tile.getInventory();
        this.wrapper = new TileWrapper(this.tile);

        IInventory playerInventory = player.inventory;

        this.addSlotToContainer(new SlotItemHandler(inventory, 0, 56, 35) {
            @Override
            public boolean isItemValid(@Nonnull ItemStack stack) {
                return AspectUtil.valid(stack);
            }
        });
        this.addSlotToContainer(new SlotBrazierOutput(player, inventory, 1, 116, 35));

        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k)
        {
            this.addSlotToContainer(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
    }

    @Override
    public void addListener(IContainerListener listener)
    {
        super.addListener(listener);
        listener.sendAllWindowProperties(this, this.wrapper);
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (int i = 0; i < this.listeners.size(); ++i)
        {
            IContainerListener icontainerlistener = this.listeners.get(i);

            if (this.progress != this.tile.getField(2)) {
                icontainerlistener.sendWindowProperty(this, 2, this.tile.getField(2));
            }

            if (this.burnTime != this.tile.getField(2)) {
                icontainerlistener.sendWindowProperty(this, 0, this.tile.getField(0));
            }

            if (this.itemTime != this.tile.getField(1)) {
                icontainerlistener.sendWindowProperty(this, 1, this.tile.getField(1));
            }

            if (this.modifiedDuration != this.tile.getField(3)) {
                icontainerlistener.sendWindowProperty(this, 3, this.tile.getField(3));
            }
        }

        this.progress = this.tile.getField(2);
        this.burnTime = this.tile.getField(0);
        this.itemTime = this.tile.getField(1);
        this.modifiedDuration = this.tile.getField(3);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar(int id, int data)
    {
        this.tile.setField(id, data);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index <= 1)
            {
                if (!this.mergeItemStack(itemstack1, 2, 38, true))
                {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (!this.mergeItemStack(itemstack1, 0, 2, false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount())
            {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }

    public class SlotBrazierOutput extends SlotItemHandler
    {
        /** The player that is using the GUI where this slot resides. */
        private final EntityPlayer player;
        private int removeCount;

        public SlotBrazierOutput(EntityPlayer player, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
            this.player = player;
        }

        @Override
        public boolean isItemValid(ItemStack stack)
        {
            return false;
        }

        @Override
        public ItemStack decrStackSize(int amount)
        {
            if (this.getHasStack())
            {
                this.removeCount += Math.min(amount, this.getStack().getCount());
            }

            return super.decrStackSize(amount);
        }

        @Override
        public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack)
        {
            this.onCrafting(stack);
            super.onTake(thePlayer, stack);
            return stack;
        }

        @Override
        protected void onCrafting(ItemStack stack, int amount)
        {
            this.removeCount += amount;
            this.onCrafting(stack);
        }

        @Override
        protected void onCrafting(ItemStack stack)
        {
            if (!this.player.world.isRemote)
            {
                int i = this.removeCount;
                float f = 0.75f;

                if (f == 0.0F)
                {
                    i = 0;
                }
                else if (f < 1.0F)
                {
                    int j = MathHelper.floor((float)i * f);

                    if (j < MathHelper.ceil((float)i * f) && Math.random() < (double)((float)i * f - (float)j))
                    {
                        ++j;
                    }

                    i = j;
                }

                while (i > 0)
                {
                    int k = EntityXPOrb.getXPSplit(i);
                    i -= k;
                    this.player.world.spawnEntity(new EntityXPOrb(this.player.world, this.player.posX, this.player.posY + 0.5D, this.player.posZ + 0.5D, k));
                }
            }

            this.removeCount = 0;
        }
    }

    public static class TileWrapper implements IInventory {
        private TileEntityBrazier brazier;

        public TileWrapper (TileEntityBrazier brazier) {
            this.brazier = brazier;
        }

        @Override
        public int getField(int id) {
            return this.brazier.getField(id);
        }

        @Override
        public void setField(int id, int value) {
            this.brazier.setField(id, value);
        }

        @Override
        public int getFieldCount() {
            return 4;
        }

        @Override
        public int getSizeInventory() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public ItemStack getStackInSlot(int index) {
            return null;
        }

        @Override
        public ItemStack decrStackSize(int index, int count) {
            return null;
        }

        @Override
        public ItemStack removeStackFromSlot(int index) {
            return null;
        }

        @Override
        public void setInventorySlotContents(int index, ItemStack stack) {

        }

        @Override
        public int getInventoryStackLimit() {
            return 0;
        }

        @Override
        public void markDirty() {

        }

        @Override
        public boolean isUsableByPlayer(EntityPlayer player) {
            return false;
        }

        @Override
        public void openInventory(EntityPlayer player) {

        }

        @Override
        public void closeInventory(EntityPlayer player) {

        }

        @Override
        public boolean isItemValidForSlot(int index, ItemStack stack) {
            return false;
        }

        @Override
        public void clear() {

        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public boolean hasCustomName() {
            return false;
        }

        @Override
        public ITextComponent getDisplayName() {
            return null;
        }
    }
}
