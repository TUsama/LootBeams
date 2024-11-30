package com.lootbeams.modules.compat.apothesis;

import com.lootbeams.LootBeams;
import com.lootbeams.data.LBItemEntity;
import com.lootbeams.data.rarity.LBRarity;
import com.lootbeams.events.RegisterLBRarityEvent;
import com.lootbeams.modules.compat.ILBCompatModule;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.salvaging.SalvageItem;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.GemInstance;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.GemItem;
import io.vavr.control.Option;
import net.minecraftforge.fml.ModList;
import net.neoforged.bus.api.SubscribeEvent;

import java.awt.*;

import static io.vavr.API.*;

public class ApotheosisCompatModule implements ILBCompatModule {
    @Override
    public boolean shouldBeEnable() {
        return ModList.get().isLoaded(Apotheosis.MODID);
    }

    @Override
    public void tryEnable() {
        if (shouldBeEnable()) {
            LootBeams.EVENT_BUS.register(new ApotheosisCompatModule());
        }
    }

    @SubscribeEvent
    public void onEnable(RegisterLBRarityEvent.Pre event) {
        event.register(itemEntity -> {
            var stack = itemEntity.getItem();
            return Match(Match(stack).of(
                    //ItemStack -> LootRarity
                    Case($(AffixHelper::hasAffixes), AffixHelper::getRarity),
                    Case($(v -> v.getItem() instanceof GemItem), v -> GemInstance.unsocketed(v).rarity()),
                    Case($(v -> (v.getItem() instanceof SalvageItem)), v -> RarityRegistry.getMaterialRarity(v.getItem())),
                    Case($(), v -> RarityRegistry.INSTANCE.emptyHolder())
            )).of(
                    //LootRarity -> ILBRarity
                    Case($(v -> v.is(RarityRegistry.INSTANCE.emptyHolder().getId())), v -> Option.none()),
                    Case($(v -> !v.isBound()), v -> Option.none()),
                    Case($(), v -> Option.some(LBItemEntity.of(itemEntity, LBRarity.of(
                                    v.get().toComponent(),
                                    new Color(v.get().getColor().getValue()),
                                    v.get().ordinal()
                            )))
                    ));
        });
    }
}
