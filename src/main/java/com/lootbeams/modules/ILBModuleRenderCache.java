package com.lootbeams.modules;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public interface ILBModuleRenderCache<T extends ILBModulePersistentData, E> {

    BiConsumer<T, E> getDataHandler();

    default void asyncHandle(T data, E obj, boolean alreadyRun){
        if (alreadyRun) return;
        CompletableFuture.runAsync(() -> getDataHandler().accept(data, obj), ModulesManager.thread);
    }
}
