package me.julb.springbootstarter.googlerecaptcha.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation requiring a valid captcha value.
 * <br>
 * @author Julb.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GoogleReCaptchaValid {

}