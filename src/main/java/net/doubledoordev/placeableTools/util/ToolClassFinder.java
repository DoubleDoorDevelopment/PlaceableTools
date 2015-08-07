package net.doubledoordev.placeableTools.util;

import net.doubledoordev.placeableTools.PlaceableTools;
import net.minecraft.block.material.Material;
import net.minecraft.item.*;

/**
 * @author Dries007
 */
public class ToolClassFinder
{
    public static boolean isHoe(ItemStack stack)
    {
        return stack != null && stack.getItem() instanceof ItemHoe;
    }

    public static boolean isSpade(ItemStack stack)
    {
        return stack != null && (stack.getItem() instanceof ItemSpade || stack.getItem().getToolClasses(stack).contains("shovel"));
    }

    public static boolean isAxe(ItemStack stack)
    {
        return stack != null && (stack.getItem() instanceof ItemAxe || stack.getItem().getToolClasses(stack).contains("axe"));
    }

    public static boolean isPick(ItemStack stack)
    {
        return stack != null && (stack.getItem() instanceof ItemPickaxe || stack.getItem().getToolClasses(stack).contains("pickaxe"));
    }

    public static boolean isSword(ItemStack stack)
    {
        return stack != null && stack.getItem() instanceof ItemSword;
    }

    public static boolean checkMaterial(Material material, ItemStack stack)
    {
        return stack != null && (!PlaceableTools.checkMaterial || !(material.isLiquid() || material.isReplaceable()) && (material.isToolNotRequired() || ToolClassFinder.isPick(stack)));
    }
}
