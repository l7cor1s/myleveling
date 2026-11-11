package com.l7cor1s.myleveling;

import com.l7cor1s.myleveling.attachment.ModAttachments;
import com.l7cor1s.myleveling.compat.CompatMods;
import com.l7cor1s.myleveling.skill.Skills;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(MyLeveling.MOD_ID)
public class MyLeveling {
    public static final String MOD_ID = "myleveling";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MyLeveling(IEventBus modEventBus) {
        // Touch the skill catalog so static definitions load early.
        Skills.all();

        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("MyLeveling loaded — ranks, XP tiers, and skill tree ready");
        CompatMods.logDetected();
    }
}
