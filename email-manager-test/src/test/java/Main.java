import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import xktz.mail.account.MailAccount;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author XKTZ
 * @date 2022-06-13
 */
public class Main {
    public static void main(String[] args) {
        ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        readWriteLock.writeLock().lock();
        readWriteLock.writeLock().lock();
        readWriteLock.writeLock().unlock();
        readWriteLock.writeLock().unlock();
    }
}
