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

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import static net.doubledoordev.placeableTools.util.PTConstants.TEXT_JOINER;

public class ToolTE extends TileEntity
{
    private ItemStack stack;

    public int      sign1Facing = -1;
    public String[] sign1Text   = {"", "", "", ""};

    public int      sign2Facing = -1;
    public String[] sign2Text   = {"", "", "", ""};
    public int      lastAdded   = 0;

    public ToolTE()
    {

    }

    public ToolTE(World world)
    {
        setWorldObj(world);
    }

    public void placeBlock(ItemStack stack)
    {
        if (stack != null) this.stack = stack.copy();
    }

    public void removeBlock(World world)
    {
        if (world.isRemote) return;
        dropItem(stack);
        if (sign1Facing != -1) dropItem(new ItemStack(Items.sign));
        if (sign2Facing != -1) dropItem(new ItemStack(Items.sign));
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

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) return;
        super.readFromNBT(tag);

        stack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("stack"));
        sign1Facing = tag.getInteger("sign1Facing");
        sign1Text = tag.getString("sign1Text").split("\n");
        sign2Facing = tag.getInteger("sign2Facing");
        sign2Text = tag.getString("sign2Text").split("\n");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) return;
        super.writeToNBT(tag);
        tag.setTag("stack", getStack().writeToNBT(new NBTTagCompound()));
        tag.setInteger("sign1Facing", sign1Facing);
        tag.setString("sign1Text", TEXT_JOINER.join(sign1Text));
        tag.setInteger("sign2Facing", sign2Facing);
        tag.setString("sign2Text", TEXT_JOINER.join(sign2Text));
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
        stack = ItemStack.loadItemStackFromNBT(pkt.func_148857_g().getCompoundTag("stack"));
        sign1Facing = pkt.func_148857_g().getInteger("sign1Facing");
        sign1Text = pkt.func_148857_g().getString("sign1Text").split("\n");
        sign2Facing = pkt.func_148857_g().getInteger("sign2Facing");
        sign2Text = pkt.func_148857_g().getString("sign2Text").split("\n");
        lastAdded = pkt.func_148857_g().getInteger("lastAdded");
    }

    public Packet getDescriptionPacket()
    {
        NBTTagCompound root = new NBTTagCompound();
        root.setTag("stack", getStack().writeToNBT(new NBTTagCompound()));
        root.setInteger("sign1Facing", sign1Facing);
        root.setString("sign1Text", TEXT_JOINER.join(sign1Text));
        root.setInteger("sign2Facing", sign2Facing);
        root.setString("sign2Text", TEXT_JOINER.join(sign2Text));
        root.setInteger("lastAdded", lastAdded);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 15, root);
    }

    public int addSign(int coordBaseMode)
    {
        if (sign1Facing == -1)
        {
            sign1Facing = coordBaseMode;
            lastAdded = 1;
            return 1;
        }
        if (sign2Facing == -1)
        {
            sign2Facing = coordBaseMode;
            lastAdded = 2;
            return 2;
        }
        return -1;
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return AxisAlignedBB.getBoundingBox(xCoord - 1, yCoord - 1, zCoord - 1, xCoord + 2, yCoord + 2, zCoord + 2);
    }

    public void setText(boolean b, String[] strings)
    {
        if (b)
        {
            lastAdded = 1;
            sign1Text = strings;
        }
        else
        {
            lastAdded = 2;
            sign2Text = strings;
        }
        MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayersInDimension(getDescriptionPacket(), worldObj.provider.dimensionId);
    }

    public void onNeighborBlockChange()
    {
        int meta = getBlockMetadata();
        ForgeDirection direction = ForgeDirection.VALID_DIRECTIONS[meta];

        if (stack.getItem() instanceof ItemSpade || stack.getItem() instanceof ItemHoe) direction = ForgeDirection.DOWN;

        Material material = worldObj.getBlock(xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ).getMaterial();

        if (material == null || stack == null || !ToolBlock.getInstance().checkMaterial(material, stack.getItem())) //We need to pop off
        {
            worldObj.setBlockToAir(xCoord, yCoord, zCoord);
        }
    }
}