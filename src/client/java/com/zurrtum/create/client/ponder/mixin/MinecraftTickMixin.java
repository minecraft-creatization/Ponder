package com.zurrtum.create.client.ponder.mixin;

import com.zurrtum.create.client.ponder.Ponder;
import com.zurrtum.create.client.ponder.foundation.PonderTooltipHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Ponder's per-tick work, and the invalidation of its cached render buffers when the client changes
 * level.
 * <p>
 * <b>Both used to live in Create.</b> {@code PonderTooltipHandler.tick()} was one line of Create's
 * {@code MinecraftMixin#tickPre} and {@code Ponder.invalidateRenderers()} one line of its
 * {@code ClientLevelMixin}; without Create, the hold progress never advanced (the prompt can never
 * complete) and stale buffers from a previous level were kept. Doing the level check here rather than in
 * a level mixin keeps it in one place: the buffers belong to the level that built them, so a change of
 * {@code Minecraft#level} is exactly the moment they are worthless.
 */
@Mixin(Minecraft.class)
public abstract class MinecraftTickMixin {

    @Unique
    private ClientLevel ponder$lastLevel;

    @Inject(method = "tick()V", at = @At("HEAD"))
    private void ponder$tick(final CallbackInfo ci) {
        final Minecraft minecraft = (Minecraft) (Object) this;
        PonderTooltipHandler.tick();
        if (minecraft.level != this.ponder$lastLevel) {
            this.ponder$lastLevel = minecraft.level;
            Ponder.invalidateRenderers();
        }
    }
}
