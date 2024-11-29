package com.lootbeams.data.rarity;

import com.lootbeams.data.LBItemEntity;

public interface ILBRarityModifier {

    LBItemEntity modify(LBItemEntity lbItemEntity);
}
