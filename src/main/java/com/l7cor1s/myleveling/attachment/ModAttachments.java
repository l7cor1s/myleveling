package com.l7cor1s.myleveling.attachment;

import com.l7cor1s.myleveling.MyLeveling;
import com.mojang.serialization.Codec;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

/**
 * Persistent player attachments. Rank is derived from XP, so only XP is stored.
 */
public final class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MyLeveling.MOD_ID);

    /**
     * Total combat XP. Survives death and End returns via {@code copyOnDeath()}.
     */
    public static final Supplier<AttachmentType<Integer>> PLAYER_XP = ATTACHMENT_TYPES.register(
            "player_xp",
            () -> AttachmentType.builder(() -> 0)
                    .serialize(Codec.INT)
                    .copyOnDeath()
                    .build()
    );

    private ModAttachments() {
    }
}
