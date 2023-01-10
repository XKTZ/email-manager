package xktz.mail.ui.backend.util;

import xktz.mail.ui.backend.error.exception.InitializationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

/**
 * An accessor able to access the file. It is thread safe
 *
 * @author XKTZ
 * @date 2022-07-08
 */
public class FileAccessor {

    /**
     * Path
     */
    private Path path;

    /**
     * Read write lock
     */
    private ReadWriteLock readWriteLock;

    /**
     * @param id   id of file
     * @param path path of the file. if it doesn't exist it will create an empty one
     */
    public FileAccessor(Path path) {
        this.path = path;
        readWriteLock = new ReentrantReadWriteLock();
        try {
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
        } catch (IOException e) {
            throw new InitializationException(e);
        }
    }

    /**
     * Update the file
     *
     * @param bytes new data
     * @throws IOException
     */
    public void update(byte[] bytes) throws IOException {
        readWriteLock.writeLock().lock();
        try {
            Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    /**
     * Read the file
     *
     * @return data in file
     * @throws IOException
     */
    public byte[] read() throws IOException {
        readWriteLock.readLock().lock();
        try {
            return Files.readAllBytes(path);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    /**
     * Delete the file
     *
     * @throws IOException
     */
    public void delete() throws IOException {
        readWriteLock.writeLock().lock();
        try {
            if (Files.exists(path)) {
                Files.delete(path);
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
}
