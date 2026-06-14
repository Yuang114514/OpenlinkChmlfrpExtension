package cn.yuang2714.openlink_chmlfrp_extension.datatypes;

/*
 * Copyright (c) Yuang2714(鬝豭鶬鶬) 2026
 * Open source with MIT licence
 */

public record ConfigProvider(Key<Boolean> doAdvancedNodeSort, Key<Integer> proxyCreationMaxRetryCount) {
    @FunctionalInterface
    public interface Getter<T> {
        T get();
    }
    
    @FunctionalInterface
    public interface Setter<T> {
        void set(T value);
    }
    
    public record Key<T>(Getter<T> getter, Setter<T> setter) {}
    //PlatformServices.CONFIG_PROVIDER.<key>.set/get
}
