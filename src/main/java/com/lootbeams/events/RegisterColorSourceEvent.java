package com.lootbeams.events;

import com.lootbeams.modules.beam.color.IBeamColorSource;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;
import java.util.function.Consumer;

public class RegisterColorSourceEvent extends Event {
    protected final List<IBeamColorSource<?>> gather;

    public RegisterColorSourceEvent(List<IBeamColorSource<?>> sources) {
        this.gather = sources;
    }

    public List<IBeamColorSource<?>> getGather() {
        return gather;
    }

    public static class Pre extends RegisterColorSourceEvent {

        public Pre(List<IBeamColorSource<?>> sources) {
            super(sources);
        }

        public void register(IBeamColorSource<?> source){
            gather.add(source);
        }

    }

    public static class Post extends RegisterColorSourceEvent {
        public Post(List<IBeamColorSource<?>> sources) {
            super(sources);
        }
        //it could be safer to pass a comparator but whatever.
        public void sortSource(Consumer<List<IBeamColorSource<?>>> consumer){
            consumer.accept(gather);
        }
    }
}
