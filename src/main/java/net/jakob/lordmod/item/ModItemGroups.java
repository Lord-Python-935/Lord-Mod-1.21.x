package net.jakob.lordmod.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.jakob.lordmod.LordMod;
import net.jakob.lordmod.block.ModBlocks;
import net.jakob.lordmod.block.custom.MagicBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup PINK_GARNET_ITEMS_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(LordMod.MOD_ID, "pink_garnet_items"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.PINK_GARNET))
                    .displayName(Text.translatable("itemgroup.lordmod.pink_garnet_items"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.PINK_GARNET);
                        entries.add(ModItems.RAW_PINK_GARNET);

                        entries.add(ModItems.CHISEL);
                    }).build());

    public static final ItemGroup PINK_GARNET_BLOCKS_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(LordMod.MOD_ID, "pink_garnet_blocks"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModBlocks.PINK_GARNET_BLOCK))
                    .displayName(Text.translatable("itemgroup.lordmod.pink_garnet_blocks"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModBlocks.PINK_GARNET_BLOCK);
                        entries.add(ModBlocks.RAW_PINK_GARNET_BLOCK);

                        entries.add(ModBlocks.PINK_GARNET_ORE);
                        entries.add(ModBlocks.PINK_GARNET_DEEPSLATE_ORE);

                        entries.add(ModBlocks.MAGIC_BLOCK);
                    }).build());

    public static final ItemGroup URAN_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(LordMod.MOD_ID, "uran_blocks"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModBlocks.URANIUM_ORE))
                    .displayName(Text.translatable("itemgroup.lordmod.uran_blocks"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModBlocks.URANIUM_ORE);
                        entries.add(ModItems.RAW_URANIUM);
                        entries.add(ModItems.URANIUM_INGOT);

                        entries.add(ModBlocks.MAGIC_BLOCK);
                        entries.add(ModBlocks.URANIUM_BLOCK);
                    }).build());

    public static void registerItemGroups() {
        LordMod.LOGGER.info("Registering Item Groups for " + LordMod.MOD_ID);
    }
}
