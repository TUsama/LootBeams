package com.lootbeams.modules.beam;

import com.lootbeams.config.Config;
import com.lootbeams.config.ConfigurationManager;
import com.lootbeams.modules.ILBModule;

public class BeamModule implements ILBModule {

    public static final BeamModule INSTANCE = new BeamModule();
    @Override
    public boolean isModuleEnable() {
        return ConfigurationManager.request(Config.ENABLE_BEAM);
    }
}
