package com.lootbeams.events;

import lombok.Getter;
import net.neoforged.bus.api.Event;

import java.util.List;
import java.util.function.Consumer;

@Getter
public abstract class RegisterColorSourceEvent extends Event {
    protected final List<IBeamOverrideColorSource<?>> gather;

    public RegisterColorSourceEvent(List<IBeamOverrideColorSource<?>> sources) {
        this.gather = sources;
    }

    public static class Pre extends RegisterColorSourceEvent {

        public Pre(List<IBeamOverrideColorSource<?>> sources) {
            super(sources);
        }

        public void register(IBeamOverrideColorSource<?> source) {
            gather.add(source);
        }

    }

    public static class Post extends RegisterColorSourceEvent {
        public Post(List<IBeamOverrideColorSource<?>> sources) {
            super(sources);
        }

        //it could be safer to pass a comparator but whatever.
        public void sortSource(Consumer<List<IBeamOverrideColorSource<?>>> consumer) {
            consumer.accept(gather);
        }
    }
}
