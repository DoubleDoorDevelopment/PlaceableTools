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

package net.doubledoordev.util;

import net.doubledoordev.PlaceableTools;
import net.doubledoordev.block.BucketBlock;
import net.doubledoordev.block.ToolBlock;
import net.doubledoordev.block.ToolTE;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import net.minecraft.item.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class EventHandler
{
    public static final EventHandler INSTANCE = new EventHandler();

    private EventHandler() {}

    public void init()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * For placing tools in the world
     */
    @SubscribeEvent
    public void clickEvent(PlayerInteractEvent event)
    {
        World world = event.entityPlayer.getEntityWorld();

        ItemStack itemStack = event.entityPlayer.getHeldItem();

        if (itemStack == null || event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) return;
        if (itemStack.getItem() instanceof ItemSign && event.face != 1 && event.face != 0)
        {
            if (world.getBlock(event.x, event.y, event.z) == ToolBlock.getInstance())
            {
                ToolTE te = (ToolTE) world.getTileEntity(event.x, event.y, event.z);

                if (te.getStack().getItem() instanceof ItemSword && te.addSign(3 + event.face) != -1)
                {
                    if (!event.entityPlayer.capabilities.isCreativeMode) itemStack.stackSize--;
                    if (!world.isRemote) MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayersInDimension(te.getDescriptionPacket(), world.provider.dimensionId);
                    event.setCanceled(!world.isRemote);
                    FMLNetworkHandler.openGui(event.entityPlayer, PlaceableTools.instance, GuiHandler.SWORD_SIGN_GUI_ID, world, event.x, event.y, event.z);
                }
            }
        }

        if (world.isRemote) return;
        if (event.entityPlayer.isSneaking() && ToolBlock.getInstance().checkMaterial(world.getBlock(event.x, event.y, event.z).getMaterial(), itemStack.getItem()))
        {
            int x = event.x, y = event.y, z = event.z;
            if (event.face == 1 && (itemStack.getItem() instanceof ItemSpade || itemStack.getItem() instanceof ItemHoe))
            {
                y++; // Cause the shovel gets placed above the block clicked
                if (world.isAirBlock(x, y, z) && world.getBlock(event.x, event.y, event.z).isSideSolid(world, event.x, event.y, event.z, ForgeDirection.UP))
                {
                    event.setCanceled(true);
                    world.setBlock(x, y, z, ToolBlock.getInstance(), ToolBlock.getInstance().getMetaForLean(world, x, y, z), 3);
                    ToolBlock.getInstance().onBlockPlacedBy(world, x, y, z, event.entityPlayer, itemStack);
                }
            }
            if (event.face != 1 && event.face != 0 && (itemStack.getItem() instanceof ItemAxe || itemStack.getItem() instanceof ItemPickaxe))
            {
                ForgeDirection direction = ForgeDirection.VALID_DIRECTIONS[event.face];
                x += direction.offsetX;
                y += direction.offsetY;
                z += direction.offsetZ;

                if (world.isAirBlock(x, y, z) && world.getBlock(event.x, event.y, event.z).isSideSolid(world, event.x, event.y, event.z, direction.getOpposite()))
                {
                    event.setCanceled(true);
                    world.setBlock(x, y, z, ToolBlock.getInstance(), direction.getOpposite().ordinal(), 3);
                    ToolBlock.getInstance().onBlockPlacedBy(world, x, y, z, event.entityPlayer, itemStack);
                }
            }
            if (itemStack.getItem() instanceof ItemSword)
            {
                ForgeDirection direction = ForgeDirection.VALID_DIRECTIONS[event.face];
                x += direction.offsetX;
                y += direction.offsetY;
                z += direction.offsetZ;

                if (world.isAirBlock(x, y, z) && world.getBlock(event.x, event.y, event.z).isSideSolid(world, event.x, event.y, event.z, direction.getOpposite()))
                {
                    int meta = direction.getOpposite().ordinal();
                    if (meta == 0)
                    {
                        meta = world.rand.nextInt(2);
                    }
                    event.setCanceled(true);
                    world.setBlock(x, y, z, ToolBlock.getInstance(), meta, 3);
                    ToolBlock.getInstance().onBlockPlacedBy(world, x, y, z, event.entityPlayer, itemStack);
                }
            }
        }

        if (event.entityPlayer.isSneaking() && itemStack.getItem() instanceof ItemBucket)
        {
            int x = event.x, y = event.y, z = event.z;
            y++;
            if (world.isAirBlock(x, y, z) && world.getBlock(event.x, event.y, event.z).isSideSolid(world, event.x, event.y, event.z, ForgeDirection.UP))
            {
                event.setCanceled(true);
                world.setBlock(x, y, z, BucketBlock.getInstance(), 0, 3);
                BucketBlock.getInstance().onBlockPlacedBy(world, x, y, z, event.entityPlayer, itemStack);
            }
        }
        else if (event.entityPlayer.isSneaking() && itemStack.getItem() instanceof ItemBucketMilk)
        {
            int x = event.x, y = event.y, z = event.z;
            y++;
            if (world.isAirBlock(x, y, z) && world.getBlock(event.x, event.y, event.z).isSideSolid(world, event.x, event.y, event.z, ForgeDirection.UP))
            {
                event.setCanceled(true);
                world.setBlock(x, y, z, BucketBlock.getInstance(), 0, 3);
                BucketBlock.getInstance().onBlockPlacedBy(world, x, y, z, event.entityPlayer, itemStack);
            }
        }
    }
}
