package com.zurrtum.create.client.ponder.api.platform;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.FluidModel;
// 26.3 moved the model API under block.dispatch (net.minecraft.client.renderer.block.model.BlockStateModel
// does not exist); the surrounding ponder sources use these same two paths.
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * The hooks Ponder needs from the mod that ships it.
 *
 * <p>Upstream ships catnip and ponder inside the Ponder repository, so the framework there may freely
 * name Create's internals. This port instead keeps Ponder a standalone module, so the seven places
 * where the framework genuinely needed Create are declared here and implemented by Create at
 * initialisation. Nothing in this module imports {@code com.zurrtum.create} outside catnip's
 * dependency on Flywheel.
 *
 * <p>Every default is a safe no-op/vanilla fallback, so the module is fully functional when loaded
 * without Create (Ponder scenes just render with vanilla fluid tinting and no custom block models).
 */
public interface PonderPlatform {
    PonderPlatform INSTANCE = new PonderPlatform() {
    };

    /**
     * Fluid tint, previously AllFluidConfigs#getTint. Create's own fluids carry tint sources that
     * vanilla does not know about.
     */
    default int getFluidTint(
        @Nullable BlockAndTintGetter level,
        @Nullable BlockPos pos,
        BlockState blockState,
        FluidModel model,
        Fluid fluid,
        DataComponentPatch components
    ) {
        return -1;
    }

    /**
     * Block-state model parts, previously WrapperBlockStateModel#addPartsWithInfo. Create wraps
     * models whose geometry depends on the world position (copycats, etc.).
     */
    default void addPartsWithInfo(
        BlockStateModel model,
        BlockAndTintGetter world,
        BlockPos pos,
        BlockState state,
        RandomSource random,
        List<BlockStateModelPart> parts
    ) {
        model.collectParts(random, parts);
    }

    /**
     * Whether this entity should be kept alive in a ponder scene after it stops being "alive".
     * Previously an {@code instanceof EjectorItemEntity} check: Create's ejector items stay until
     * they are explicitly removed, because they are mid-flight props rather than living entities.
     */
    default boolean keepTickingEntity(Entity entity) {
        return false;
    }

    /**
     * Currently unused by the framework but kept as the single seam for Create's logger, so a
     * future diagnostic does not reintroduce the import.
     */
    default void debug(String message) {
    }
}
