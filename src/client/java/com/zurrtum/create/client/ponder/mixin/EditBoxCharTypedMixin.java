package com.zurrtum.create.client.ponder.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import com.zurrtum.create.client.ponder.enums.PonderKeybinds;
import com.zurrtum.create.client.ponder.foundation.PonderTooltipHandler;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.CharacterEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Keeps the ponder key out of text fields while the hold-to-ponder prompt is on screen.
 * <p>
 * <b>What this fixes.</b> Ponder's prompt is drawn while the mouse hovers an item that has a scene, and
 * it is triggered by *polling* the key state ({@code InputConstants.isKeyDown}), so nothing ever consumed
 * the key press itself. In the creative inventory's search tab that meant holding W over a Create block
 * typed a "w" into the search box and the query stopped matching anything, instead of opening the scene -
 * reported by the human as "instead of activating the Ponder scene it goes into the search string".
 * <p>
 * The field is the right place to cut, not the screen: 26.3 dispatches a typed character from
 * {@code KeyboardHandler.charTyped} through {@code ContainerEventHandler.charTyped} to the focused
 * widget, and every text field in the game goes through {@link EditBox}. Cancelling here consumes the
 * character for whoever has focus, and only while {@link PonderTooltipHandler#isHoveringPonderable()} -
 * which is true exactly while the item tooltip for a ponderable item is being rendered, i.e. while the
 * prompt the player is answering is visible. Moving the mouse off the item clears it within a tick, so
 * typing "w" anywhere else is unaffected.
 */
@Mixin(EditBox.class)
public class EditBoxCharTypedMixin {

    @Inject(method = "charTyped(Lnet/minecraft/client/input/CharacterEvent;)Z", at = @At("HEAD"), cancellable = true)
    private void ponder$keepThePonderKeyOutOfTextFields(final CharacterEvent event, final CallbackInfoReturnable<Boolean> cir) {
        if (PonderKeybinds.PONDER.isUnbound() || !PonderTooltipHandler.isHoveringPonderable()) {
            return;
        }
        if (!InputConstants.isKeyDown(PonderKeybinds.PONDER.key.getValue())) {
            return;
        }
        // Consumed: the character belongs to the prompt, which reads the key state on its own.
        cir.setReturnValue(true);
    }
}
