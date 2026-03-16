package utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Custom annotation to specify Excel sheet for a test method.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface SheetName {
    String value(); // the name of the sheet
}