/**
 * module info for microprofile.caches.cdi module
 */
module fd.caches.cdi {
    requires jakarta.interceptor;
    requires jakarta.inject;
    requires jakarta.cdi;
    requires fd.caches.api;
    requires microprofile.config.api;
}