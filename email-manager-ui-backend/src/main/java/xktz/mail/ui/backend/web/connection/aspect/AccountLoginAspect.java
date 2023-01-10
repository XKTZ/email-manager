package xktz.mail.ui.backend.web.connection.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xktz.mail.ui.backend.error.exception.AccountNotConnectedException;
import xktz.mail.ui.backend.web.connection.service.ConnectionService;

/**
 * Aspect checking login
 *
 * @author XKTZ
 * @date 2022-06-27
 */
@Aspect
@Component
public class AccountLoginAspect {

    @Autowired
    private ConnectionService connectionService;

    @Before("execution(@xktz.mail.ui.backend.web.connection.aspect.LoginCheck * *(..))")
    public void preInitialization() {
        if (!connectionService.isConnected()) {
            throw new AccountNotConnectedException();
        }
    }
}
