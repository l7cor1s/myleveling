package com.l7cor1s.myleveling.attachment;

import com.l7cor1s.myleveling.MyLeveling;
import com.l7cor1s.myleveling.progress.PlayerProgress;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

/**
 * Persistent player attachments. Survives death via {@code copyOnDeath()}.
 */
public final class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MyLeveling.MOD_ID);

    public static final Supplier<AttachmentType<PlayerProgress>> PLAYER_PROGRESS = ATTACHMENT_TYPES.register(
            "player_progress",
            () -> AttachmentType.builder(() -> PlayerProgress.EMPTY)
                    .serialize(PlayerProgress.CODEC)
                    .copyOnDeath()
                    .build()
    );

    private ModAttachments() {
    }
}
