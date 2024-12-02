package com.clefal.lootbeams.data;

import com.clefal.lootbeams.LootBeams;
import com.clefal.lootbeams.data.rarity.ILBRarityApplier;
import com.clefal.lootbeams.data.rarity.LBRarity;
import com.clefal.lootbeams.events.RegisterLBRarityEvent;
import com.clefal.lootbeams.modules.ILBModulePersistentData;
import io.vavr.control.Option;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Rarity;

import java.util.*;

public class InternalLBItemEntityProvider implements ILBModulePersistentData {
    public final static InternalLBItemEntityProvider INSTANCE = new InternalLBItemEntityProvider();
    private final static LinkedList<ILBRarityApplier> sources = new LinkedList<>();

    static {
        INSTANCE.initData();
    }


    public static LBItemEntity getLBItemEntity(ItemEntity entity){
        Iterator<ILBRarityApplier> iterator = sources.iterator();
        while (iterator.hasNext()){
            ILBRarityApplier next = iterator.next();
            Option<LBItemEntity> apply = next.apply(entity);
            if (!apply.isEmpty()) {
                return apply.get();
            }
        }
        return LBItemEntity.of(entity, LBRarity.of(Rarity.COMMON));
    }

    @Override
    public void initData() {
        //vanilla rarity transform
        sources.add(itemEntity -> Option.some(LBItemEntity.of(itemEntity, LBRarity.of(itemEntity.getItem().getRarity()))));
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
