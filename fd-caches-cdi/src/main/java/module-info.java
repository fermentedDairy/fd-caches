/**
 * module info for microprofile.caches.cdi module
 */
module fd.caches.cdi {
    requires jakarta.interceptor;
    requires jakarta.inject;
    requires jakarta.cdi;
    requires fd.caches.api;
    requires microprofile.config.api;
    requires org.apache.commons.lang3;
    requires fd.caches.providers;
    requires fd.caches.annotations;
    requires fd.caches.shared;

    opens org.fermented.dairy.caches.interceptors to org.apache.commons.lang3;
}