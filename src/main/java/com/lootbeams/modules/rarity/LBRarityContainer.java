package com.lootbeams.modules.rarity;

import com.lootbeams.LootBeams;
import com.lootbeams.events.RegisterLBRarityEvent;
import com.lootbeams.modules.ILBModulePersistentData;
import io.vavr.control.Option;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Rarity;

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
        return ItemWithRarity.of(entity, LBRarity.of(Rarity.COMMON));
    }

    @Override
    public void initData() {
        //vanilla rarity transform
        sources.add(itemEntity -> Option.some(ItemWithRarity.of(itemEntity, LBRarity.of(itemEntity.getItem().getRarity()))));
        ArrayList<ILBRarityApplier> appliers = new ArrayList<>();
        LootBeams.EVENT_BUS.post(new RegisterLBRarityEvent.Pre(appliers));
        LootBeams.EVENT_BUS.post(new RegisterLBRarityEvent.Post(appliers));
        if (!appliers.isEmpty()) {
            ListIterator<ILBRarityApplier> outsiders = appliers.listIterator(appliers.size());
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
