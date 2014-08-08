/*
 * Copyright (c) 2014, DoubleDoorDevelopment
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *  Neither the name of the project nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.doubledoordev.placeableTools.block;

import net.doubledoordev.placeableTools.PlaceableTools;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.Random;

public class BucketTE extends TileEntity
{
    private ItemStack stack;
    private Fluid     fluid;

    public BucketTE()
    {

    }

    public BucketTE(World world)
    {
        setWorldObj(world);
    }

    public void placeBlock(ItemStack stack)
    {
        if (stack != null) this.stack = stack.copy();
        if (this.stack != null) this.stack.stackSize = 1;

        FluidStack fluidStack = FluidContainerRegistry.getFluidForFilledItem(this.stack);
        fluid = fluidStack == null ? null : fluidStack.getFluid();
    }

    public void removeBlock(World world)
    {
        if (world.isRemote) return;
        dropItem(stack);
    }

    public void dropItem(ItemStack stack)
    {
        if (!worldObj.isRemote && worldObj.getGameRules().getGameRuleBooleanValue("doTileDrops") && stack != null)
        {
            float f = 0.7F;
            double d0 = (double) (worldObj.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            double d1 = (double) (worldObj.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            double d2 = (double) (worldObj.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            EntityItem entityitem = new EntityItem(worldObj, (double) xCoord + d0, (double) yCoord + d1, (double) zCoord + d2, stack);
            entityitem.delayBeforeCanPickup = 10;
            worldObj.spawnEntityInWorld(entityitem);
        }
    }

    public ItemStack getStack()
    {
        return stack;
    }

    public Fluid getFluid()
    {
        return fluid;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) return;
        super.readFromNBT(tag);

        stack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("stack"));
        FluidStack fluidStack = FluidContainerRegistry.getFluidForFilledItem(this.stack);
        fluid = fluidStack == null ? null : fluidStack.getFluid();
    }

    @Override
    public void writeToNBT(NBTTagCompound tag)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) return;
        super.writeToNBT(tag);
        tag.setTag("stack", getStack().writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
        stack = ItemStack.loadItemStackFromNBT(pkt.func_148857_g().getCompoundTag("stack"));
        FluidStack fluidStack = FluidContainerRegistry.getFluidForFilledItem(this.stack);
        fluid = fluidStack == null ? null : fluidStack.getFluid();
    }

    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound root = new NBTTagCompound();
        root.setTag("stack", getStack().writeToNBT(new NBTTagCompound()));
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 15, root);
    }

    public int getLightLevel()
    {
        return fluid == null ? 0 : fluid.getLuminosity();
    }

    public void standIn(Entity entity)
    {
        if (worldObj.isRemote) return;
        if (fluid != null)
        {
            if (fluid.getTemperature() >= 700)
            {
                if (!entity.isImmuneToFire() && (!entity.isBurning() || worldObj.rand.nextInt(25) == 2))
                {
                    entity.attackEntityFrom(DamageSource.lava, 4.0F);
                    entity.setFire(15);
                }
            }

            if (fluid == FluidRegistry.WATER)
            {
                entity.extinguish();
            }

            if (fluid == PlaceableTools.instance.milk)
            {
                if (entity instanceof EntityLivingBase) ((EntityLivingBase) entity).clearActivePotions();
            }
        }
    }

    public boolean activate(EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        ItemStack itemstack = player.getHeldItem();
        if (itemstack != null && fluid == FluidRegistry.WATER)
        {
            if (itemstack.getItem() instanceof ItemArmor && ((ItemArmor) itemstack.getItem()).getArmorMaterial() == ItemArmor.ArmorMaterial.CLOTH)
            {
                ItemArmor itemarmor = (ItemArmor) itemstack.getItem();
                itemarmor.removeColor(itemstack);
                return true;
            }

            if (itemstack.getItem() instanceof ItemBlock)
            {
                Block block = Block.getBlockFromItem(itemstack.getItem());
                if (block instanceof BlockColored)
                {
                    itemstack.setItemDamage(0);
                    return true;
                }
                if (block == Blocks.stained_hardened_clay) player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Blocks.hardened_clay, itemstack.stackSize));
                if (block == Blocks.stained_glass) player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Blocks.glass, itemstack.stackSize));
                if (block == Blocks.stained_glass_pane) player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Blocks.glass_pane, itemstack.stackSize));
            }
        }

        return false;
    }

    public void randomDisplayTick(Random random)
    {
        int rndFactor = 10;
        if (fluid != null && fluid.getTemperature() >= 700)
        {
            if (worldObj.isRaining())
            {
                rndFactor /= 2;
                worldObj.playSoundEffect((double) ((float) xCoord + 0.5F), (double) ((float) yCoord + 0.5F), (double) ((float) zCoord + 0.5F), "random.fizz", 0.5F, 2.6F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.8F);
                worldObj.spawnParticle("largesmoke", (double) xCoord + Math.random(), (double) yCoord + 1.2D, (double) zCoord + Math.random(), 0.0D, 0.0D, 0.0D);
            }

            if (fluid == FluidRegistry.LAVA)
            {
                if (random.nextInt(rndFactor) == 0)
                {
                    double d5 = (double) ((float) xCoord + random.nextFloat());
                    double d7 = (double) yCoord + 1;
                    double d6 = (double) ((float) zCoord + random.nextFloat());
                    worldObj.spawnParticle("lava", d5, d7, d6, 0.0D, 0.0D, 0.0D);
                    worldObj.playSound(d5, d7, d6, "liquid.lavapop", 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
                }

                if (random.nextInt(rndFactor * 2) == 0) worldObj.playSound((double) xCoord, (double) yCoord, (double) zCoord, "liquid.lava", 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
            }
        }

        if (fluid == FluidRegistry.WATER && random.nextInt(rndFactor * 2) == 0)
        {
            worldObj.playSound((double) ((float) xCoord + 0.5F), (double) ((float) yCoord + 0.5F), (double) ((float) zCoord + 0.5F), "liquid.water", random.nextFloat() * 0.25F + 0.75F, random.nextFloat() * 1.0F + 0.5F, false);
        }
    }
}
