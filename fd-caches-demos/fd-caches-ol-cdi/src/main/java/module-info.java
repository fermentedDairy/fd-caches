module fd.caches.ol.cdi {
    requires jakarta.ws.rs;
    requires microprofile.openapi.api;
    requires fd.caches.api;
    requires fd.caches.providers;
    requires jakarta.validation;
    requires jakarta.cdi;
    requires fd.caches.cdi;
    requires lombok;
    requires org.apache.commons.text;
    requires java.logging;
    requires fd.caches.annotations;

    //for testing
    exports org.fermented.dairy.caches.ol.cdi.rest.entity.rto.data;
    exports org.fermented.dairy.caches.ol.cdi.rest.entity.records;
}
