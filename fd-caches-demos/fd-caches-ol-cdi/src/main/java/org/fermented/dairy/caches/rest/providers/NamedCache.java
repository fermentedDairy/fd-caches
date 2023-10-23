package org.fermented.dairy.caches.rest.providers;

import jakarta.enterprise.context.ApplicationScoped;
import org.fermented.dairy.caches.api.functions.Loader;
import org.fermented.dairy.caches.api.functions.OptionalLoader;
import org.fermented.dairy.caches.api.interfaces.Cache;

import java.util.Collection;
import java.util.Optional;

@ApplicationScoped
public class NamedCache implements Cache {

    @Override
    public Object load(final Object key, final Loader<Object, Object> loader, final String cacheName, final long ttlMilliSeconds, final Class keyClass, final Class valueClass) throws Exception {
        return null;
    }

    @Override
    public Object load(final Object key, final Object value, final String cacheName, final long ttlMillieSeconds, final Class keyClass, final Class valueClass) throws Exception {
        return null;
    }

    @Override
    public Optional loadOptional(final Object key, final OptionalLoader<Object, Object> loader, final String cacheName, final long ttlMilliSeconds, final Class keyClass, final Class valueClass) throws Exception {
        return Optional.empty();
    }

    @Override
    public void purge() {

    }

    @Override
    public void removeValue(final String cacheName, final Object key) {

    }

    @Override
    public void clearCache(final String cacheName) {

    }

    @Override
    public Collection<String> getCacheNames() {
        return null;
    }

    @Override
    public Collection<Object> getKeys(final String cacheNames) {
        return null;
    }

    @Override
    public String getProviderName() {
        return "namedCache";
    }

    @Override
    public Optional<Object> peek(final String cacheName, final Object key) {
        return Optional.empty();
    }
}
