package me.julb.springbootstarter.web.aspects.captcha;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import me.julb.library.utility.exceptions.UnauthorizedException;
import me.julb.springbootstarter.web.services.CaptchaService;

/**
 * The Google Re-Captcha aspect.
 * <P>
 * @author Julb.
 */
@Service
@Aspect
@ConditionalOnBean(CaptchaService.class)
public class CaptchaValidAspect {

    /**
     * The captcha service attribute.
     */
    @Autowired
    private CaptchaService captchaService;

    /**
     * Validates the captcha.
     * @param joinPoint the join point.
     * @return the joint point.
     * @throws Throwable if an error occurs.
     */
    @Around("@annotation(me.julb.springbootstarter.web.annotations.captcha.CaptchaValid)")
    public Object validateCaptcha(ProceedingJoinPoint joinPoint)
        throws Throwable {
        HttpServletRequest request = currentHttpServletRequest();
        Boolean isValidCaptcha = captchaService.validate(request);
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