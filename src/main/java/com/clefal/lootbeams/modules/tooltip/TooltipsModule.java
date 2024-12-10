package com.clefal.lootbeams.modules.tooltip;

import com.clefal.lootbeams.LootBeams;
import com.clefal.lootbeams.config.Config;
import com.clefal.lootbeams.config.ConfigurationManager;
import com.clefal.lootbeams.events.EntityRenderDispatcherHookEvent;
import com.clefal.lootbeams.events.TooltipsGatherNameAndRarityEvent;
import com.clefal.lootbeams.modules.ILBModule;
import com.clefal.lootbeams.modules.tooltip.nametag.NameTagRenderer;
import io.vavr.collection.Vector;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.Map;

public class TooltipsModule implements ILBModule {

    public final static TooltipsModule INSTANCE = new TooltipsModule();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void renderNameTag(EntityRenderDispatcherHookEvent.RenderLBTooltipsEvent event) {
        if (ConfigurationManager.request(Config.ENABLE_TOOLTIPS) == TooltipsEnableStatus.TooltipsStatus.NAME_RARITY_TOOLTIPS)
            return;
        NameTagRenderer.renderNameTag(event.poseStack, event.buffers, event.LBItemEntity);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void colorizeNameAndRarity(TooltipsGatherNameAndRarityEvent event){
        for (Map.Entry<TooltipsGatherNameAndRarityEvent.Case, Component> caseComponentEntry : event.gather.entrySet()) {
            Style oldStyle = caseComponentEntry.getValue().getStyle();
            caseComponentEntry.setValue(caseComponentEntry.getValue().plainCopy().withStyle(oldStyle).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(event.lbItemEntity.rarity().color().getRGB()))));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void InternalNameAndRarityCollector(TooltipsGatherNameAndRarityEvent event){
        TooltipsEnableStatus.TooltipsStatus status = ConfigurationManager.request(Config.ENABLE_TOOLTIPS);
        status.extractComponents.accept(event);
    }


    @Override
    public void tryEnable() {
        LootBeams.EVENT_BUS.register(INSTANCE);
    }
}
