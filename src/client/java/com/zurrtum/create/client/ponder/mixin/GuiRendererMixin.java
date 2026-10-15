package com.zurrtum.create.client.ponder.mixin;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.create.client.ponder.foundation.render.SceneRenderState;
import com.zurrtum.create.client.ponder.foundation.render.SceneRenderer;
import com.zurrtum.create.client.ponder.foundation.render.TitleTextRenderState;
import com.zurrtum.create.client.ponder.foundation.render.TitleTextRenderer;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Registers Ponder's picture-in-picture GUI renderers: the ponder scene itself and its title text.
 * <p>
 * <b>Why Ponder does this itself.</b> These two entries were part of Create's {@code GuiRendererMixin},
 * which registers Create's own GUI renderers on the same builder. Without Create the map had no
 * {@code SceneRenderer}, so even a Ponder UI that opened would draw an empty scene area - the second
 * half of the "as if there were no mods" report that started this work. Create's mixin keeps its own
 * entries.
 */
@Mixin(GuiRenderer.class)
public class GuiRendererMixin {

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap;builder()Lcom/google/common/collect/ImmutableMap$Builder;", remap = false))
    private ImmutableMap.Builder<Class<? extends PictureInPictureRenderState>, PictureInPictureRenderer<?>> ponder$addRenderers(
        final Operation<ImmutableMap.Builder<Class<? extends PictureInPictureRenderState>, PictureInPictureRenderer<?>>> original
    ) {
        final ImmutableMap.Builder<Class<? extends PictureInPictureRenderState>, PictureInPictureRenderer<?>> builder = original.call();
        builder.put(TitleTextRenderState.class, new TitleTextRenderer());
        builder.put(SceneRenderState.class, new SceneRenderer());
        return builder;
    }
}
