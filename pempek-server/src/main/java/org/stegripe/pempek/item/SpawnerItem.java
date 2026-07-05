package org.stegripe.pempek.item;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SpawnerItem extends BlockItem {

    public SpawnerItem(Block block, Properties settings) {
        super(block, settings);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, Player player, ItemStack stack, BlockState state) {
        boolean handled = super.updateCustomBlockEntityTag(pos, level, player, stack, state);
        if (level.pempekConfig.silkTouchEnabled && player.getBukkitEntity().hasPermission("pempek.place.spawners")) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof SpawnerBlockEntity spawner) {
                CompoundTag customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
                Optional<String> mobTypeStringOptional = customData.getString("Pempek.mob_type");
                if (mobTypeStringOptional.isPresent()) {
                    EntityType.byString(mobTypeStringOptional.get()).ifPresent(type -> spawner.getSpawner().setEntityId(type, level, level.getRandom(), pos));
                } else if (customData.contains("Pempek.SpawnData")) {
                    customData.getCompound("Pempek.SpawnData")
                        .flatMap(spawnerData -> spawnerData.read("SpawnData", net.minecraft.world.level.SpawnData.CODEC))
                        .ifPresent(spawnData -> spawner.getSpawner().nextSpawnData = spawnData);
                }
            }
        }
        return handled;
    }
}
