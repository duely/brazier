package com.noobanidus.brazier.blocks;

import com.noobanidus.brazier.Brazier;
import com.noobanidus.brazier.gui.GuiHandler;
import com.noobanidus.brazier.init.Registrar;
import com.noobanidus.brazier.init.Registrar.Items;
import com.noobanidus.brazier.tiles.TileEntityBrazier;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockBrazier extends Block {
	private final boolean isBurning;
	private static boolean keepInventory;

	private static AxisAlignedBB bounds = new AxisAlignedBB(0, 0, 0, 1, 0.75, 1);

	public BlockBrazier (String name, Material materialIn, boolean isBurning) {
		super(materialIn);
		this.setRegistryName(Brazier.MODID, name);
		this.isBurning = isBurning;
		this.setHardness(3.5f);
		this.setSoundType(SoundType.STONE);
		this.setTranslationKey("brazier");
	}

	@Override
	public AxisAlignedBB getBoundingBox (IBlockState state, IBlockAccess source, BlockPos pos) {
		return bounds;
	}

	@Override
	public Item getItemDropped (IBlockState state, Random rand, int fortune) {
		return Items.brazier;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick (IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (this.isBurning) {
			for (int i = 0; i < 5 + rand.nextInt(3); i++) {
				if (rand.nextDouble() < 0.1D) {
					worldIn.playSound((double) pos.getX() + 0.5D, (double) pos.getY(), (double) pos.getZ() + 0.5D, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
				}

				double x_offset = Math.min(Math.max(0.3, rand.nextDouble()), 0.7);
				double y_offset = Math.min(Math.max(0.3, rand.nextDouble()), 0.7);

				double min_y = Math.max(0.2, Math.min(x_offset, y_offset) - 0.5);

				double d0 = (double) pos.getX() + x_offset;
				double d1 = (double) pos.getY() + 0.6D + Math.min(min_y, rand.nextDouble());
				double d2 = (double) pos.getZ() + y_offset;

				double smoke_y = (double) pos.getY() + 0.75d + Math.min(Math.max(0.3, rand.nextDouble()), 0.2);

				worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, smoke_y, d2, 0.0D, 0.0D, 0.0D);
				if (rand.nextBoolean()) {
					worldIn.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
					if (rand.nextInt(5) == 0) {
						worldIn.spawnParticle(EnumParticleTypes.SPELL_WITCH, d0, smoke_y, d2, 0.0D, 0.0D, 0.0D);
					}
				}
			}
		}
	}

	@Override
	public boolean onBlockActivated (World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) {
			return true;
		} else {
			TileEntity tileentity = worldIn.getTileEntity(pos);

			if (tileentity instanceof TileEntityBrazier) {
				playerIn.openGui(Brazier.instance, GuiHandler.BRAZIER, worldIn, pos.getX(), pos.getY(), pos.getZ());
			}

			return true;
		}
	}

	public static void setState (boolean active, World worldIn, BlockPos pos) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		keepInventory = true;

		if (active) {
			worldIn.setBlockState(pos, Registrar.Blocks.lit_brazier.getDefaultState(), 3);
		} else {
			worldIn.setBlockState(pos, Registrar.Blocks.brazier.getDefaultState(), 3);
		}

		keepInventory = false;

		if (tileentity != null) {
			tileentity.validate();
			worldIn.setTileEntity(pos, tileentity);
		}
	}

	@Override
	public void breakBlock (World worldIn, BlockPos pos, IBlockState state) {
		if (!keepInventory) {
			TileEntity tileentity = worldIn.getTileEntity(pos);

			if (tileentity instanceof TileEntityBrazier) {
				// TODO drop items
				worldIn.updateComparatorOutputLevel(pos, this);
				IItemHandler handler = tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
				if (handler != null) {
					ItemStack stack1 = handler.getStackInSlot(0);
					ItemStack stack2 = handler.getStackInSlot(1);
					if (!stack1.isEmpty()) {
						InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack1);
					}
					if (!stack2.isEmpty()) {
						InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack2);
					}
				}
			}
		}

		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public ItemStack getItem (World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(Registrar.Blocks.brazier);
	}


	@Override
	public EnumBlockRenderType getRenderType (IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean hasTileEntity (IBlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity (World world, IBlockState state) {
		return new TileEntityBrazier();
	}

	@Override
	public boolean isOpaqueCube (IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullBlock (IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube (IBlockState state) {
		return false;
	}

	@Override
	public BlockRenderLayer getRenderLayer () {
		return BlockRenderLayer.CUTOUT;
	}
}
