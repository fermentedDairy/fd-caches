package org.fermented.dairy.caches.rest;

import static org.fermented.dairy.caches.rest.Urls.APP_ROOT;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Application class.
 */
@ApplicationPath(APP_ROOT)
public class CacheDemoApplication extends Application {

}