package com.l7cor1s.myleveling;

import com.l7cor1s.myleveling.attachment.ModAttachments;
import com.l7cor1s.myleveling.boss.BossEvents;
import com.l7cor1s.myleveling.boss.ModEntities;
import com.l7cor1s.myleveling.compat.CompatMods;
import com.l7cor1s.myleveling.gate.ModBlocks;
import com.l7cor1s.myleveling.item.ModItems;
import com.l7cor1s.myleveling.network.ModNetworking;
import com.l7cor1s.myleveling.quest.QuestCatalog;
import com.l7cor1s.myleveling.skill.SkillCatalog;
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
        SkillCatalog.bootstrap();

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModEntities.register(modEventBus);
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);

        // Quest catalog references items; bootstrap after item deferred register exists.
        QuestCatalog.bootstrap();

        modEventBus.addListener(ModNetworking::register);
        modEventBus.addListener(BossEvents::onAttributes);
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("MyLeveling loaded — ranks, quests, bosses, ranked loot, gates");
        CompatMods.logDetected();
    }
}
