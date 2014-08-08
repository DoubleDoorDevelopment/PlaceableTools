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

package net.doubledoordev.placeableTools.client;

import net.doubledoordev.placeableTools.PlaceableTools;
import net.doubledoordev.placeableTools.block.ToolTE;
import net.doubledoordev.placeableTools.network.SignMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class SwordSignGui extends GuiScreen
{
    protected String  screenTitle       = "Edit sign message:";
    private int       updateCounter;
    private int       editLine;
    private GuiButton doneBtn;

    private ToolTE te;
    private TileEntitySign fakeSign = new TileEntitySign();

    public SwordSignGui(ToolTE toolTE)
    {
        this.te = toolTE;
        fakeSign.setWorldObj(toolTE.getWorldObj());
        fakeSign.xCoord = te.xCoord;
        fakeSign.yCoord = te.yCoord;
        fakeSign.zCoord = te.zCoord;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        this.buttonList.add(this.doneBtn = new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120, "Done"));
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
        PlaceableTools.getSnw().sendToServer(new SignMessage(te.xCoord, te.yCoord, te.zCoord, te.lastAdded == 1, fakeSign.signText));
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        ++this.updateCounter;
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.enabled)
        {
            if (par1GuiButton.id == 0)
            {
                // TODO: make update this.te.onInventoryChanged();
                this.mc.displayGuiScreen((GuiScreen) null);
            }
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2)
    {
        if (par2 == 200)
        {
            this.editLine = this.editLine - 1 & 3;
        }

        if (par2 == 208 || par2 == 28 || par2 == 156)
        {
            this.editLine = this.editLine + 1 & 3;
        }

        if (par2 == 14 && this.fakeSign.signText[this.editLine].length() > 0)
        {
            this.fakeSign.signText[this.editLine] = this.fakeSign.signText[this.editLine].substring(0, this.fakeSign.signText[this.editLine].length() - 1);
        }

        if (ChatAllowedCharacters.isAllowedCharacter(par1) && this.fakeSign.signText[this.editLine].length() < 15)
        {
            this.fakeSign.signText[this.editLine] = this.fakeSign.signText[this.editLine] + par1;
        }

        if (par2 == 1)
        {
            this.actionPerformed(this.doneBtn);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.screenTitle, this.width / 2, 40, 16777215);
        GL11.glPushMatrix();
        GL11.glTranslatef((float) (this.width / 2), 0.0F, 50.0F);
        float f1 = 93.75F;
        GL11.glScalef(-f1, -f1, -f1);
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        Block block = this.te.getBlockType();

        if (block == Blocks.standing_sign)
        {
            float f2 = (float) (this.te.getBlockMetadata() * 360) / 16.0F;
            GL11.glRotatef(f2, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(0.0F, -1.0625F, 0.0F);
        }
        else
        {
            int k = this.te.getBlockMetadata();
            float f3 = 0.0F;

            if (k == 2)
            {
                f3 = 180.0F;
            }

            if (k == 4)
            {
                f3 = 90.0F;
            }

            if (k == 5)
            {
                f3 = -90.0F;
            }

            GL11.glRotatef(f3, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(0.0F, -1.0625F, 0.0F);
        }

        if (this.updateCounter / 6 % 2 == 0)
        {
            fakeSign.lineBeingEdited = editLine;
        }

        TileEntityRendererDispatcher.instance.getSpecialRendererByClass(TileEntitySign.class).renderTileEntityAt(this.fakeSign, -0.5D, -0.75D, -0.5D, 0.0F);
        GL11.glPopMatrix();
        super.drawScreen(par1, par2, par3);
    }
}
