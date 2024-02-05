module fd.caches.aop {
    requires org.aspectj.weaver;
    requires fd.caches.shared;
    requires spring.core;
    requires spring.beans;
    requires fd.caches.api;
    requires spring.context;
    requires fd.caches.annotations;
    exports org.fermented.dairy.caches.aspects;
}