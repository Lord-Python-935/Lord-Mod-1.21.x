package net.jakob.lordmod.item.custom;

import net.jakob.lordmod.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Objects;

public class ChiselItem extends Item {
    public static final Map<Block, Block> CHISEL_MAP =
            Map.of(
                    Blocks.COBBLESTONE, Blocks.STONE,
                    Blocks.STONE, Blocks.STONE_BRICKS,
                    Blocks.STONE_BRICKS, Blocks.STONE_BRICK_STAIRS,
                    Blocks.STONE_BRICK_STAIRS, Blocks.STONE_BRICK_SLAB,
                    Blocks.STONE_BRICK_SLAB, Blocks.STONE_BRICKS,
                    Blocks.ANDESITE, Blocks.ANDESITE_STAIRS,
                    Blocks.ANDESITE_STAIRS, Blocks.ANDESITE_SLAB,
                    Blocks.ANDESITE_SLAB, Blocks.ANDESITE_WALL,
                    Blocks.ANDESITE_WALL, Blocks.ANDESITE,
                    ModBlocks.PINK_GARNET_BLOCK, Blocks.DIAMOND_BLOCK
            );

    public ChiselItem(Settings settings) {
        super(settings);
    }

   @Override
   public ActionResult useOnBlock(ItemUsageContext context){
        World world = context.getWorld();
        Block clickedBlock = world.getBlockState(context.getBlockPos()).getBlock();

        if(CHISEL_MAP.containsKey(clickedBlock)){
            if(!world.isClient){
                world.setBlockState(context.getBlockPos(), CHISEL_MAP.get(clickedBlock).getDefaultState());

                context.getStack().damage(1, ((ServerWorld) world), ((ServerPlayerEntity) context.getPlayer()),
                        item -> Objects.requireNonNull(context.getPlayer()).sendEquipmentBreakStatus(item, EquipmentSlot.MAINHAND));

                world.playSound(null, context.getBlockPos(), SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS);
            }
        }


        return super.useOnBlock(context);
   }

}