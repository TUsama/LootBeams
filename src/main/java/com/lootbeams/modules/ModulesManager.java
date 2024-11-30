package com.lootbeams.modules;

import com.lootbeams.LootBeams;
import com.lootbeams.modules.beam.BeamModule;
import com.lootbeams.modules.compat.apothesis.ApotheosisCompatModule;
import com.lootbeams.modules.compat.mine_and_slash.MineAndSlashCompatModule;
import com.lootbeams.modules.dynamicprovider.DynamicProviderModule;
import com.lootbeams.modules.sound.SoundModule;
import com.lootbeams.modules.tooltip.TooltipsModule;

import java.util.ArrayList;
import java.util.List;

public class ModulesManager {
    private final static List<ILBModule> list = new ArrayList<>();


    public static void registerAll(){
        list.forEach(ILBModule::tryEnable);
    }

    static {
        list.add(BeamModule.INSTANCE);
        list.add(TooltipsModule.INSTANCE);
        list.add(new SoundModule());
        list.add(DynamicProviderModule.INSTANCE);


        list.add(new ApotheosisCompatModule());
        list.add(new MineAndSlashCompatModule());
    }
}
