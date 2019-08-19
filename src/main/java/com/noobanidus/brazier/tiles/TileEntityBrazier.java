package com.noobanidus.brazier.tiles;

import com.noobanidus.brazier.Brazier;
import com.noobanidus.brazier.BrazierConfig;
import com.noobanidus.brazier.blocks.BlockBrazier;
import com.noobanidus.brazier.init.Registrar;
import com.noobanidus.brazier.util.AspectUtil;
import com.noobanidus.brazier.util.ItemStackWrapper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.blocks.essentia.BlockJarItem;
import thaumcraft.common.items.consumables.ItemPhial;
import thaumcraft.common.items.resources.ItemCrystalEssence;

import javax.annotation.Nullable;

public class TileEntityBrazier extends TileEntity implements ITickable {
	private int progress = 0;
	private int modifiedDuration = -1;
	private int burnTime = 0;
	private int itemTime = 0;
	private boolean ready = false;
	private int carriedOver = 0;

	private ItemStackHandler inventory = new ItemStackHandler(2);
	private IItemHandler input = new ItemStackWrapper(0, inventory);
	private IItemHandler output = new ItemStackWrapper(1, inventory);

	public TileEntityBrazier () {
	}

	public ItemStackHandler getInventory () {
		return inventory;
	}

	public void setField (int field, int value) {
		if (field == 0) {
			this.burnTime = value;
		} else if (field == 1) {
			this.itemTime = value;
		} else if (field == 2) {
			this.progress = value;
		} else if (field == 3) {
			this.modifiedDuration = value;
		}
	}

	public int getField (int field) {
		if (field == 0) {
			return this.burnTime;
		} else if (field == 1) {
			return this.itemTime;
		} else if (field == 2) {
			return this.progress;
		} else if (field == 3) {
			return this.modifiedDuration;
		}

		return -1;
	}

	public int getBurnTime () {
		return burnTime;
	}

	private void resetDuration () {
		this.modifiedDuration = BrazierConfig.cookTime;
		this.progress = 0;

		if (carriedOver != 0) {
			this.progress += carriedOver;
			carriedOver = 0;
		}
	}

	@Override
	public boolean hasCapability (Capability<?> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return true;
		}

		return super.hasCapability(capability, facing);
	}

	@Nullable
	@Override
	public <T> T getCapability (Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (facing == EnumFacing.UP) {
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(input);
			}
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(output);
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public void onDataPacket (NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
		super.onDataPacket(net, pkt);
	}

	@Override
	public void readFromNBT (NBTTagCompound compound) {
		this.modifiedDuration = compound.getInteger("modified_duration");
		this.burnTime = compound.getInteger("burn_time");
		this.progress = compound.getInteger("progress");
		this.itemTime = compound.getInteger("item_time");
		this.inventory.deserializeNBT(compound.getCompoundTag("inventory"));
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT (NBTTagCompound compound) {
		compound = super.writeToNBT(compound);

		compound.setInteger("modified_duration", this.modifiedDuration);
		compound.setInteger("burn_time", this.burnTime);
		compound.setInteger("progress", this.progress);
		compound.setInteger("item_time", this.itemTime);
		compound.setTag("inventory", this.inventory.serializeNBT());
		return compound;
	}

	@Override
	public NBTTagCompound getUpdateTag () {
		return writeToNBT(new NBTTagCompound());
	}

	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket () {
		return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
	}

	@Override
	public void update () {
		int essentiaBurnTime = BrazierConfig.cookTime / BrazierConfig.essentiaCount;
		int bonus = (int) Math.floor(essentiaBurnTime * BrazierConfig.complexBonus);
		IBlockState state = world.getBlockState(pos);

		boolean update = false;

		if (this.modifiedDuration == -1) {
			resetDuration();
			update = true;
		}

		if (this.burnTime == 0) {
			// Try to consume essentia
			ItemStack incoming = inventory.getStackInSlot(0);
			if (incoming.isEmpty() && state.getBlock() == Registrar.Blocks.lit_brazier) {
				BlockBrazier.setState(false, world, pos);
			} else if (!incoming.isEmpty()) {
				int countIncoming = 0;
				int countBonus = 0;

				if (incoming.getItem() instanceof BlockJarItem) {
					AspectList aspects = ((BlockJarItem) incoming.getItem()).getAspects(incoming);
					if (aspects != null && aspects.size() == 1) {
						boolean empty = false;
						for (Aspect aspect : aspects.getAspects()) {
							if (aspects.getAmount(aspect) >= 1) {
								aspects.reduce(aspect, 1);
								countIncoming += 1;
								countBonus += AspectUtil.complexity(aspect);
								if (aspects.getAmount(aspect) == 0) {
									empty = true;
								}
								break;
							}
						}
						if (!empty) {
							((BlockJarItem) incoming.getItem()).setAspects(incoming, aspects);
							inventory.setStackInSlot(0, incoming);
						} else {
							inventory.setStackInSlot(0, new ItemStack(BlocksTC.jarNormal));
						}
						update = true;
					} else if (aspects != null) {
						Brazier.LOG.info("Found an essentia jar with more than 1 aspect?!" + incoming.getItem().getItemStackDisplayName(incoming));
					}
				} else if (incoming.getItem() == ItemsTC.phial) {
					AspectList aspects = ((ItemPhial) incoming.getItem()).getAspects(incoming);
					if (aspects != null && aspects.size() == 1) {
						for (Aspect aspect : aspects.getAspects()) {
							int thisCount = aspects.getAmount(aspect);
							countIncoming += thisCount;
							countBonus += AspectUtil.complexity(aspect) * thisCount;
							aspects.reduce(aspect, thisCount);
							break;
						}
						if (incoming.getCount() != 1) {
							incoming.shrink(1);
							inventory.setStackInSlot(0, incoming);
							if (!world.isRemote) {
								ItemStack empty = new ItemStack(ItemsTC.phial);
								InventoryHelper.spawnItemStack(world, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, empty);
							}
						} else {
							inventory.setStackInSlot(0, new ItemStack(ItemsTC.phial));
						}
						update = true;
					} else if (aspects != null) {
						Brazier.LOG.info("Found an essentia phial with more than 1 aspect?!" + incoming.getItem().getItemStackDisplayName(incoming));
					}
				} else if (incoming.getItem() == ItemsTC.crystalEssence) {
					AspectList aspects = ((ItemCrystalEssence) incoming.getItem()).getAspects(incoming);
					if (aspects.size() == 1) {
						for (Aspect aspect : aspects.getAspects()) {
							int thisCount = aspects.getAmount(aspect);
							countIncoming += thisCount;
							countBonus += AspectUtil.complexity(aspect) * thisCount;
							break;
						}
						incoming.shrink(1);
						inventory.setStackInSlot(0, incoming);
						update = true;
					} else {
						Brazier.LOG.info("Found an essentia crystal with more than 1 aspect?!" + incoming.getItem().getItemStackDisplayName(incoming));
					}
				}

				if (countIncoming != 0) {
					this.itemTime = essentiaBurnTime * countIncoming;
					this.burnTime += this.itemTime;
					BlockBrazier.setState(true, world, pos);
				} else if (this.burnTime == 0) {
					BlockBrazier.setState(false, world, pos);
				}
				if (countBonus != 0) {
					// If the bonus ticks caused it to be ready
					if (progress < modifiedDuration) {
						ready = false;
					}
					int finalBonus = countBonus * bonus;
					if (progress + finalBonus >= modifiedDuration) {
						carriedOver = (progress + finalBonus) - this.modifiedDuration;
						this.progress = modifiedDuration;
					} else {
						this.progress += finalBonus;
					}
					if (progress >= modifiedDuration) {
						ready = true;
					}
				}
			}
		} else {
			this.burnTime--;
			if (progress < modifiedDuration) {
				ready = false;
			}
			this.progress++;
			if (progress >= modifiedDuration) {
				ready = true;
			}
		}

		int meta = BrazierConfig.meta;
		if (meta < 0 || meta > 5) {
			meta = 0;
		}

		if (progress >= modifiedDuration && ready && !world.isRemote) {
			// Spawn a curio
			ItemStack slot = inventory.getStackInSlot(1);
			if (!slot.isEmpty()) {
				if (slot.getItem() == ItemsTC.curio && slot.getCount() < slot.getMaxStackSize()) {
					slot.grow(1);
					inventory.setStackInSlot(1, slot);
				} else {
					// Well, you're fucked.
					ItemStack copy = new ItemStack(ItemsTC.curio, 1, meta);
					BlockPos pos = getPos();
					InventoryHelper.spawnItemStack(world, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, copy);
					// Confer https://i.imgur.com/Xxw75GL.gifv
				}
			} else {
				slot = new ItemStack(ItemsTC.curio, 1, meta);
				inventory.setStackInSlot(1, slot);
			}
			markDirty();
			resetDuration();
			ready = false;
			update = true;
		}

		if (update && !world.isRemote) {
			SPacketUpdateTileEntity packet = getUpdatePacket();
			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
			if (server != null) {
				server.getPlayerList().sendToAllNearExcept(null, pos.getX(), pos.getY(), pos.getZ(), 64, world.provider.getDimension(), packet);
			}
		}
	}
}
