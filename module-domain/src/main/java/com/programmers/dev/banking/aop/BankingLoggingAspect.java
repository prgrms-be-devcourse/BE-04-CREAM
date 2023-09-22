package com.programmers.dev.banking.aop;

import com.programmers.dev.exception.BankingException;
import com.programmers.dev.exception.CreamException;
import com.programmers.dev.exception.ErrorCode;
import com.programmers.dev.user.domain.User;
import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


@Aspect
@Component
@Slf4j
public class BankingLoggingAspect {

    @Around("@annotation(com.programmers.dev.banking.aop.Deposit)")
    public Object depositLog(ProceedingJoinPoint joinPoint) throws Throwable {
        Exception exception = null;

        Object[] args = joinPoint.getArgs();
        User user = (User) args[0];
        Long money = (Long) args[1];

        for (int i = 0; i < 3; ++i) {
            try {
                Object proceed = joinPoint.proceed();
                log.info("[입금][성공] userId={}, money={}", user.getId(), money);

                return proceed;
            } catch (BankingException bankingException) {
                exception = new CreamException(ErrorCode.BANKING_SERVICE_ERROR);
            }
        }

        log.info("[입금][실패] userId={}, money={}, errorMessage={}", user, money, exception.getMessage());
        Sentry.captureException(exception);

        throw exception;
    }

    @Around("@annotation(com.programmers.dev.banking.aop.Withdraw)")
    public Object withdrawLog(ProceedingJoinPoint joinPoint) throws Throwable {
        Exception exception = null;

        Object[] args = joinPoint.getArgs();
        User user = (User) args[0];
        Long money = (Long) args[1];

        for (int i = 0; i < 3; ++i) {
            try {
                Object proceed = joinPoint.proceed();
                log.info("[인출][성공] userId={}, money={}", user.getId(), money);

                return proceed;
            } catch (BankingException bankingException) {
                exception = new CreamException(ErrorCode.BANKING_SERVICE_ERROR);
            }
        }

        log.info("[인출][실패] userId={}, money={}, errorMessage={}", user, money, exception.getMessage());
        Sentry.captureException(exception);

        throw exception;
    }
}
