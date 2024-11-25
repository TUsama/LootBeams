package com.lootbeams.events;

import com.lootbeams.modules.rarity.ILBRarity;
import lombok.Getter;
import net.neoforged.bus.api.Event;

import java.util.List;
import java.util.function.Consumer;

@Getter
public abstract class RegisterLBRarityEvent extends Event {
    protected final List<ILBRarity> gather;

    public RegisterLBRarityEvent(List<ILBRarity> rarities) {
        this.gather = rarities;
    }

    public static class Pre extends RegisterLBRarityEvent {

        public Pre(List<ILBRarity> rarities) {
            super(rarities);
        }

        public void register(ILBRarity rarity) {
            gather.add(rarity);
        }

    }

    public static class Post extends RegisterLBRarityEvent {
        public Post(List<ILBRarity> rarities) {
            super(rarities);
        }

        //it could be safer to pass a comparator but whatever.
        //the first match rarity would be selected, so it's important for those developers who want their rarity to have higher priority. 
        public void sort(Consumer<List<ILBRarity>> consumer) {
            consumer.accept(gather);
        }
    }
}
