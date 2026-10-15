package com.zurrtum.create.client.catnip.event;

import com.zurrtum.create.client.ponder.foundation.PonderResourceReloader;

import com.zurrtum.create.client.catnip.lang.LangNumberFormat;
import com.zurrtum.create.client.ponder.Ponder;
import com.zurrtum.create.client.ponder.foundation.PonderIndex;
import net.minecraft.server.packs.resources.ResourceManager;

public class ClientResourceReloadListener extends PonderResourceReloader {
    public ClientResourceReloadListener() {
        super("ponder");
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        LangNumberFormat.numberFormat.update();
        Ponder.invalidateRenderers();
        // Builds the scene and tag registries from every registered plugin, clearing them first - which
        // is what makes it correct to run on every reload rather than once. Ponder has to do this itself:
        // it used to be Create's MinecraftMixin calling PonderIndex.registerAll() on Minecraft.run(), so a
        // standalone Ponder never built an index at all (no "Registering Ponder Scenes" line, no scenes),
        // and with Create present the entries were registered without the clear() that reload() performs.
        PonderIndex.reload();
    }
}
