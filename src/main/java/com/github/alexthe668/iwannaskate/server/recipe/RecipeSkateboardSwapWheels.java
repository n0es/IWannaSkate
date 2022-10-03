package com.github.alexthe668.iwannaskate.server.recipe;

import com.github.alexthe668.iwannaskate.server.item.IWSItemRegistry;
import com.github.alexthe668.iwannaskate.server.item.SkateboardData;
import com.github.alexthe668.iwannaskate.server.item.SkateboardWheels;
import com.github.alexthe668.iwannaskate.server.misc.IWSTags;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WoolCarpetBlock;

import java.util.ArrayList;
import java.util.List;

public class RecipeSkateboardSwapWheels extends CustomRecipe {

    public RecipeSkateboardSwapWheels(ResourceLocation name) {
        super(name);
    }

    public boolean matches(CraftingContainer craftingContainer, Level level) {
        ItemStack skateboard = ItemStack.EMPTY;
        ItemStack wheels1 = ItemStack.EMPTY;
        ItemStack wheels2 = ItemStack.EMPTY;

        for(int i = 0; i < craftingContainer.getContainerSize(); ++i) {
            ItemStack itemstack2 = craftingContainer.getItem(i);
            if (!itemstack2.isEmpty()) {
                if (itemstack2.is(IWSTags.SKATEBOARD_WHEELS)) {
                    if (!wheels2.isEmpty()) {
                        return false;
                    }
                    if(wheels1.isEmpty()){
                        wheels1 = itemstack2;
                    }else{
                        wheels2 = itemstack2;
                    }
                } else {
                    if (!itemstack2.is(IWSItemRegistry.SKATEBOARD.get())) {
                        return false;
                    }

                    if (!skateboard.isEmpty()) {
                        return false;
                    }

                    if (SkateboardData.fromStack(itemstack2).hasGripTape()) {
                        return false;
                    }

                    skateboard = itemstack2;
                }
            }
        }

        return !skateboard.isEmpty() && !wheels1.isEmpty() && wheels1.sameItem(wheels2);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer craftingContainer) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(craftingContainer.getContainerSize(), ItemStack.EMPTY);
        ItemStack skateboard = ItemStack.EMPTY;
        List<Integer> wheelPositions = new ArrayList<>();
        for(int i = 0; i < craftingContainer.getContainerSize(); ++i) {
            ItemStack itemstack2 = craftingContainer.getItem(i);
            if (itemstack2.is(IWSItemRegistry.SKATEBOARD.get())) {
                skateboard = itemstack2;
            }else if(!itemstack2.isEmpty()){
                wheelPositions.add(i);
            }
        }
        if(!skateboard.isEmpty()){
            ItemStack wheelCopy = new ItemStack(SkateboardData.fromStack(skateboard).getWheelType().getItemRegistryObject().get());
            for(int i : wheelPositions){
                nonnulllist.set(i, wheelCopy.copy());
            }
        }
        return nonnulllist;
    }


    public ItemStack assemble(CraftingContainer container) {
        ItemStack wheels = ItemStack.EMPTY;
        ItemStack skateboard = ItemStack.EMPTY;

        for(int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack itemstack2 = container.getItem(i);
            if (!itemstack2.isEmpty()) {
                if (itemstack2.is(IWSTags.SKATEBOARD_WHEELS)) {
                    wheels = itemstack2;
                } else if (itemstack2.is(IWSItemRegistry.SKATEBOARD.get())) {
                    skateboard = itemstack2.copy();
                }
            }
        }

        if (skateboard.isEmpty()) {
            return skateboard;
        } else {
            SkateboardData data = SkateboardData.fromStack(skateboard);
            data.setWheelType(SkateboardWheels.fromItem(wheels.getItem()));
            SkateboardData.setStackData(skateboard, data);
            return skateboard;
        }
    }

    public boolean canCraftInDimensions(int x, int y) {
        return x * y >= 3;
    }

    public RecipeSerializer<?> getSerializer() {
        return IWSRecipeRegistry.SKATEBOARD_SWAP_WHEELS.get();
    }
}