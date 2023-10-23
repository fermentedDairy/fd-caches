package org.fermented.dairy.caches.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import static org.fermented.dairy.caches.rest.URLS.APP_ROOT;

@ApplicationPath(APP_ROOT)
public class CacheDemoApplication extends Application {

}