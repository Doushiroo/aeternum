package com.aeternum.client.overlay;

import com.aeternum.AeternumMod;
import com.aeternum.data.PlayerData;
import com.aeternum.registry.ModAttachments;
import com.aeternum.systems.olympiad.OlympiadSystem;
import com.aeternum.systems.titles.TitleSystem;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

/**
 * AETERNUM HUD — Custom overlay replacing vanilla hearts.
 *
 * Layout (left side, bottom):
 *  ┌──────────────────────────────────┐
 *  │  [HP BAR]  1247 / 2000           │
 *  │  [EN BAR]  380 / 500             │
 *  │  [ST BAR]  100 / 100             │
 *  │                                  │
 *  │  Lv.52  [CLASS]  "Title"         │
 *  │  KARMA: Virtuous (+2340)         │
 *  │  TEMP: 37.2°C (Normal)           │
 *  └──────────────────────────────────┘
 *
 * Right side (buff/debuff list)
 * Top center (world event notification)
 * Skill bar (bottom center)
 */
@OnlyIn(Dist.CLIENT)
public class AeternumHUD {

    private static final ResourceLocation HUD_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(AeternumMod.MODID, "textures/gui/hud.png");

    // Colors
    private static final int COLOR_HP_FULL      = 0xFF22CC44;  // Green
    private static final int COLOR_HP_MID       = 0xFFDDAA00;  // Orange
    private static final int COLOR_HP_LOW       = 0xFFCC2222;  // Red
    private static final int COLOR_ENERGY        = 0xFF2255CC;  // Blue
    private static final int COLOR_STAMINA       = 0xFFDDDD22; // Yellow
    private static final int COLOR_BAR_BG        = 0xFF333333;  // Dark gray background
    private static final int COLOR_BAR_BORDER    = 0xFF888888;  // Border

    // Dimensions
    private static final int BAR_WIDTH  = 160;
    private static final int BAR_HEIGHT = 10;
    private static final int BAR_X      = 10;
    private static final int PADDING    = 2;

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;
        if (mc.player.isSpectator()) return;

        GuiGraphics graphics = event.getGuiGraphics();
        PlayerData data;

        try {
            data = mc.player.getData(ModAttachments.PLAYER_DATA.get());
        } catch (Exception e) {
            return;
        }

        int screenH = mc.getWindow().getGuiScaledHeight();
        int barY = screenH - 100;

        // ── HEALTH BAR ──────────────────────────────
        double hpPercent = data.getHealthPercent();
        int hpColor = hpPercent > 0.5 ? COLOR_HP_FULL : (hpPercent > 0.25 ? COLOR_HP_MID : COLOR_HP_LOW);

        drawBar(graphics, BAR_X, barY, BAR_WIDTH, BAR_HEIGHT, hpPercent, hpColor);

        String hpText = showAsPercent(data)
            ? String.format("HP: %.1f%%", hpPercent * 100)
            : String.format("HP: %d / %d", (int)data.getCurrentHealth(), (int)data.getMaxHealth());
        graphics.drawString(mc.font, hpText, BAR_X + BAR_WIDTH + 5, barY + 1, 0xFFFFFFFF, true);

        barY += BAR_HEIGHT + PADDING + 2;

        // ── ENERGY BAR ──────────────────────────────
        double enPercent = data.getCurrentEnergy() / data.getMaxEnergy();
        drawBar(graphics, BAR_X, barY, BAR_WIDTH, BAR_HEIGHT, enPercent, COLOR_ENERGY);

        String enText = String.format("EN: %d / %d", (int)data.getCurrentEnergy(), (int)data.getMaxEnergy());
        graphics.drawString(mc.font, enText, BAR_X + BAR_WIDTH + 5, barY + 1, 0xFFAACCFF, true);

        barY += BAR_HEIGHT + PADDING + 2;

        // ── STAMINA BAR ─────────────────────────────
        double stPercent = data.getCurrentStamina() / data.getMaxStamina();
        drawBar(graphics, BAR_X, barY, BAR_WIDTH, BAR_HEIGHT, stPercent, COLOR_STAMINA);

        String stText = String.format("ST: %d / %d", (int)data.getCurrentStamina(), (int)data.getMaxStamina());
        graphics.drawString(mc.font, stText, BAR_X + BAR_WIDTH + 5, barY + 1, 0xFFFFFFAA, true);

        barY += BAR_HEIGHT + PADDING + 8;

        // ── PLAYER INFO ──────────────────────────────
        String classDisplay = data.getPlayerClass().getDisplayName();
        String levelInfo = "§eLv." + data.getLevel() + " §7[" + classDisplay + "]";
        graphics.drawString(mc.font, levelInfo, BAR_X, barY, 0xFFFFFFFF, true);

        barY += 12;

        // ── ACTIVE TITLE ────────────────────────────
        String titleId = data.getActiveTitle();
        if (!titleId.isEmpty()) {
            var title = TitleSystem.ALL_TITLES.get(titleId);
            if (title != null) {
                graphics.drawString(mc.font, title.getDisplayName(), BAR_X, barY, 0xFFFFD700, true);
                barY += 12;
            }
        }

        // ── KARMA ───────────────────────────────────
        String karmaColor = switch (data.getKarmaLevel()) {
            case DIVINE, HOLY, VIRTUOUS, GOOD -> "§a";
            case NEUTRAL -> "§7";
            case SHADY, WICKED -> "§c";
            case CORRUPT, ABYSSAL -> "§4";
        };
        String karmaText = karmaColor + "⬡ " + data.getKarmaLevel().name() + " (" + data.getKarma() + ")";
        graphics.drawString(mc.font, karmaText, BAR_X, barY, 0xFFFFFFFF, true);

        barY += 12;

        // ── TEMPERATURE ──────────────────────────────
        String tempStatus = data.getTemperatureStatus().name().replace("_", " ");
        String tempColor = switch (data.getTemperatureStatus()) {
            case NORMAL -> "§a";
            case HOT -> "§e";
            case HEAT_EXHAUSTION, HEAT_STROKE -> "§c";
            case COLD -> "§b";
            case HYPOTHERMIA, FROSTBITE -> "§9";
        };
        String tempText = "§7Temp: " + tempColor + String.format("%.1f°C", data.getBodyTemperature()) + " §8(" + tempStatus + ")";
        graphics.drawString(mc.font, tempText, BAR_X, barY, 0xFFFFFFFF, true);

        barY += 12;

        // ── HERO STATUS ─────────────────────────────
        if (OlympiadSystem.isHero(mc.player.getUUID())) {
            var hero = OlympiadSystem.getHero(mc.player.getUUID());
            String heroLabel = hero.isGrandChampion() ? "§6§l⚔ GRAND CHAMPION ⚔" : "§e§l★ HERO ★";
            graphics.drawString(mc.font, heroLabel, BAR_X, barY, 0xFFFFD700, true);
        }

        // ── SKILL BAR (bottom center) ────────────────
        renderSkillBar(graphics, mc, data, screenH);

        // ── WORLD EVENT INDICATOR (top center) ───────
        renderEventIndicator(graphics, mc);
    }

    private static void drawBar(GuiGraphics g, int x, int y, int w, int h, double percent, int fillColor) {
        // Background
        g.fill(x - 1, y - 1, x + w + 1, y + h + 1, 0xFF111111); // outer border
        g.fill(x, y, x + w, y + h, COLOR_BAR_BG);                // background

        // Fill
        int fillW = (int)(w * Math.max(0, Math.min(1, percent)));
        if (fillW > 0) {
            g.fill(x, y, x + fillW, y + h, fillColor);
        }
    }

    private static void renderSkillBar(GuiGraphics g, Minecraft mc, PlayerData data, int screenH) {
        int skillCount = Math.min(8, data.getUnlockedSkills().size());
        if (skillCount == 0) return;

        int slotSize = 22;
        int totalW = skillCount * slotSize + (skillCount - 1) * 2;
        int startX = (mc.getWindow().getGuiScaledWidth() - totalW) / 2;
        int startY = screenH - 52;

        for (int i = 0; i < skillCount; i++) {
            int slotX = startX + i * (slotSize + 2);
            String skillId = data.getUnlockedSkills().get(i);

            // Slot background
            g.fill(slotX, startY, slotX + slotSize, startY + slotSize, 0xBB222233);
            g.fill(slotX + 1, startY + 1, slotX + slotSize - 1, startY + slotSize - 1, 0xBB334455);

            // Cooldown overlay
            long cd = data.getSkillCooldownRemaining(skillId);
            if (cd > 0) {
                // Darken
                g.fill(slotX, startY, slotX + slotSize, startY + slotSize, 0xAA000000);
                String cdText = cd > 1000 ? (cd / 1000) + "s" : "<1s";
                g.drawCenteredString(mc.font, cdText, slotX + slotSize / 2, startY + slotSize / 2 - 4, 0xFFFFFFFF);
            }

            // Skill number
            g.drawString(mc.font, String.valueOf(i + 1), slotX + 2, startY + 2, 0xFFCCCCCC, false);
        }
    }

    private static void renderEventIndicator(GuiGraphics g, Minecraft mc) {
        // Render active world event name at top center
        // In full implementation, fetch from client-side event cache synced from server
        // Stub: could render "§c☽ BLOOD MOON §7| 12:34 remaining" at top center
    }

    private static boolean showAsPercent(PlayerData data) {
        // Could be a config option. For now, always show actual values.
        return false;
    }
}
