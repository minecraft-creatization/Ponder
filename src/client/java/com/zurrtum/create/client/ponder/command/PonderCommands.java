package com.zurrtum.create.client.ponder.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.zurrtum.create.client.catnip.gui.NavigatableSimiScreen;
import com.zurrtum.create.client.catnip.gui.ScreenOpener;
import com.zurrtum.create.client.ponder.command.PonderClientCommand;
import com.zurrtum.create.client.ponder.Ponder;
import com.zurrtum.create.client.ponder.foundation.PonderIndex;
import com.zurrtum.create.client.ponder.foundation.ui.PonderIndexScreen;
import com.zurrtum.create.client.ponder.foundation.ui.PonderTagIndexScreen;
import com.zurrtum.create.client.ponder.foundation.ui.PonderUI;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.resources.Identifier;

public class PonderCommands {
    public static void registerClient(CommandDispatcher<ClientSuggestionProvider> dispatcher) {
        dispatcher.register(PonderClientCommand.literal("ponder", context -> openScreen(context, new PonderTagIndexScreen()))
            .then(PonderClientCommand.literal(
                "reload", context -> {
                    PonderIndex.reload();
                    return Command.SINGLE_SUCCESS;
                }
            )).then(PonderClientCommand.literal("index", context -> openScreen(context, new PonderIndexScreen())))
            .then(PonderClientCommand.literal("tags", context -> openScreen(context, new PonderTagIndexScreen())))
            .then(PonderClientCommand.argument(
                "scene", IdentifierArgument.id(), context -> {
                    Identifier id = context.getArgument("scene", Identifier.class);
                    if (!PonderIndex.getSceneAccess().doScenesExistForId(id)) {
                        Ponder.LOGGER.error("Could not find ponder scenes for item: " + id);
                        return 0;
                    }
                    return openScreen(context, PonderUI.of(id));
                }
            )));
    }

    public static int openScreen(CommandContext<ClientSuggestionProvider> context, NavigatableSimiScreen screen) {
        context.getSource().minecraft.schedule(() -> ScreenOpener.transitionTo(screen));
        return Command.SINGLE_SUCCESS;
    }
}
