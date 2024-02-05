module fd.caches.sb.aop {
    requires static lombok;
    requires spring.web;
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires fd.caches.annotations;
    requires fd.caches.api;
    requires io.swagger.v3.oas.annotations;
    requires jakarta.validation;
    requires org.apache.commons.text;
    requires java.logging;
    requires fd.caches.aop;
    requires fd.caches.providers;
    exports org.fermented.dairy.caches.sb.aop.rest.entity.records;
    exports org.fermented.dairy.caches.sb.aop.rest.entity.rto.data;
}