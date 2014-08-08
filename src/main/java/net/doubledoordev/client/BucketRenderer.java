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

package net.doubledoordev.client;

import net.doubledoordev.block.BucketTE;
import net.doubledoordev.util.renderShapes.Point3D;
import net.doubledoordev.util.renderShapes.complexShapes.Octagon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

import static net.doubledoordev.util.PTConstants.MODID;

@SideOnly(Side.CLIENT)
public class BucketRenderer extends TileEntitySpecialRenderer
{
    private static final ResourceLocation MODEL_RESOURCE_LOCATION = new ResourceLocation(MODID.toLowerCase(), "models/bucket.obj");
    private static final IModelCustom     BUCKET_MODEL      = AdvancedModelLoader.loadModel(MODEL_RESOURCE_LOCATION);
    private static final ResourceLocation BUCKET_TEXTURE    = new ResourceLocation(MODID.toLowerCase(), "textures/bucket.png");
    private static final Octagon          OCTAGON           = new Octagon(new Point3D(8, 15, 8), 7);

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f)
    {
        BucketTE bucketTE = (BucketTE) te;
        GL11.glPushMatrix();
        GL11.glScalef(1.0F, 1.0F, 1.0F);
        GL11.glTranslatef((float) x, (float) y, (float) z + 1F);
        GL11.glScalef(0.0625F, 0.0625F, 0.0625F);
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        bindTexture(BUCKET_TEXTURE);
        BUCKET_MODEL.renderAllExcept("Liquid_001");

        GL11.glPopAttrib();
        GL11.glPopMatrix();

        if (bucketTE.getFluid() != null && bucketTE.getFluid().getIcon() != null)
        {
            GL11.glPushMatrix();
            GL11.glScalef(1.0F, 1.0F, 1.0F);
            GL11.glTranslatef((float) x, (float) y, (float) z);
            GL11.glScalef(0.0625F, 0.0625F, 0.0625F);
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            bindTexture(TextureMap.locationBlocksTexture);
            OCTAGON.renderShapeWithIcon(bucketTE.getFluid().getIcon());

            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }
}
