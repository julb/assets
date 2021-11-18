package me.julb.springbootstarter.googlerecaptcha.aspects;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import me.julb.library.utility.exceptions.UnauthorizedException;
import me.julb.springbootstarter.googlerecaptcha.annotations.ConditionalOnGoogleReCaptchaEnabled;
import me.julb.springbootstarter.googlerecaptcha.services.GoogleReCaptchaService;

/**
 * The Google Re-Captcha aspect.
 * <br>
 * @author Julb.
 */
@Aspect
@Component
@ConditionalOnGoogleReCaptchaEnabled
public class GoogleReCaptchaValidAspect {

    /**
     * The captcha service attribute.
     */
    @Autowired
    private GoogleReCaptchaService googleReCaptchaService;

    /**
     * Validates the captcha.
     * @param joinPoint the join point.
     * @return the joint point.
     * @throws Throwable if an error occurs.
     */
    @Around("@annotation(me.julb.springbootstarter.googlerecaptcha.annotations.GoogleReCaptchaValid)")
    public Object validateCaptcha(ProceedingJoinPoint joinPoint)
        throws Throwable {
        HttpServletRequest request = currentHttpServletRequest();
        Boolean isValidCaptcha = googleReCaptchaService.validate(request);
        if (BooleanUtils.isNotTrue(isValidCaptcha)) {
            throw new UnauthorizedException();
        }
        return joinPoint.proceed();
    }

    /**
     * Gets the current HTTP servlet request.
     * @return the HTTP servlet request.
     */
    public HttpServletRequest currentHttpServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }
}