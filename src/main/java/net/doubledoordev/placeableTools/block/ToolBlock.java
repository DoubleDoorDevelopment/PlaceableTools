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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.doubledoordev.placeableTools.util.PTConstants;
import net.doubledoordev.placeableTools.util.ToolClassFinder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;

public class ToolBlock extends BlockContainer
{
    private static ToolBlock instance;

    public static ToolBlock getInstance()
    {
        return instance;
    }

    public ToolBlock()
    {
        super(Material.circuits);
        setHardness(1.5F);
        setResistance(5F);
        setBlockName(PTConstants.MODID + ":toolblock");
        instance = this;
    }

    public int getMetaForLean(World world, int x, int y, int z)
    {
        int meta = -1;
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
        {
            if (direction == ForgeDirection.DOWN || direction == ForgeDirection.UP) continue;
            if (world.getBlock(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ).isSideSolid(world, x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ, direction.getOpposite())) meta = direction.ordinal();
        }
        if (meta == -1)
        {
            meta = world.rand.nextInt(2);
        }

        return meta;
    }

    @Override
    public void breakBlock(World par1World, int x, int y, int z, Block block, int meta)
    {
        TileEntity te = par1World.getTileEntity(x, y, z);
        if (te != null && te instanceof ToolTE)
        {
            ToolTE inv = (ToolTE) te;
            inv.removeBlock(par1World);
        }
        super.breakBlock(par1World, x, y, z, block, meta);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack stack)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof ToolTE)
        {
            ToolTE inv = (ToolTE) te;
            inv.placeBlock(stack);
            if (entityLiving instanceof EntityPlayer && !((EntityPlayer) entityLiving).capabilities.isCreativeMode) stack.stackSize--;
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
        super.onNeighborBlockChange(world, x, y, z, block);

        if (world.isRemote) return;
        ((ToolTE) world.getTileEntity(x, y, z)).onNeighborBlockChange();
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return false;
    }

    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
    {
        return ((ToolTE) world.getTileEntity(x, y, z)).getStack();
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
    {
        return new ArrayList<>();
    }

    public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z)
    {
        ToolTE te = (ToolTE) blockAccess.getTileEntity(x, y, z);
        ItemStack stack = te.getStack();
        if (stack == null) return;
        if (ToolClassFinder.isHoe(stack) || ToolClassFinder.isSpade(stack))
        {
            float height = 1.3f;
            float depth = -0.3f;
            float d1 = 0.25F;
            float d2 = 0.03F;
            switch (ForgeDirection.values()[blockAccess.getBlockMetadata(x, y, z)])
            {
                case NORTH:
                    this.setBlockBounds(0.5f - d1, depth, 0, 0.5f + d1, height, 0.5f + d2);
                    break;
                case SOUTH:
                    this.setBlockBounds(0.5f - d1, depth, 0.5f + d2, 0.5f + d1, height, 1);
                    break;
                case EAST:
                    this.setBlockBounds(0.5f - d2, depth, 0.5f - d1, 1, height, 0.5f + d1);
                    break;
                case WEST:
                    this.setBlockBounds(0, depth, 0.5f - d1, 0.5f + d2, height, 0.5f + d1);
                    break;
                case DOWN:
                    this.setBlockBounds(0.5f - d2, depth, 0.5f - d1, 0.5f + d2, height, 0.5f + d1);
                    break;
                case UP:
                    this.setBlockBounds(0.5f - d1, depth, 0.5f - d2, 0.5f + d1, height, 0.5f + d2);
                    break;
            }
        }
        else if (ToolClassFinder.isAxe(stack))
        {
            float height = 0.95f;
            float depth = -0.5f;
            float d1 = 0.03F;
            float d2 = 0.15F;
            switch (ForgeDirection.values()[blockAccess.getBlockMetadata(x, y, z)])
            {
                case NORTH:
                    this.setBlockBounds(0.5f - d1, depth, 0, 0.5f + d1, height, 0.5f + d2);
                    break;
                case SOUTH:
                    this.setBlockBounds(0.5f - d1, depth, 0.5f - d2, 0.5f + d1, height, 1);
                    break;
                case EAST:
                    this.setBlockBounds(0.5f - d2, depth, 0.5f - d1, 1, height, 0.5f + d1);
                    break;
                case WEST:
                    this.setBlockBounds(0, depth, 0.5f - d1, 0.5f + d2, height, 0.5f + d1);
                    break;
            }
        }
        else if (ToolClassFinder.isPick(stack))
        {
            float height = 1.05f;
            float depth = -0.55f;
            float d1 = 0.03F;
            float d2 = 0.45F;
            switch (ForgeDirection.values()[blockAccess.getBlockMetadata(x, y, z)])
            {
                case NORTH:
                    this.setBlockBounds(0.5f - d1, depth, 0, 0.5f + d1, height, 0.5f + d2);
                    break;
                case SOUTH:
                    this.setBlockBounds(0.5f - d1, depth, 0.5f - d2, 0.5f + d1, height, 1);
                    break;
                case EAST:
                    this.setBlockBounds(0.5f - d2, depth, 0.5f - d1, 1, height, 0.5f + d1);
                    break;
                case WEST:
                    this.setBlockBounds(0, depth, 0.5f - d1, 0.5f + d2, height, 0.5f + d1);
                    break;
            }
        }
        else if (ToolClassFinder.isSword(stack))
        {
            float d1 = 0.03F;
            float d2 = 0.9f;
            switch (ForgeDirection.values()[blockAccess.getBlockMetadata(x, y, z)])
            {
                case NORTH:
                    this.setBlockBounds(0.5f - d1, 0, 0, 0.5f + d1, 1, 0.5f + d2);
                    break;
                case SOUTH:
                    this.setBlockBounds(0.5f - d1, 0, 0.5f - d2, 0.5f + d1, 1, 1);
                    break;
                case EAST:
                    this.setBlockBounds(0.5f - d2, 0, 0.5f - d1, 1, 1, 0.5f + d1);
                    break;
                case WEST:
                    this.setBlockBounds(0, 0, 0.5f - d1, 0.5f + d2, 1, 0.5f + d1);
                    break;
                case UP:
                    this.setBlockBounds(0.5f - d1, 0, 0, 0.5f + d1, 1.55f, 1);
                    break;
                case DOWN:
                    this.setBlockBounds(0, 0, 0.5f - d1, 1, 1.55f, 0.5f + d1);
                    break;
            }
        }
    }

    public static void placeLeaning(World world, int x, int y, int z, Item tool)
    {
        placeOther(world, x, y, z, tool, getInstance().getMetaForLean(world, x, y, z));
    }

    public static void placeOther(World world, int x, int y, int z, Item tool, int meta)
    {
        world.setBlock(x, y, z, ToolBlock.getInstance(), meta, 3);
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof ToolTE)
        {
            ((ToolTE) te).placeBlock(new ItemStack(tool, 1, world.rand.nextInt(tool.getMaxDamage())));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer)
    {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer)
    {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_)
    {
        return new ToolTE(world);
    }

    @Override
    public void registerBlockIcons(IIconRegister p_149651_1_)
    {

    }
}
