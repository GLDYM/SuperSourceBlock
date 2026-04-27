package dev.polaris_light.supersourceblock.data.reload;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.polaris_light.supersourceblock.SuperSourceBlockMod;
import dev.polaris_light.supersourceblock.data.FluidSourceRule;
import dev.polaris_light.supersourceblock.data.SourceRecipeManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.core.registries.Registries;

public class FluidSourceReloadListener extends SimpleJsonResourceReloadListener {
    private static final String TYPE_ID = "super_source_block:fluid_source";

    public FluidSourceReloadListener() {
        super(new Gson(), "fluid_source");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        List<FluidSourceRule> rules = new ArrayList<>();
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
                SuperSourceBlockMod.LOGGER.warn("Invalid fluid_source amount in {}", entry.getKey());
                continue;
            }
            Integer outputAmount = json.has("output_amount") ? Integer.valueOf(GsonHelper.getAsInt(json, "output_amount")) : null;
            if (outputAmount != null && outputAmount <= 0) {
                SuperSourceBlockMod.LOGGER.warn("Invalid fluid_source output_amount in {}", entry.getKey());
                continue;
            }
            Boolean maxOutput = json.has("max_output") ? Boolean.valueOf(GsonHelper.getAsBoolean(json, "max_output")) : null;
            Integer interval = json.has("interval") ? Integer.valueOf(GsonHelper.getAsInt(json, "interval")) : null;
            if (interval != null && interval <= 0) {
                SuperSourceBlockMod.LOGGER.warn("Invalid fluid_source interval in {}", entry.getKey());
                continue;
            }

            boolean hasFluid = json.has("fluid");
            boolean hasFluidTag = json.has("fluid_tag");
            if (hasFluid == hasFluidTag) {
                SuperSourceBlockMod.LOGGER.warn("fluid_source {} must contain exactly one of 'fluid' or 'fluid_tag'", entry.getKey());
                continue;
            }

            if (hasFluid) {
                ResourceLocation fluidId = ResourceLocation.parse(GsonHelper.getAsString(json, "fluid"));
                Fluid fluid = BuiltInRegistries.FLUID.get(fluidId);
                if (fluid == net.minecraft.world.level.material.Fluids.EMPTY) {
                    SuperSourceBlockMod.LOGGER.warn("Unknown fluid '{}' in {}", fluidId, entry.getKey());
                    continue;
                }
                rules.add(new FluidSourceRule(fluid, null, amount, outputAmount, maxOutput, interval));
            } else {
                ResourceLocation tagId = ResourceLocation.parse(GsonHelper.getAsString(json, "fluid_tag"));
                TagKey<Fluid> tag = TagKey.create(Registries.FLUID, tagId);
                rules.add(new FluidSourceRule(null, tag, amount, outputAmount, maxOutput, interval));
            }
        }
        SourceRecipeManager.INSTANCE.setFluidRules(rules);
        SuperSourceBlockMod.LOGGER.info("Loaded {} fluid_source recipes", rules.size());
    }
}
