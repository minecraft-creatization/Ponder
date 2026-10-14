package com.zurrtum.create.client.ponder.foundation;

import com.zurrtum.create.client.ponder.Ponder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

/**
 * Ponder's own resource-reload listener base, previously Create's CreateResourceReloader. Inlined
 * so the module does not depend on Create.
 */
public abstract class PonderResourceReloader implements ResourceManagerReloadListener {
    private final Identifier id;

    public PonderResourceReloader(String id) {
        this.id = Identifier.fromNamespaceAndPath(Ponder.MOD_ID, id);
    }

    public PonderResourceReloader(Identifier id) {
        this.id = id;
    }

    public Identifier getId() {
        return id;
    }
}
