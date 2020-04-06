package com.codetaylor.mc.artisanautomation.modules.automator.item;

import com.codetaylor.mc.artisanworktables.api.internal.reference.Tags;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

public class ItemUpgrade
    extends Item {

  public static final String NAME_SPEED = "upgrade_speed";
  public static final String NAME_FLUID_CAPACITY = "upgrade_fluid_capacity";
  public static final String NAME_ENERGY_CAPACITY = "upgrade_energy_capacity";
  public static final String NAME_AUTO_EXPORT_ITEMS = "upgrade_auto_export_items";
  public static final String NAME_AUTO_IMPORT_ITEMS = "upgrade_auto_import_items";
  public static final String NAME_AUTO_IMPORT_EXPORT_ITEMS = "upgrade_auto_import_export_items";

  public ItemUpgrade() {

    this.setMaxStackSize(1);
  }

  public static boolean isUpgrade(ItemStack itemStack) {

    NBTTagCompound itemTag = itemStack.getTagCompound();

    if (itemStack.isEmpty() || itemTag == null) {
      return false;
    }

    if (!itemTag.hasKey(Tags.TAG_ARTISAN_WORKTABLES)) {
      return false;
    }

    NBTTagCompound artisanTag = itemTag.getCompoundTag(Tags.TAG_ARTISAN_WORKTABLES);

    if (!artisanTag.hasKey(Tags.TAG_UPGRADE)) {
      return false;
    }

    return true;
  }

  @Nullable
  public static NBTTagCompound getUpgradeTag(ItemStack itemStack) {

    if (ItemUpgrade.isUpgrade(itemStack)) {
      NBTTagCompound itemTag = itemStack.getTagCompound();
      return (itemTag == null) ? null : itemTag
          .getCompoundTag(Tags.TAG_ARTISAN_WORKTABLES)
          .getCompoundTag(Tags.TAG_UPGRADE);
    }

    return null;
  }
}
