package me.julb.springbootstarter.web.annotations.captcha;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation requiring a valid captcha value.
 * <P>
 * @author Julb.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CaptchaValid {

}