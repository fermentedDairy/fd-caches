/**
 * Module containing shared implementations between CDI and AOP implementations
 */
module fd.caches.shared {
    exports org.fermented.dairy.caches.handlers;
    requires org.apache.commons.lang3;
    requires fd.caches.api;
    requires fd.caches.annotations;
}