package com.lootbeams.modules.rarity;

import com.lootbeams.events.LBEventBus;
import com.lootbeams.events.RegisterLBRarityEvent;
import com.lootbeams.modules.ILBModulePersistentData;
import com.lootbeams.modules.rarity.impl.LBBuiltInRarity;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.*;
import java.util.List;

public class LBRarityContainer implements ILBModulePersistentData {
    public final static LBRarityContainer INSTANCE = new LBRarityContainer();
    private final static ILBRarity DEFAULT = LBBuiltInRarity.COMMON;
    private final static LinkedList<ILBRarity> sources = new LinkedList<>();

    static {
        INSTANCE.initData();
    }


    public static ILBRarity getItemRarity(ItemEntity entity){
        Iterator<ILBRarity> iterator = sources.iterator();
        while (iterator.hasNext()){
            ILBRarity next = iterator.next();
            boolean thisRarity = next.isThisRarity(entity);
            if (thisRarity) {
                System.out.printf("item is %s, rarity is %s%n", entity, next);
                return next;
            }
        }
        return DEFAULT;
    }

    @Override
    public void initData() {
        sources.addAll(List.of(LBBuiltInRarity.values()));
        ArrayList<ILBRarity> outsideRarity = new ArrayList<>();
        LBEventBus.INSTANCE.post(new RegisterLBRarityEvent.Pre(outsideRarity));
        LBEventBus.INSTANCE.post(new RegisterLBRarityEvent.Post(outsideRarity));
        if (!outsideRarity.isEmpty()) {
            ListIterator<ILBRarity> iBeamColorSourceListIterator = outsideRarity.listIterator(outsideRarity.size());
            while (iBeamColorSourceListIterator.hasPrevious()) {
                sources.addFirst(iBeamColorSourceListIterator.previous());
            }
        }
    }

    @Override
    public void updateData() {
        throw new UnsupportedOperationException();
    }
}
