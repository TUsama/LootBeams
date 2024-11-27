package com.lootbeams.modules;

import com.lootbeams.modules.beam.BeamModule;
import com.lootbeams.modules.compat.apothesis.ApotheosisCompatModule;
import com.lootbeams.modules.tooltip.TooltipsModule;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ModulesManager {
    private final static List<ILBModule> list = new ArrayList<>();


    public static void registerAll(){
        list.forEach(ILBModule::tryEnable);
    }

    static {
        list.add(BeamModule.INSTANCE);
        list.add(TooltipsModule.INSTANCE);
        list.add(new ApotheosisCompatModule());
    }
}
