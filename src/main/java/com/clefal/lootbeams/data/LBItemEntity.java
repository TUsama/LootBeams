package com.clefal.lootbeams.data;

import com.clefal.lootbeams.config.Config;
import com.clefal.lootbeams.config.ConfigurationManager;
import com.clefal.lootbeams.data.rarity.LBRarity;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.world.entity.item.ItemEntity;
@Getter
@Accessors(fluent = true)
public class LBItemEntity {


    private final ItemEntity item;
    private LBRarity rarity;
    private boolean isSounded;
    private int lifeTime;

    private LBItemEntity(ItemEntity item, LBRarity rarity, boolean isSounded, int lifeTime) {
        this.item = item;
        this.rarity = rarity;
        this.isSounded = isSounded;
        this.lifeTime = lifeTime;
    }

    public static LBItemEntity of(ItemEntity item, LBRarity rarity) {
        return new LBItemEntity(item, rarity, false, 0);
    }

    public LBItemEntity to(LBRarity rarity) {
        return LBItemEntity.of(item, rarity);
    }

    public void updateLife(){
        this.lifeTime++;
    }

    public void updateSounded(){
        this.isSounded = true;
    }

    public boolean isCommon(){
        return this.rarity.absoluteOrdinal() <= 0;
    }

    public boolean isRare(){
        Integer min = ConfigurationManager.<Integer>request(Config.RARE_ORDINAL_MIN);

        return this.rarity.absoluteOrdinal() >= min;
    }
}
