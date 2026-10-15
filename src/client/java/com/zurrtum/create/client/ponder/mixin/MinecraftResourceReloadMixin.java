package com.zurrtum.create.client.ponder.mixin;

import com.zurrtum.create.client.ponder.Ponder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Registers Ponder's resource reload listener with the client resource manager.
 * <p>
 * <b>Why Ponder does this itself.</b> It used to be one line in Create's {@code MinecraftMixin}
 * ({@code resourceManager.registerReloadListener(Ponder.RESOURCE_RELOAD_LISTENER)}), which made Ponder's
 * scene index - and therefore every Ponder scene - silently dependent on Create being installed. In a
 * standalone run ({@code ponder-everywhere-26.3}, which takes flywheel and ponder only) nothing
 * registered it: the log showed no {@code Registering Ponder Scenes} line at all and the addon's scene
 * never appeared. The same class of hidden dependency as the {@code CustomRenderType} interface in
 * {@link RenderTypeMixin}, found by the same run.
 * <p>
 * The anchor is the {@code createReload} call, because it sits inside {@code Minecraft}'s constructor,
 * after the resource manager exists and before the reload it starts. Anchoring on the constructor's tail
 * compiles and looks harmless but registers the listener after the initial - and most important - reload
 * has already been created, so it would never run; Create's own port carries that lesson in a comment
 * next to its identical anchor, and Veil's slice uses it too.
 */
@Mixin(Minecraft.class)
public abstract class MinecraftResourceReloadMixin {

    @Shadow
    @Final
    private ReloadableResourceManager resourceManager;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/ReloadableResourceManager;createReload(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/concurrent/CompletableFuture;Ljava/util/List;)Lnet/minecraft/server/packs/resources/ReloadInstance;"))
    private void ponder$registerResourceReloadListener(final GameConfig config, final CallbackInfo ci) {
        this.resourceManager.registerReloadListener(Ponder.RESOURCE_RELOAD_LISTENER);
    }
}
