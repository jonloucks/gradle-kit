package io.github.jonloucks.gradle.kit;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;

final class ConfigImpl<T> implements Config.Builder<T> {
    
    @Override
    public ConfigImpl<T> name(String name) {
        this.name = name;
        return this;
    }
    
    @Override
    public ConfigImpl<T> description(String description) {
        this.description = description;
        return this;
    }
    
    @Override
    public ConfigImpl<T> fallback(Supplier<T> fallback) {
        this.fallback = fallback;
        return this;
    }
    
    @Override
    public ConfigImpl<T> keys(String... keys) {
        if (keys.length > 0) {
            this.keys.addAll(asList(keys));
            if (null == name) {
                this.name = keys[0];
            }
        }
        return this;
    }
    
    @Override
    public Optional<String> getName() {
        return ofNullable(name);
    }
    
    @Override
    public Optional<String> getDescription() {
        return ofNullable(description);
    }
    
    @Override
    public Optional<T> getFallback() {
        if (ofNullable(fallback).isPresent()) {
            return ofNullable(fallback.get());
        }
        return Optional.empty();
    }
    
    @Override
    public List<String> getKeys() {
        return keys;
    }
    
    @Override
    public Optional<T> of(String text) {
        return ofNullable(of.apply(text));
    }
    
    ConfigImpl(Function<String,T> of) {
        this.of = of;
    }
    
    private String name;
    private String description;
    private Supplier<T> fallback;
    private final List<String> keys = new ArrayList<>() ;
    private final Function<String, T> of;
}
