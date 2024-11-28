package com.lootbeams.modules.sound;

import com.lootbeams.Configuration;
import com.lootbeams.LootBeams;
import com.lootbeams.config.Config;
import com.lootbeams.config.ConfigurationManager;
import com.lootbeams.data.LBItemEntityCache;
import com.lootbeams.events.EntityRenderDispatcherHookEvent;
import com.lootbeams.modules.ILBModule;
import com.lootbeams.utils.Checker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.List;

public class SoundModule implements ILBModule {

    public static final SoundModule INSTANCE = new SoundModule();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEnableModule(EntityRenderDispatcherHookEvent.RenderLootBeamEvent event) {
        if (event.LBItemEntity.isSounded()) return;
        ItemEntity itemEntity = event.LBItemEntity.item();
        Item item = itemEntity.getItem().getItem();
        if ((ConfigurationManager.<Boolean>request(Config.SOUND_ALL_ITEMS) && !Checker.isItemInRegistryList(ConfigurationManager.request(Config.BLACKLIST), item))
                || (Configuration.SOUND_ONLY_EQUIPMENT.get() && Checker.isEquipmentItem(item))
                || (Configuration.SOUND_ONLY_RARE.get() && event.LBItemEntity.rarity().absoluteOrdinal() != 0)
                || Checker.isItemInRegistryList(Configuration.SOUND_ONLY_WHITELIST.get(), item)) {
            WeighedSoundEvents sound = Minecraft.getInstance().getSoundManager().getSoundEvent(LootBeams.LOOT_DROP);
            if (sound != null && Minecraft.getInstance().level != null) {
                Minecraft.getInstance().level.playSound(Minecraft.getInstance().player, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), SoundEvent.createFixedRangeEvent(LootBeams.LOOT_DROP, 8.0f), SoundSource.AMBIENT, 0.1f * Configuration.SOUND_VOLUME.get().floatValue(), 1.0f);
                LBItemEntityCache.updateSoundStatus(itemEntity.getItem());
            }
        }
    }


    @Override
    public void tryEnable() {
        if (ConfigurationManager.<Boolean>request(Config.SOUND)){
            LootBeams.EVENT_BUS.register(INSTANCE);
        }
    }
}
