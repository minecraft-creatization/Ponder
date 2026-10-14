package com.zurrtum.create.client.ponder.mixin;

import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.zurrtum.create.client.ponder.foundation.render.DynamicTransformsHolder;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.QuadParticleFeatureRenderer;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(QuadParticleFeatureRenderer.class)
public class QuadParticleFeatureRendererMixin implements DynamicTransformsHolder {
    @Shadow
    private @Nullable GpuBufferSlice dynamicTransforms;
    @Unique
    private GpuBufferSlice dynamicTransformsOverride;

    @Override
    public void ponder$updateTransforms(@Nullable GpuBufferSlice dynamicTransforms) {
        dynamicTransformsOverride = dynamicTransforms;
    }

    @Inject(method = "finishPrepare(Lnet/minecraft/client/renderer/feature/FeatureFrameContext;)V", at = @At("HEAD"), cancellable = true)
    private void init(FeatureFrameContext context, CallbackInfo ci) {
        if (dynamicTransformsOverride != null) {
            dynamicTransforms = dynamicTransformsOverride;
            ci.cancel();
        }
    }

    // 26.3 removed RenderSystem.outputColorTextureOverride/outputDepthTextureOverride. Particles now draw into
    // whatever RenderPass the caller opened, so ponder redirects its scene simply by rendering through its own
    // pass (see SceneRenderer) and this mixin no longer needs to rewrite the render targets.
}
