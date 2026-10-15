package com.zurrtum.create.client.ponder.mixin;

import com.mojang.brigadier.CommandDispatcher;
import com.zurrtum.create.client.ponder.command.PonderCommands;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Registers Ponder's client commands once the server's dispatcher has arrived - {@code /ponder} opens
 * the scene index, which is also the most direct way to reach Ponder's UI on a client that has no Create
 * scene of its own.
 * <p>
 * <b>Why Ponder does this itself.</b> The call was one line of Create's {@code ClientPacketListenerMixin},
 * next to Flywheel's and Create's own command registrations; without Create the command did not exist.
 * Create's mixin keeps the other two.
 */
@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Shadow
    private CommandDispatcher<ClientSuggestionProvider> commands;

    @Inject(method = "handleCommands(Lnet/minecraft/network/protocol/game/ClientboundCommandsPacket;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;commands:Lcom/mojang/brigadier/CommandDispatcher;", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    private void ponder$registerClientCommands(final ClientboundCommandsPacket packet, final CallbackInfo ci) {
        PonderCommands.registerClient(this.commands);
    }
}
