package xktz.mail.ui.backend.web.account.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Aspect for account reading/writing
 *
 * @author XKTZ
 * @date 2022-07-02
 */
@Aspect
@Component
public class AccountReadWriteAspect {

    private ReentrantReadWriteLock readWriteLock;

    public AccountReadWriteAspect() {
        this.readWriteLock = new ReentrantReadWriteLock();
    }

    @Around("execution(@xktz.mail.ui.backend.web.account.aspect.AccountReading * *(..))")
    public Object accountReading(ProceedingJoinPoint jp) throws Throwable {
        readWriteLock.readLock().lock();
        try {
            return jp.proceed();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Around("execution(@xktz.mail.ui.backend.web.account.aspect.AccountWriting * *(..))")
    public Object accountWriting(ProceedingJoinPoint jp) throws Throwable {
        readWriteLock.writeLock().lock();
        try {
            return jp.proceed();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
}
