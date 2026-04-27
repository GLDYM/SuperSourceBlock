package dev.polaris_light.supersourceblock.data;

import dev.polaris_light.supersourceblock.config.SuperSourceConfig;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public final class SourceRecipeManager {
    public static final SourceRecipeManager INSTANCE = new SourceRecipeManager();

    private final List<FluidSourceRule> fluidRules = new CopyOnWriteArrayList<>();
    private final List<ItemSourceRule> itemRules = new CopyOnWriteArrayList<>();

    private SourceRecipeManager() {
    }

    public void setFluidRules(List<FluidSourceRule> rules) {
        this.fluidRules.clear();
        this.fluidRules.addAll(rules);
    }

    public void setItemRules(List<ItemSourceRule> rules) {
        this.itemRules.clear();
        this.itemRules.addAll(rules);
    }

    public FluidSourceRule findFluidRule(FluidStack stack, int currentAmount) {
        if (stack.isEmpty()) {
            return null;
        }
        Fluid fluid = stack.getFluid();
        for (FluidSourceRule rule : this.fluidRules) {
            int required = rule.amount() != null ? rule.amount() : SuperSourceConfig.requiredMb();
            if (currentAmount < required) {
                continue;
            }
            if (rule.hasFluid() && rule.fluid() == fluid) {
                return rule;
            }
            if (rule.hasTag() && fluid.builtInRegistryHolder().is(rule.fluidTag())) {
                return rule;
            }
        }
        return null;
    }

    public ItemSourceRule findItemRule(ItemStack stack, int currentAmount) {
        if (stack.isEmpty()) {
            return null;
        }
        for (ItemSourceRule rule : this.itemRules) {
            int required = rule.amount() != null ? rule.amount() : SuperSourceConfig.requiredItems();
            if (currentAmount < required) {
                continue;
            }
            if (rule.hasItem() && stack.is(rule.item())) {
                return rule;
            }
            if (rule.hasTag() && stack.is(rule.itemTag())) {
                return rule;
            }
        }
        return null;
    }
}
