package com.lootbeams.modules.tooltip.nametag;

import com.google.common.collect.ImmutableList;
import com.lootbeams.config.Config;
import com.lootbeams.config.ConfigurationManager;
import com.lootbeams.modules.ILBModulePersistentData;
import com.lootbeams.modules.ILBModuleRenderCache;
import com.lootbeams.utils.Provider;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class NameTagCache implements ILBModuleRenderCache<NameTagCache.Data, ItemEntity> {
    private final static NameTagCache CACHE = new NameTagCache();
    private final static WeakHashMap<ItemStack, List<String>> nameTagMap = new WeakHashMap<>(500);
    private final static Object lock = new Object();
    private static boolean mark = false;

    public final static String langKeyFormat = "lootbeams.fake_rarity.";

    public static Either<Boolean, List<String>> ask(ItemEntity entity) {
        ItemStack item = entity.getItem();

        if (nameTagMap.containsKey(item)) {

            return Either.right(nameTagMap.get(item));
        }

        CACHE.handle(Data.DATA, entity, mark);

        return Either.left(false);
    }

    protected static boolean provide(ItemEntity entity, List<Component> components) {
        if (nameTagMap.containsKey(entity.getItem())) {
            return false;
        }
        synchronized (lock) {
            nameTagMap.put(entity.getItem(), components.stream().map(x -> StringUtil.stripColor(x.getString())).toList());
        }
        return true;
    }

    @Override
    public BiConsumer<Data, ItemEntity> getDataHandler() {
        return ((data, itemEntity) -> {
            Set<ResourceLocation> customRarity = data.customRarity;
            ItemStack item = itemEntity.getItem();
            Config.TooltipsStatus request = ConfigurationManager.request(Config.ENABLE_TOOLTIPS);
            Boolean showAmount = ConfigurationManager.request(Boolean.class, Config.RENDER_STACKCOUNT);
            Component name = item.getHoverName();
            if (Boolean.TRUE.equals(showAmount)) {
                int count = item.getCount();
                if (count > 1) {
                    name = name.plainCopy().append(" x" + count);
                }
            }
            //request can't be Config.TooltipsStatus.NONE here,
            //because the RenderLBTooltipsEvent will not be fire if request is Config.TooltipsStatus.NONE
            if (request == Config.TooltipsStatus.ONLY_NAME){
                provide(itemEntity, ImmutableList.of(name));

            } else {
                Component finalName = name;
                Component finalName1 = name;
                item.getTags()
                        .map(x -> Pair.of(customRarity.add(x.location()), x.location()))
                        .filter(x -> !x.getFirst())
                        .findFirst()
                        .ifPresentOrElse(x -> {
                            //provide custom rarity
                            ResourceLocation Location = x.getSecond();
                            String rarity = Location.getPath();

                            provide(itemEntity, ImmutableList.of(
                                    finalName,
                                    Component.literal(I18n.exists(langKeyFormat + rarity) ? I18n.get(langKeyFormat + rarity) : rarity)
                            ));
                        }, () -> {
                            //if this item doesn't have a custom rarity, use the built-in rarity checker instead.
                            provide(itemEntity, ImmutableList.of(finalName1,
                                    Component.literal(Provider.getRarity(item))));
                        });
            }
            mark = true;

        });
    }


    public static class Data implements ILBModulePersistentData {
        public final static Data DATA = new Data();

        static {
            DATA.initData();
        }

        protected Set<ResourceLocation> customRarity;

        @Override
        public void initData() {
            List<String> list = ConfigurationManager.request(List.class, Config.CUSTOM_RARITIES);

            customRarity = list.stream()
                    .map(x -> x.replace("#", ""))
                    .map(ResourceLocation::tryParse)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        }

        @Override
        public void updateData() {
            initData();
        }
    }
}
