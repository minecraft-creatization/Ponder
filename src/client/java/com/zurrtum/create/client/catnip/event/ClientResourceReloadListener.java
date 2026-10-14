package com.zurrtum.create.client.catnip.event;

import com.zurrtum.create.client.ponder.foundation.PonderResourceReloader;

import com.zurrtum.create.client.catnip.lang.LangNumberFormat;
import com.zurrtum.create.client.ponder.Ponder;
import net.minecraft.server.packs.resources.ResourceManager;

public class ClientResourceReloadListener extends PonderResourceReloader {
    public ClientResourceReloadListener() {
        super("ponder");
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        LangNumberFormat.numberFormat.update();
        Ponder.invalidateRenderers();
    }
}
