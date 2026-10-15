package com.zurrtum.create.client.ponder.mixin;

import com.zurrtum.create.client.catnip.render.CustomRenderType;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * Makes vanilla {@link RenderType} implement {@link CustomRenderType}, which is what the
 * {@code create$markPriority()} flag Ponder sets in {@code PonderRenderTypes} and reads back in
 * {@link RenderTypeFeatureRendererGroupMixin} needs in order to exist at all.
 * <p>
 * <b>Why this lives in Ponder and not in Create.</b> It used to be Create's
 * {@code com.zurrtum.create.client.mixin.RenderTypeMixin}, which both implemented this interface and
 * added Create's own solid-blend behaviour. Without Create on the classpath the interface was therefore
 * never added, and the first rendered frame died with
 * <i>"ClassCastException: class net.minecraft.client.renderer.rendertype.RenderType cannot be cast to
 * class com.zurrtum.create.client.catnip.render.CustomRenderType"</i> inside Ponder's own group mixin -
 * a hard, hidden dependency of Ponder on Create that a standalone run
 * ({@code ponder-everywhere-26.3}, whose build takes flywheel and ponder only) exposed immediately.
 * Create's mixin now carries only its solid-blend behaviour, so with Create present both mixins apply
 * to the same target and neither duplicates the interface.
 */
@Mixin(RenderType.class)
public class RenderTypeMixin implements CustomRenderType {

    @Unique
    private boolean ponder$priority;

    @Override
    public boolean create$isPriority() {
        return this.ponder$priority;
    }

    @Override
    public void create$markPriority() {
        this.ponder$priority = true;
    }
}
