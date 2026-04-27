package dev.polaris_light.supersourceblock.data;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public record ItemSourceRule(Item item, TagKey<Item> itemTag, Integer amount, Integer outputAmount, Boolean maxOutput, Integer interval) {
    public boolean hasItem() {
        return item != null;
    }

    public boolean hasTag() {
        return itemTag != null;
    }
}
