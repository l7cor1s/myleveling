package com.l7cor1s.myleveling.sound;

import com.l7cor1s.myleveling.MyLeveling;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, MyLeveling.MOD_ID);

    private static final Map<String, DeferredHolder<SoundEvent, SoundEvent>> BY_ID = new LinkedHashMap<>();

    public static final DeferredHolder<SoundEvent, SoundEvent> AAAAY = register("aaaay");
    public static final DeferredHolder<SoundEvent, SoundEvent> VASELINE_DRISLO = register("vaseline_drislo");
    public static final DeferredHolder<SoundEvent, SoundEvent> SHUT_UP = register("shut_up");
    public static final DeferredHolder<SoundEvent, SoundEvent> FIRE_ON_KHUTOR = register("fire_on_khutor");
    public static final DeferredHolder<SoundEvent, SoundEvent> WIG_DROP = register("wig_drop");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLACKLIST_CLUB = register("blacklist_club");
    public static final DeferredHolder<SoundEvent, SoundEvent> PENETRATION = register("penetration");
    public static final DeferredHolder<SoundEvent, SoundEvent> CORBEN_SCREAM = register("corben_scream");
    public static final DeferredHolder<SoundEvent, SoundEvent> VASELINE_SCREECH = register("vaseline_screech");
    public static final DeferredHolder<SoundEvent, SoundEvent> OPEN_BEER = register("open_beer");

    private ModSounds() {
    }

    public static void register(IEventBus bus) {
        SOUNDS.register(bus);
    }

    public static Optional<SoundEvent> byId(String id) {
        DeferredHolder<SoundEvent, SoundEvent> holder = BY_ID.get(id.toLowerCase());
        return holder == null ? Optional.empty() : Optional.of(holder.get());
    }

    public static Iterable<String> ids() {
        return BY_ID.keySet();
    }

    private static DeferredHolder<SoundEvent, SoundEvent> register(String id) {
        DeferredHolder<SoundEvent, SoundEvent> holder = SOUNDS.register(id, () ->
                SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MyLeveling.MOD_ID, id)));
        BY_ID.put(id, holder);
        return holder;
    }
}
