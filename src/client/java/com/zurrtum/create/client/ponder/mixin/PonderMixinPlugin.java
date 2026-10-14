package com.zurrtum.create.client.ponder.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Ponder's mixin gate.
 *
 * <p>QuadRenderHelperMixin calls Sodium's {@code SpriteUtil} directly, so it may only be applied when
 * Sodium is actually present. Listing it in ponder.mixins.json would apply it unconditionally and fail
 * to load on a vanilla client; this is the same arrangement Create uses for the same class (there it is
 * added by MixinPlugin under {@code isModLoaded("sodium")} and is deliberately absent from the JSON
 * array). Everything else Ponder ships injects into vanilla only and is safe to list in the JSON.
 */
public class PonderMixinPlugin implements IMixinConfigPlugin {
    private final List<String> mixins = new ArrayList<>();

    @Override
    public void onLoad(String mixinPackage) {
        if (FabricLoader.getInstance().isModLoaded("sodium")) {
            mixins.add("QuadRenderHelperMixin");
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return mixins;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}