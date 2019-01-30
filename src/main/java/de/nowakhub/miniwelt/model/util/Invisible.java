package de.nowakhub.miniwelt.model.util;

import de.nowakhub.miniwelt.controller.WorldController;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * makes a {@link de.nowakhub.miniwelt.model.Actor} instance method invisible on its contect menu by {@link WorldController#buildActorContextMenu()}
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Invisible {
}

