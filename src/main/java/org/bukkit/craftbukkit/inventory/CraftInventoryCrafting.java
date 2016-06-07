package org.bukkit.craftbukkit.inventory;

import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.util.Java15Compat;

import fewizz.svarka.inventory.IBukkitInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.crafting.IRecipe;

public class CraftInventoryCrafting extends CraftInventory implements CraftingInventory {
    private final IBukkitInventory resultInventory;

    public CraftInventoryCrafting(InventoryCrafting inventory, IBukkitInventory resultInventory) {
        super(inventory);
        this.resultInventory = resultInventory;
    }

    public IBukkitInventory getResultInventory() {
        return resultInventory;
    }

    public IBukkitInventory getMatrixInventory() {
        return inventory;
    }

    @Override
    public int getSize() {
        return getResultInventory().getSizeInventory() + getMatrixInventory().getSizeInventory();
    }

    @Override
    public void setContents(ItemStack[] items) {
        int resultLen = getResultInventory().getContents().length;
        int len = getMatrixInventory().getContents().length + resultLen;
        if (len > items.length) {
            throw new IllegalArgumentException("Invalid inventory size; expected " + len + " or less");
        }
        setContents(items[0], Java15Compat.Arrays_copyOfRange(items, 1, items.length));
    }

    @Override
    public ItemStack[] getContents() {
        ItemStack[] items = new ItemStack[getSize()];
        net.minecraft.item.ItemStack[] mcResultItems = getResultInventory().getContents();

        int i = 0;
        for (i = 0; i < mcResultItems.length; i++ ) {
            items[i] = CraftItemStack.asCraftMirror(mcResultItems[i]);
        }

        net.minecraft.item.ItemStack[] mcItems = getMatrixInventory().getContents();

        for (int j = 0; j < mcItems.length; j++) {
            items[i + j] = CraftItemStack.asCraftMirror(mcItems[j]);
        }

        return items;
    }

    public void setContents(ItemStack result, ItemStack[] contents) {
        setResult(result);
        setMatrix(contents);
    }

    @Override
    public CraftItemStack getItem(int index) {
        if (index < getResultInventory().getSizeInventory()) {
        	net.minecraft.item.ItemStack item = getResultInventory().getStackInSlot(index);
            return item == null ? null : CraftItemStack.asCraftMirror(item);
        } else {
        	net.minecraft.item.ItemStack item = getMatrixInventory().getStackInSlot(index - getResultInventory().getSizeInventory());
            return item == null ? null : CraftItemStack.asCraftMirror(item);
        }
    }

    @Override
    public void setItem(int index, ItemStack item) {
        if (index < getResultInventory().getSizeInventory()) {
            getResultInventory().setInventorySlotContents(index, (item == null ? null : CraftItemStack.asNMSCopy(item)));
        } else {
            getMatrixInventory().setInventorySlotContents((index - getResultInventory().getSizeInventory()), (item == null ? null : CraftItemStack.asNMSCopy(item)));
        }
    }

    public ItemStack[] getMatrix() {
    	net.minecraft.item.ItemStack[] matrix = getMatrixInventory().getContents();
        ItemStack[] items = new ItemStack[matrix.length];

        for (int i = 0; i < matrix.length; i++) {
            items[i] = CraftItemStack.asCraftMirror(matrix[i]);
        }

        return items;
    }

    public ItemStack getResult() {
    	net.minecraft.item.ItemStack item = getResultInventory().getStackInSlot(0);
        if(item != null) return CraftItemStack.asCraftMirror(item);
        return null;
    }

    public void setMatrix(ItemStack[] contents) {
        if (getMatrixInventory().getContents().length > contents.length) {
            throw new IllegalArgumentException("Invalid inventory size; expected " + getMatrixInventory().getContents().length + " or less");
        }

        net.minecraft.item.ItemStack[] mcItems = getMatrixInventory().getContents();

        for (int i = 0; i < mcItems.length; i++) {
            if (i < contents.length) {
                ItemStack item = contents[i];
                if (item == null || item.getTypeId() <= 0) {
                    getMatrixInventory().setInventorySlotContents(i, null);
                } else {
                    getMatrixInventory().setInventorySlotContents(i, CraftItemStack.asNMSCopy(item));
                }
            } else {
                getMatrixInventory().setInventorySlotContents(i, null);
            }
        }
    }

    public void setResult(ItemStack item) {
    	net.minecraft.item.ItemStack[] contents = getResultInventory().getContents();
        if (item == null || item.getTypeId() <= 0) {
            contents[0] = null;
        } else {
            contents[0] = CraftItemStack.asNMSCopy(item);
        }
    }

    public Recipe getRecipe() {
        //IRecipe recipe = ((InventoryCrafting)getInventory()).currentRecipe; TODO!!!
        //return recipe == null ? null : recipe.toBukkitRecipe();
    	return null;
    }
}
