package com.zurrtum.create.client.ponder.foundation.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Scene-scoped sound bookkeeping for the Ponder UI.
 *
 * <p>Previously this was Create's {@code client.foundation.sound.SoundScapes}, which tracks sounds
 * playing in the real world in order to decide which positional audio sources to start. A ponder
 * scene has no real world: it renders a schematic and plays its own sounds, so all the UI needs is
 * "which pitch groups are currently sounding", so it can pick a matching one when it plays a sound.
 * Keeping that here removes the last non-catnip Create dependency from this module while preserving
 * the UI behaviour, and it is deliberately sound-source-agnostic.
 */
public class PonderSoundScapes {
    private static final int MAX_AMBIENT_SOURCE_DISTANCE = 16;
    public static final PonderSoundScapes DEFAULT = new PonderSoundScapes(false);
    public static PonderSoundScapes current = DEFAULT;

    private final boolean includePaused;
    private final Map<BlockPos, Float> sounds = new HashMap<>();

    public PonderSoundScapes(boolean includePaused) {
        this.includePaused = includePaused;
        current = this;
    }

    public static void setInstance(@Nullable PonderSoundScapes instance) {
        current = instance == null ? DEFAULT : instance;
    }

    /** Registers a sound as playing at a position, keyed by its pitch. */
    public void play(BlockPos pos, float pitch) {
        sounds.put(pos, pitch);
    }

    public void tick() {
        if (!includePaused && Minecraft.getInstance().isPaused()) {
            return;
        }
        if (sounds.isEmpty()) {
            return;
        }
        // Scene sounds are one-shot: they are re-registered by the instruction that plays them, so
        // pruning is only needed to drop sounds whose source has moved far from the camera.
        sounds.entrySet().removeIf(e -> outOfRange(e.getKey()));
    }

    public int getSoundCount(PitchGroup group) {
        return (int) sounds.values().stream().filter(group::contains).count();
    }

    public Set<BlockPos> getAllLocations(PitchGroup group) {
        Set<BlockPos> out = new HashSet<>();
        sounds.forEach((pos, pitch) -> {
            if (group.contains(pitch)) {
                out.add(pos);
            }
        });
        return out;
    }

    public static PitchGroup getGroupFromPitch(float pitch) {
        return PitchGroup.fromPitch(pitch);
    }

    public void invalidateAll() {
        sounds.clear();
    }

    protected static boolean outOfRange(BlockPos pos) {
        Vec3 camera = getCameraPos();
        return camera.distanceToSqr(Vec3.atCenterOf(pos)) > MAX_AMBIENT_SOURCE_DISTANCE * MAX_AMBIENT_SOURCE_DISTANCE;
    }

    protected static Vec3 getCameraPos() {
        // 26.3 has no GameRenderer#getMainCamera; the camera entity is the equivalent, and this is what
        // Create's own SoundScapes uses.
        var entity = Minecraft.getInstance().getCameraEntity();
        return entity == null ? Vec3.ZERO : entity.position();
    }

    public enum PitchGroup {
        VERY_LOW(0.5f, 0.75f),
        LOW(0.75f, 1.0f),
        MID(1.0f, 1.5f),
        HIGH(1.5f, 2.0f);

        private final float min;
        private final float max;

        PitchGroup(float min, float max) {
            this.min = min;
            this.max = max;
        }

        public boolean contains(float pitch) {
            return pitch >= min && pitch < max;
        }

        public static PitchGroup fromPitch(float pitch) {
            for (PitchGroup group : values()) {
                if (group.contains(pitch)) {
                    return group;
                }
            }
            return pitch < VERY_LOW.min ? VERY_LOW : HIGH;
        }

        public float getMin() {
            return min;
        }

        public float getMax() {
            return max;
        }
    }
}
