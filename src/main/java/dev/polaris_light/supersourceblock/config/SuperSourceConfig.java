package dev.polaris_light.supersourceblock.config;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class SuperSourceConfig {
    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.IntValue EMPTY_TO_SUPER_BUCKETS;
    private static final ModConfigSpec.IntValue EMPTY_ITEM_TO_SUPER_ITEMS;
    private static final ModConfigSpec.BooleanValue ALLOW_ANY_FLUID_SOURCE;
    private static final ModConfigSpec.BooleanValue ALLOW_ANY_ITEM_SOURCE;
    private static final ModConfigSpec.BooleanValue SUPER_FLUID_USE_INTEGER_MAX_OUTPUT;
    private static final ModConfigSpec.BooleanValue SUPER_ITEM_USE_INTEGER_MAX_OUTPUT;
    private static final ModConfigSpec.IntValue SUPER_FLUID_OUTPUT_AMOUNT;
    private static final ModConfigSpec.IntValue SUPER_ITEM_OUTPUT_AMOUNT;
    private static final ModConfigSpec.IntValue SUPER_FLUID_OUTPUT_INTERVAL_TICKS;
    private static final ModConfigSpec.IntValue SUPER_ITEM_OUTPUT_INTERVAL_TICKS;
    private static final ModConfigSpec.IntValue EMPTY_CHEMICAL_TO_SUPER_AMOUNT;
    private static final ModConfigSpec.BooleanValue SUPER_CHEMICAL_USE_LONG_MAX_OUTPUT;
    private static final ModConfigSpec.IntValue SUPER_CHEMICAL_OUTPUT_AMOUNT;
    private static final ModConfigSpec.IntValue SUPER_CHEMICAL_OUTPUT_INTERVAL_TICKS;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_BLACKLIST;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_WHITELIST;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> FLUID_BLACKLIST;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> FLUID_WHITELIST;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> CHEMICAL_BLACKLIST;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> CHEMICAL_WHITELIST;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        EMPTY_TO_SUPER_BUCKETS = builder
            .comment("How many buckets (B) of one fluid are required for Empty Fluid Source Block to transform.")
            .defineInRange("empty_to_super_buckets", 512, 1, Integer.MAX_VALUE / 1000);
        EMPTY_ITEM_TO_SUPER_ITEMS = builder
            .comment("How many items of one type are required for Empty Item Source Block to transform.")
            .defineInRange("empty_item_to_super_items", 4096, 1, Integer.MAX_VALUE);
        EMPTY_CHEMICAL_TO_SUPER_AMOUNT = builder
            .comment("How much of one Mekanism chemical is required for Empty Chemical Source Block to transform.")
            .defineInRange("empty_chemical_to_super_amount", 4096, 1, Integer.MAX_VALUE);

        ALLOW_ANY_FLUID_SOURCE = builder
            .comment("If true, any fluid can transform empty fluid source when amount threshold is reached.")
            .define("allow_any_fluid_source", true);
        ALLOW_ANY_ITEM_SOURCE = builder
            .comment("If true, any item can transform empty item source when amount threshold is reached.")
            .define("allow_any_item_source", true);

        SUPER_FLUID_USE_INTEGER_MAX_OUTPUT = builder
            .comment("If true, super fluid source outputs Integer.MAX_VALUE per transfer.")
            .define("super_fluid_use_integer_max_output", true);
        SUPER_ITEM_USE_INTEGER_MAX_OUTPUT = builder
            .comment("If true, super item source outputs Integer.MAX_VALUE items per transfer attempt.")
            .define("super_item_use_integer_max_output", false);
        SUPER_CHEMICAL_USE_LONG_MAX_OUTPUT = builder
            .comment("If true, super chemical source outputs Long.MAX_VALUE chemical per transfer attempt.")
            .define("super_chemical_use_long_max_output", true);

        SUPER_FLUID_OUTPUT_AMOUNT = builder
            .comment("When super_fluid_use_integer_max_output is false, this is the fluid output amount per tick in mB.")
            .defineInRange("super_fluid_output_amount", 256000, 1, Integer.MAX_VALUE);
        SUPER_ITEM_OUTPUT_AMOUNT = builder
            .comment("When super_item_use_integer_max_output is false, this is the item output amount per tick.")
            .defineInRange("super_item_output_amount", 1, 1, Integer.MAX_VALUE);
        SUPER_CHEMICAL_OUTPUT_AMOUNT = builder
            .comment("When super_chemical_use_long_max_output is false, this is the chemical output amount per tick.")
            .defineInRange("super_chemical_output_amount", 256, 1, Integer.MAX_VALUE);

        SUPER_FLUID_OUTPUT_INTERVAL_TICKS = builder
            .comment("Output interval of super fluid source in ticks.")
            .defineInRange("super_fluid_output_interval_ticks", 1, 1, Integer.MAX_VALUE);
        SUPER_ITEM_OUTPUT_INTERVAL_TICKS = builder
            .comment("Output interval of super item source in ticks.")
            .defineInRange("super_item_output_interval_ticks", 20, 1, Integer.MAX_VALUE);
        SUPER_CHEMICAL_OUTPUT_INTERVAL_TICKS = builder
            .comment("Output interval of super chemical source in ticks.")
            .defineInRange("super_chemical_output_interval_ticks", 1, 1, Integer.MAX_VALUE);

        ITEM_BLACKLIST = builder
            .comment("Item blacklist. Supports item ids like 'minecraft:stone' and tags like '#c:stones'. Blacklist has priority over whitelist.")
            .defineListAllowEmpty("item_blacklist", List.of(), () -> "", SuperSourceConfig::isStringValue);
        ITEM_WHITELIST = builder
            .comment("Item whitelist. Empty means allow all items not blocked by the blacklist. Supports item ids and '#tag' entries.")
            .defineListAllowEmpty("item_whitelist", List.of(), () -> "", SuperSourceConfig::isStringValue);
        FLUID_BLACKLIST = builder
            .comment("Fluid blacklist. Supports fluid ids like 'minecraft:water' and tags like '#c:water'. Blacklist has priority over whitelist.")
            .defineListAllowEmpty("fluid_blacklist", List.of(), () -> "", SuperSourceConfig::isStringValue);
        FLUID_WHITELIST = builder
            .comment("Fluid whitelist. Empty means allow all fluids not blocked by the blacklist. Supports fluid ids and '#tag' entries.")
            .defineListAllowEmpty("fluid_whitelist", List.of(), () -> "", SuperSourceConfig::isStringValue);

        CHEMICAL_BLACKLIST = builder
            .comment("Chemical blacklist. Supports chemical ids like 'mekanism:oxygen' and tags like '#mekanism:water_vapor'. Blacklist has priority over whitelist.")
            .defineListAllowEmpty("chemical_blacklist", List.of(), () -> "", SuperSourceConfig::isStringValue);
        CHEMICAL_WHITELIST = builder
            .comment("Chemical whitelist. Empty means allow all chemicals not blocked by the blacklist. Supports chemical ids and '#tag' entries.")
            .defineListAllowEmpty("chemical_whitelist", List.of(), () -> "", SuperSourceConfig::isStringValue);
        SPEC = builder.build();
    }

    private SuperSourceConfig() {
    }

    public static int requiredBuckets() {
        return EMPTY_TO_SUPER_BUCKETS.get();
    }

    public static int requiredMb() {
        return Math.multiplyExact(requiredBuckets(), 1000);
    }

    public static int requiredItems() {
        return EMPTY_ITEM_TO_SUPER_ITEMS.get();
    }

    public static long requiredChemicalAmount() {
        return EMPTY_CHEMICAL_TO_SUPER_AMOUNT.get();
    }

    public static boolean allowAnyFluidSource() {
        return ALLOW_ANY_FLUID_SOURCE.get();
    }

    public static boolean allowAnyItemSource() {
        return ALLOW_ANY_ITEM_SOURCE.get();
    }

    public static List<? extends String> chemicalBlacklist() {
        return CHEMICAL_BLACKLIST.get();
    }

    public static List<? extends String> chemicalWhitelist() {
        return CHEMICAL_WHITELIST.get();
    }

    public static boolean superFluidUseIntegerMaxOutput() {
        return SUPER_FLUID_USE_INTEGER_MAX_OUTPUT.get();
    }

    public static boolean superItemUseIntegerMaxOutput() {
        return SUPER_ITEM_USE_INTEGER_MAX_OUTPUT.get();
    }

    public static int superFluidOutputAmount() {
        if (superFluidUseIntegerMaxOutput()) {
            return Integer.MAX_VALUE;
        }
        return SUPER_FLUID_OUTPUT_AMOUNT.get();
    }

    public static int superItemOutputAmount() {
        if (superItemUseIntegerMaxOutput()) {
            return Integer.MAX_VALUE;
        }
        return SUPER_ITEM_OUTPUT_AMOUNT.get();
    }

    public static int superFluidOutputIntervalTicks() {
        return SUPER_FLUID_OUTPUT_INTERVAL_TICKS.get();
    }

    public static int superItemOutputIntervalTicks() {
        return SUPER_ITEM_OUTPUT_INTERVAL_TICKS.get();
    }

    public static boolean superChemicalUseLongMaxOutput() {
        return SUPER_CHEMICAL_USE_LONG_MAX_OUTPUT.get();
    }

    public static long superChemicalOutputAmount() {
        if (superChemicalUseLongMaxOutput()) {
            return Long.MAX_VALUE;
        }
        return SUPER_CHEMICAL_OUTPUT_AMOUNT.get();
    }

    public static int superChemicalOutputIntervalTicks() {
        return SUPER_CHEMICAL_OUTPUT_INTERVAL_TICKS.get();
    }

    public static boolean isItemAllowed(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (ConfigListMatcher.matchesItem(stack, ITEM_BLACKLIST.get())) {
            return false;
        }
        List<? extends String> whitelist = ITEM_WHITELIST.get();
        return whitelist.isEmpty() || ConfigListMatcher.matchesItem(stack, whitelist);
    }

    public static boolean isFluidAllowed(FluidStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (ConfigListMatcher.matchesFluid(stack, FLUID_BLACKLIST.get())) {
            return false;
        }
        List<? extends String> whitelist = FLUID_WHITELIST.get();
        return whitelist.isEmpty() || ConfigListMatcher.matchesFluid(stack, whitelist);
    }

    private static boolean isStringValue(Object value) {
        return value instanceof String;
    }
}
