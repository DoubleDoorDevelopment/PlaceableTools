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

import net.doubledoordev.block.ToolTE;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class ToolRenderer extends TileEntitySpecialRenderer
{
    public static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    public static final ResourceLocation SIGN_TEXTURE   = new ResourceLocation("textures/entity/sign.png");
    public static final ModelSign        MODEL_SIGN     = new ModelSign();

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float tickTime)
    {
        ToolTE te = (ToolTE) tileentity;
        if (te.getStack() == null) return;

        ItemStack is = te.getStack();
        doRenderPass(0, te.getBlockMetadata(), is, x, y, z);

        if (is.getItem().requiresMultipleRenderPasses())
        {
            for (int i = 1; i < is.getItem().getRenderPasses(is.getItemDamage()); i++)
            {
                doRenderPass(i, te.getBlockMetadata(), is, x, y, z);
            }
        }

        if (te.getStack().getItem() instanceof ItemSword && te.sign1Facing != -1)
        {
            renderSign(x, y, z, tickTime, te.sign1Facing, te.sign1Text, te.getBlockMetadata());
        }

        if (te.getStack().getItem() instanceof ItemSword && te.sign2Facing != -1)
        {
            renderSign(x, y, z, tickTime, te.sign2Facing, te.sign2Text, te.getBlockMetadata());
        }
    }

    private void renderSign(double x, double y, double z, float tickTime, int facing, String[] signText, int meta)
    {
        GL11.glPushMatrix();
        float f1 = 0.6666667F;
        float f2 = 0.0F;

        switch (facing)
        {
            case 0:
            case 8:
                f2 = -90f;
                break;
            case 1:
            case 7:
                f2 = 90f;
                break;
            case 2:
            case 5:
                f2 = 180f;
        }

        if (meta >= 2)
        {
            GL11.glTranslated(0, -1.2, 0);
        }

        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.75F * f1, (float) z + 0.5F);
        GL11.glRotatef(-f2, 0.0F, 1.0F, 0.0F);

        GL11.glTranslatef(0.0F, -0.3125F, -0.4375F);
        GL11.glTranslatef(0f, 0.8F, 0.5f);
        MODEL_SIGN.signStick.showModel = false;

        if (facing > 4)
        {
            if (meta >= 2) GL11.glTranslated(-0.6, 0, 0);
            //GL11.glTranslated(b ? -0.4 : -0.6, 0, 0);

            if (meta == 2 && facing == 7) GL11.glTranslated(1.2, 0, 0);
            else if (meta == 3 && facing == 8) GL11.glTranslated(1.2, 0, 0);
            else if (meta == 4 && facing == 6) GL11.glTranslated(1.2, 0, 0);
            else if (meta == 5 && facing == 5) GL11.glTranslated(1.2, 0, 0);
        }

        this.bindTexture(SIGN_TEXTURE);
        GL11.glPushMatrix();
        GL11.glScalef(f1, -f1, -f1);
        MODEL_SIGN.renderSign();
        GL11.glPopMatrix();
        FontRenderer fontrenderer = this.func_147498_b();
        f2 = 0.016666668F * f1;
        GL11.glTranslatef(0.0F, 0.5F * f1, 0.07F * f1);
        GL11.glScalef(f2, -f2, f2);
        GL11.glNormal3f(0.0F, 0.0F, -1.0F * f2);
        GL11.glDepthMask(false);
        byte b0 = 0;

        for (int j = 0; j < signText.length; ++j)
        {
            GL11.glPushMatrix();
            String s = signText[j];
            int width = fontrenderer.getStringWidth(s);
            if (width > 95)
            {
                float f = 1f - ((width) * 0.0015f);
                GL11.glScalef(f, f, f);
            }
            fontrenderer.drawString(s, -width / 2, j * 10 - signText.length * 5, b0);
            GL11.glPopMatrix();
        }

        GL11.glDepthMask(true);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    public static void doRenderPass(int i, int meta, ItemStack stack, double x, double y, double z)
    {
        IIcon icon = stack.getItem().getIcon(stack, i);

        if (icon == null) return;

        GL11.glPushMatrix();
        TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
        texturemanager.bindTexture(texturemanager.getResourceLocation(stack.getItemSpriteNumber()));
        Tessellator tessellator = Tessellator.instance;

        GL11.glTranslated(x, y, z); //Center to block
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        GL11.glTranslatef(-0.5f, .5f, 0.5f); // Center on block

        if (stack.getItem() instanceof ItemSpade || stack.getItem() instanceof ItemHoe)
        {
            float shift = 0.3f;
            switch (ForgeDirection.values()[meta])
            {
                case NORTH:
                    GL11.glTranslatef(0, 0, -shift);
                    GL11.glRotatef(10f, -0.5f, 0, 0);
                    break;
                case SOUTH:
                    GL11.glTranslatef(0, 0, shift);
                    GL11.glRotatef(-10f, -0.5f, 0, 0);
                    break;
                case EAST:
                    GL11.glRotatef(90f, 0, 1, 0);
                    GL11.glTranslatef(-1, 0, 1);
                    GL11.glTranslatef(0, 0, shift);
                    GL11.glRotatef(-10f, -0.5f, 0, 0);
                    break;
                case WEST:
                    GL11.glRotatef(90f, 0, 1, 0);
                    GL11.glTranslatef(-1, 0, 1);
                    GL11.glTranslatef(0, 0, -shift);
                    GL11.glRotatef(10f, -0.5f, 0, 0);
                    break;
                case DOWN:
                    GL11.glRotatef(90f, 0, 1, 0);
                    GL11.glTranslatef(-1, 0, 1);
                    break;
            }
            GL11.glTranslatef(0, 0, -0.03f); //Icon depth of the shovel
            GL11.glRotatef(180f, 1, 0, 0);
        }
        else if (stack.getItem() instanceof ItemAxe || stack.getItem() instanceof ItemPickaxe)
        {
            float shift = 0.15f;
            switch (ForgeDirection.values()[meta])
            {
                case NORTH:
                    GL11.glRotatef(90f, 0, 1, 0);
                    GL11.glTranslatef(-1, 0, 1);
                    GL11.glTranslatef(shift, 0, 0);
                    GL11.glRotatef(-10f, 0, 0, 1);
                    break;
                case SOUTH:
                    GL11.glRotatef(-90f, 0, 1, 0);
                    GL11.glTranslatef(-1, 0, -1);
                    GL11.glTranslatef(shift, 0, 0);
                    GL11.glRotatef(-10f, 0, 0, 1);
                    break;
                case EAST:
                    GL11.glTranslatef(shift, 0, 0);
                    GL11.glRotatef(-10f, 0, 0, 1);
                    break;
                case WEST:
                    GL11.glRotatef(180f, 0, 1, 0);
                    GL11.glTranslatef(-2, 0, 0);
                    GL11.glTranslatef(shift, 0, 0);
                    GL11.glRotatef(-10f, 0, 0, 1);
                    break;
            }
            GL11.glTranslatef(0, 0, 0.03F);
        }
        else if (stack.getItem() instanceof ItemSword)
        {
            float shift = 0.15f;
            switch (ForgeDirection.values()[meta])
            {
                case NORTH:
                    GL11.glRotatef(90f, 0, 1, 0);
                    GL11.glTranslatef(shift, 0, 0);
                    GL11.glRotatef(-90f, 0, 0, 1);
                    GL11.glTranslatef(-1, 0, 1);
                    break;
                case SOUTH:
                    GL11.glRotatef(-90f, 0, 1, 0);
                    GL11.glTranslatef(shift, 0, 0);
                    GL11.glRotatef(-90f, 0, 0, 1);
                    GL11.glTranslatef(-1, 0, -1);
                    break;
                case EAST:
                    GL11.glTranslatef(shift, 0, 0);
                    GL11.glRotatef(-90f, 0, 0, 1);
                    GL11.glTranslatef(-1, 1, 0);
                    break;
                case WEST:
                    GL11.glRotatef(180f, 0, 1, 0);
                    GL11.glTranslatef(-1, 1, 0);
                    GL11.glTranslatef(shift, 0, 0);
                    GL11.glRotatef(-90f, 0, 0, 1);
                    break;
                case UP:
                    GL11.glRotatef(180f, 1, 0, 0);
                    GL11.glTranslatef(1, 0, 1);
                    GL11.glRotatef(90f, 0, 1, 0);
                    break;
                case DOWN:
                    GL11.glRotatef(180f, 1, 0, 0);
                    break;
            }
            GL11.glTranslatef(-0.05f, 0, 0.03F);
        }
        GL11.glRotatef(-45f, 0, 0, 1);
        GL11.glScalef(1.5f, 1.5f, 1.5f);
        ItemRenderer.renderItemIn2D(tessellator, icon.getMaxU(), icon.getMinV(), icon.getMinU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), 0.06F / 1.5f);

        if (stack.hasEffect(i))
        {
            GL11.glDepthFunc(GL11.GL_EQUAL);
            GL11.glDisable(GL11.GL_LIGHTING);
            texturemanager.bindTexture(RES_ITEM_GLINT);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
            float f7 = 0.76F;
            GL11.glColor4f(0.5F * f7, 0.25F * f7, 0.8F * f7, 1.0F);
            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GL11.glPushMatrix();
            float f8 = 0.125F;
            GL11.glScalef(f8, f8, f8);
            float f9 = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
            GL11.glTranslatef(f9, 0.0F, 0.0F);
            GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
            ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.06F / 1.5f);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(f8, f8, f8);
            f9 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
            GL11.glTranslatef(-f9, 0.0F, 0.0F);
            GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
            ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.06F / 1.5f);
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }
}
