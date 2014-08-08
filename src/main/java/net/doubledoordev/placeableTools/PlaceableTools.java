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

package net.doubledoordev.placeableTools;

import net.doubledoordev.placeableTools.block.BucketBlock;
import net.doubledoordev.placeableTools.block.BucketTE;
import net.doubledoordev.placeableTools.block.ToolBlock;
import net.doubledoordev.placeableTools.block.ToolTE;
import net.doubledoordev.placeableTools.network.SignMessage;
import net.doubledoordev.lib.DevPerks;
import net.doubledoordev.placeableTools.util.EventHandler;
import net.doubledoordev.placeableTools.util.GuiHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import org.apache.logging.log4j.Logger;

import static net.doubledoordev.placeableTools.util.PTConstants.MODID;

@Mod(modid = MODID)
public class PlaceableTools
{
    @Mod.Instance(MODID)
    public static PlaceableTools instance;

    @SidedProxy(serverSide = "net.doubledoordev.placeableTools.CommonProxy", clientSide = "net.doubledoordev.placeableTools.client.ClientProxy")
    public static CommonProxy proxy;

    private Logger               logger;
    private SimpleNetworkWrapper snw;
    public  Fluid                milk;
    private boolean              debug;

    public static Logger getLogger()
    {
        return instance.logger;
    }

    public static SimpleNetworkWrapper getSnw()
    {
        return instance.snw;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        proxy.preInit(event);

        Configuration configuration = new Configuration(event.getSuggestedConfigurationFile());
        debug = configuration.getBoolean("debug", MODID, debug, "Enable extra debug output.");
        if (configuration.getBoolean("sillyness", MODID, true, "Disable sillyness only if you want to piss off the developers XD")) MinecraftForge.EVENT_BUS.register(new DevPerks(debug));
        if (configuration.hasChanged()) configuration.save();

        if (!FluidRegistry.isFluidRegistered("milk")) FluidRegistry.registerFluid(milk = new Fluid("milk"));
        FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluid("milk"), new ItemStack(Items.milk_bucket), new ItemStack(Items.bucket));

        new ToolBlock();
        GameRegistry.registerTileEntity(ToolTE.class, "ToolTE");
        GameRegistry.registerBlock(ToolBlock.getInstance(), "ToolBlock");

        new BucketBlock();
        GameRegistry.registerTileEntity(BucketTE.class, "BucketTE");
        GameRegistry.registerBlock(BucketBlock.getInstance(), "BucketBlock");

        EventHandler.INSTANCE.init();
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());

        int id = 0;
        snw = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        snw.registerMessage(SignMessage.Handler.class, SignMessage.class, id++, Side.SERVER);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit(event);
    }
}
