package io.github.jonloucks.gradle.kit;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

interface Config<T> {
    Optional<String> getName();
    
    Optional<String> getDescription();
    
    Optional<T> getFallback();
    
    List<String> getKeys();
    
    Optional<T> of(String value);
    
    interface Builder<T> extends Config<T> {
        Builder<T> name(String name);
        
        Builder<T> description(String description);
        
        Builder<T> fallback(Supplier<T> fallback);
        
        Builder<T> keys(String... keys);
    }
}
