package com.aeternum;

import com.aeternum.config.AeternumConfig;
import com.aeternum.events.*;
import com.aeternum.network.NetworkHandler;
import com.aeternum.registry.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AeternumMod.MODID)
public class AeternumMod {

    public static final String MODID = "aeternum";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public AeternumMod(IEventBus modEventBus) {
        LOGGER.info("=== AETERNUM - THE DEFINITIVE WORLD IS LOADING ===");

        // Register all deferred registries
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);
        ModMenus.MENU_TYPES.register(modEventBus);
        ModSounds.SOUNDS.register(modEventBus);
        ModAttributes.ATTRIBUTES.register(modEventBus);
        ModParticles.PARTICLE_TYPES.register(modEventBus);

        // Common setup
        modEventBus.addListener(this::commonSetup);

        // Register forge (game) event handlers
        NeoForge.EVENT_BUS.register(new CombatEventHandler());
        NeoForge.EVENT_BUS.register(new PlayerEventHandler());
        NeoForge.EVENT_BUS.register(new WorldEventHandler());
        NeoForge.EVENT_BUS.register(new EntityEventHandler());
        NeoForge.EVENT_BUS.register(new EconomyEventHandler());
        NeoForge.EVENT_BUS.register(new KarmaEventHandler());
        NeoForge.EVENT_BUS.register(new TemperatureEventHandler());
        NeoForge.EVENT_BUS.register(new ClanEventHandler());
        NeoForge.EVENT_BUS.register(new TerritoryEventHandler());
        NeoForge.EVENT_BUS.register(new LevelingEventHandler());
        NeoForge.EVENT_BUS.register(new TitleEventHandler());
        NeoForge.EVENT_BUS.register(new SkillEventHandler());
        NeoForge.EVENT_BUS.register(new OceanEventHandler());
        NeoForge.EVENT_BUS.register(new SkyEventHandler());
        NeoForge.EVENT_BUS.register(new BossEventHandler());
        NeoForge.EVENT_BUS.register(new ProfessionEventHandler());

        // Config
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, AeternumConfig.SERVER_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, AeternumConfig.CLIENT_SPEC);

        LOGGER.info("=== AETERNUM LOADED SUCCESSFULLY ===");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            NetworkHandler.register();
            LOGGER.info("Network channels registered.");
        });
    }
}
