package dev.polaris_light.supersourceblock.data;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public record FluidSourceRule(Fluid fluid, TagKey<Fluid> fluidTag, Integer amount, Integer outputAmount, Boolean maxOutput, Integer interval) {
    public boolean hasFluid() {
        return fluid != null;
    }

    public boolean hasTag() {
        return fluidTag != null;
    }
}
