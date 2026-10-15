package com.zurrtum.create.client.ponder.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.SharedConstants;
import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.ResourceMetadata;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Mounts Ponder's own jar resources as a built-in client resource pack.
 *
 * <p>Ponder used to live inside Create's jar, and Create's {@code ClientPackSourceMixin} exposes the
 * {@code ponder} namespace from its own root paths, which mounted these assets for free. After the
 * split the assets sit in Ponder's jar, so Ponder has to mount them itself: without this mixin the
 * client resource manager never sees {@code assets/ponder/**}, and every Ponder shader fails
 * ("Couldn't find source for VERTEX shader (ponder:entity_block)"), which makes
 * {@code ShaderManager.apply} throw "Failed to load required shader programs" and aborts the whole
 * resource reload.
 *
 * <p>Only Ponder's own namespace is exposed -- Create's pack still exposes {@code minecraft},
 * {@code flywheel}, {@code vanillin} and {@code fabric} from Create's jar, and Flywheel mounts
 * {@code flywheel} from its own. The pack id, the {@link KnownPack} version string and the
 * {@code BOTTOM} position are deliberately identical to Create's arrangement so pack ordering does
 * not change.
 *
 * <p>Gated on Fabric API being absent: when it is present, its {@code fabric-resource-loader-v0/v1}
 * module registers a built-in pack for every mod, and a second registration would duplicate the pack
 * in the list. This mirrors Create's own gate rather than inventing a second policy.
 */
@Mixin(ClientPackSource.class)
public abstract class ClientPackSourceMixin {
    @Inject(method = "populatePackList(Ljava/util/function/BiConsumer;)V", at = @At("TAIL"))
    private void ponder$addBuiltinPack(BiConsumer<String, Function<String, Pack>> discoveredPacks, CallbackInfo ci) {
        FabricLoader loader = FabricLoader.getInstance();
        if (loader.isModLoaded("fabric-api")) {
            return;
        }
        ModContainer mod = loader.getModContainer("ponder").orElseThrow();
        List<Path> rootPaths = mod.getRootPaths();
        ModMetadata metadata = mod.getMetadata();
        discoveredPacks.accept(
            "ponder", id -> {
                String directory = PackType.CLIENT_RESOURCES.getDirectory();
                PackLocationInfo info = createInfo(id, metadata.getName(), directory, metadata.getVersion().getFriendlyString());
                ResourceMetadata meta = createMeta(Component.literal("Ponder built-in resources"));
                PackSelectionConfig position = new PackSelectionConfig(true, Pack.Position.BOTTOM, false);
                return createPacket(rootPaths, directory, info, meta, position, "ponder");
            }
        );
    }

    @Unique
    private static PackLocationInfo createInfo(String id, String name, String path, String version) {
        return new PackLocationInfo(
            id,
            Component.literal(name),
            PackSource.BUILT_IN,
            Optional.of(new KnownPack(id, path, version))
        );
    }

    @Unique
    private static ResourceMetadata createMeta(Component description) {
        return ResourceMetadata.of(
            PackMetadataSection.CLIENT_TYPE,
            new PackMetadataSection(
                description,
                SharedConstants.getCurrentVersion().packVersion(PackType.CLIENT_RESOURCES).minorRange()
            )
        );
    }

    @Unique
    private static Pack createPacket(
        List<Path> rootPaths,
        String directory,
        PackLocationInfo info,
        ResourceMetadata meta,
        PackSelectionConfig position,
        String... namespace
    ) {
        VanillaPackResourcesBuilder builder = new VanillaPackResourcesBuilder().setMetadata(meta)
            .exposeNamespace(namespace);
        for (Path path : rootPaths) {
            builder.pushAssetPath(PackType.CLIENT_RESOURCES, path.resolve(directory));
        }
        VanillaPackResources pack = builder.build(info);
        return Pack.readMetaAndCreate(info, pack.asResourcesSupplier(), PackType.CLIENT_RESOURCES, position);
    }
}
