package dev.polaris_light.supersourceblock.data.reload;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.polaris_light.supersourceblock.SuperSourceBlockMod;
import dev.polaris_light.supersourceblock.data.ItemSourceRule;
import dev.polaris_light.supersourceblock.data.SourceRecipeManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;

public class ItemSourceReloadListener extends SimpleJsonResourceReloadListener {
    private static final String TYPE_ID = "super_source_block:item_source";

    public ItemSourceReloadListener() {
        super(new Gson(), "item_source");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        List<ItemSourceRule> rules = new ArrayList<>();
        for (Map.Entry<ResourceLocation, JsonElement> entry : object.entrySet()) {
            if (!entry.getValue().isJsonObject()) {
                continue;
            }
            JsonObject json = entry.getValue().getAsJsonObject();
            String type = GsonHelper.getAsString(json, "type", "");
            if (!TYPE_ID.equals(type)) {
                SuperSourceBlockMod.LOGGER.warn("Skipping {} because type is '{}', expected '{}'", entry.getKey(), type, TYPE_ID);
                continue;
            }
            Integer amount = json.has("amount") ? Integer.valueOf(GsonHelper.getAsInt(json, "amount")) : null;
            if (amount != null && amount <= 0) {
                SuperSourceBlockMod.LOGGER.warn("Invalid item_source amount in {}", entry.getKey());
                continue;
            }
            Integer outputAmount = json.has("output_amount") ? Integer.valueOf(GsonHelper.getAsInt(json, "output_amount")) : null;
            if (outputAmount != null && outputAmount <= 0) {
                SuperSourceBlockMod.LOGGER.warn("Invalid item_source output_amount in {}", entry.getKey());
                continue;
            }
            Boolean maxOutput = json.has("max_output") ? Boolean.valueOf(GsonHelper.getAsBoolean(json, "max_output")) : null;
            Integer interval = json.has("interval") ? Integer.valueOf(GsonHelper.getAsInt(json, "interval")) : null;
            if (interval != null && interval <= 0) {
                SuperSourceBlockMod.LOGGER.warn("Invalid item_source interval in {}", entry.getKey());
                continue;
            }

            boolean hasItem = json.has("item");
            boolean hasItemTag = json.has("item_tag");
            if (hasItem == hasItemTag) {
                SuperSourceBlockMod.LOGGER.warn("item_source {} must contain exactly one of 'item' or 'item_tag'", entry.getKey());
                continue;
            }

            if (hasItem) {
                ResourceLocation itemId = ResourceLocation.parse(GsonHelper.getAsString(json, "item"));
                Item item = BuiltInRegistries.ITEM.get(itemId);
                if (item == net.minecraft.world.item.Items.AIR) {
                    SuperSourceBlockMod.LOGGER.warn("Unknown item '{}' in {}", itemId, entry.getKey());
                    continue;
                }
                rules.add(new ItemSourceRule(item, null, amount, outputAmount, maxOutput, interval));
            } else {
                ResourceLocation tagId = ResourceLocation.parse(GsonHelper.getAsString(json, "item_tag"));
                TagKey<Item> tag = TagKey.create(Registries.ITEM, tagId);
                rules.add(new ItemSourceRule(null, tag, amount, outputAmount, maxOutput, interval));
            }
        }
        SourceRecipeManager.INSTANCE.setItemRules(rules);
        SuperSourceBlockMod.LOGGER.info("Loaded {} item_source recipes", rules.size());
    }
}
