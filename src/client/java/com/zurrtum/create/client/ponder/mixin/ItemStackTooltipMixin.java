package com.zurrtum.create.client.ponder.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.zurrtum.create.client.ponder.foundation.PonderTooltipHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

/**
 * Adds the hold-to-ponder hint to an item's tooltip - and, with it, tells
 * {@link PonderTooltipHandler} which stack the mouse is over, which is the only way Ponder ever learns
 * that a scene is available for it.
 * <p>
 * <b>Why Ponder does this itself.</b> It used to be a line in Create's {@code ItemStackMixin}, and it is
 * the reason a standalone client looked like "Minecraft with no mods at all": no hint appeared, and the
 * hold-to-ponder progress could never start, even though Ponder's index was built and the plugin had
 * registered its scene. Reported by the human exactly that way after the first standalone scene test.
 */
@Mixin(ItemStack.class)
public abstract class ItemStackTooltipMixin {

    @ModifyReturnValue(method = "getTooltipLines", at = @At("RETURN"))
    private List<Component> ponder$addPonderHint(final List<Component> tooltip) {
        PonderTooltipHandler.addToTooltip(tooltip, (ItemStack) (Object) this);
        return tooltip;
    }
}
