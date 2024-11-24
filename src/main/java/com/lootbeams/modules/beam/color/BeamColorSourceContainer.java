package com.lootbeams.modules.beam.color;

import com.lootbeams.events.LBEventBus;
import com.lootbeams.events.RegisterColorSourceEvent;
import com.lootbeams.modules.ILBModulePersistentData;
import net.minecraft.world.entity.item.ItemEntity;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class BeamColorSourceContainer implements ILBModulePersistentData {
    public final static BeamColorSourceContainer INSTANCE = new BeamColorSourceContainer();

    private final static LinkedList<IBeamColorSource<?>> sources = new LinkedList<>();

    static {
        INSTANCE.initData();
    }


    public static Color getItemColor(ItemEntity entity){
        for (IBeamColorSource<?> source : sources) {

            Color color = source.getColor(entity);
            if (!IBeamColorSource.DEFAULT.equals(color)) {
                return color;
            }
        }
        return IBeamColorSource.DEFAULT;
    }

    @Override
    public void initData() {
        sources.addAll(List.of(LBBuildInColorSource.values()));
        ArrayList<IBeamColorSource<?>> iBeamColorSources = new ArrayList<>();
        LBEventBus.INSTANCE.post(new RegisterColorSourceEvent.Pre(iBeamColorSources));
        LBEventBus.INSTANCE.post(new RegisterColorSourceEvent.Post(iBeamColorSources));
        if (!iBeamColorSources.isEmpty()) {
            ListIterator<IBeamColorSource<?>> iBeamColorSourceListIterator = iBeamColorSources.listIterator(iBeamColorSources.size());
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
