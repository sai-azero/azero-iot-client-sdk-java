package com.azero.services.iot.client;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation class that is used to annotate properties in {@link AZEROIotDevice}.
 * Properties annotated must be accessible via corresponding getter and setter
 * methods.
 * <p>
 * With optional values provided by this annotation class, properties can also
 * be configured to disable reporting to the shadow or not to accept updates
 * from the shadow. If {@link #enableReport()} is disabled, property getter
 * function is not required. Likewise, if {@link #allowUpdate()} is disabled for
 * a property, its setter method is not required.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AZEROIotDeviceProperty {

    /**
     * An optional name can be provided to the annotated property, which will be
     * used for publishing to the shadow as well as receiving updates from the
     * shadow. If not provided, the actual property name will be used.
     *
     * @return the name of the property.
     */
    String name() default "";

    /**
     * Enable reporting the annotated property to the shadow. It's enabled by
     * default.
     *
     * @return true to enable reporting to the shadow, false otherwise.
     */
    boolean enableReport() default true;

    /**
     * Allow updates from the shadow. It's enabled by default.
     *
     * @return true to allow updates from the shadow, false otherwise.
     */
    boolean allowUpdate() default true;

}
