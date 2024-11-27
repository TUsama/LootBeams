package com.lootbeams.modules.rarity;

import com.lootbeams.events.LBEventBus;
import com.lootbeams.events.RegisterLBRarityEvent;
import com.lootbeams.modules.ILBModulePersistentData;
import com.lootbeams.modules.rarity.impl.LBBuiltInRarity;
import io.vavr.control.Option;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.*;

public class LBRarityContainer implements ILBModulePersistentData {
    public final static LBRarityContainer INSTANCE = new LBRarityContainer();
    private final static LinkedList<ILBRarityApplier> sources = new LinkedList<>();

    static {
        INSTANCE.initData();
    }


    public static ItemWithRarity getItemWithRarity(ItemEntity entity){
        Iterator<ILBRarityApplier> iterator = sources.iterator();
        while (iterator.hasNext()){
            ILBRarityApplier next = iterator.next();
            Option<ItemWithRarity> apply = next.apply(entity);
            if (!apply.isEmpty()) {
                System.out.printf("item is %s, rarity is %s%n", entity, apply.get());
                return apply.get();
            }
        }
        return ItemWithRarity.of(entity, LBBuiltInRarity.COMMON);
    }

    @Override
    public void initData() {
        sources.addAll(Arrays.stream(LBBuiltInRarity.values()).map(LBBuiltInRarity::getApplier).toList());
        ArrayList<ILBRarityApplier> outsideRarity = new ArrayList<>();
        LBEventBus.INSTANCE.post(new RegisterLBRarityEvent.Pre(outsideRarity));
        LBEventBus.INSTANCE.post(new RegisterLBRarityEvent.Post(outsideRarity));
        if (!outsideRarity.isEmpty()) {
            ListIterator<ILBRarityApplier> outsiders = outsideRarity.listIterator(outsideRarity.size());
            while (outsiders.hasPrevious()) {
                sources.addFirst(outsiders.previous());
            }
        }
    }

    @Override
    public void updateData() {
        throw new UnsupportedOperationException();
    }
}
