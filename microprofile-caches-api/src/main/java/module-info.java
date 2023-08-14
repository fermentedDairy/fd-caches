/**
 * Module for microprofile.caches.api
 * requires - jakarta.interceptor
 * exports - org.fermented.dairy.microprofile.caches.api.exceptions
 * exports - org.fermented.dairy.microprofile.caches.api.functions
 * exports - org.fermented.dairy.microprofile.caches.api.interfaces
 */
module microprofile.caches.api {
    requires jakarta.interceptor;
    exports org.fermented.dairy.microprofile.caches.api.exceptions;
    exports org.fermented.dairy.microprofile.caches.api.functions;
    exports org.fermented.dairy.microprofile.caches.api.interfaces;
}