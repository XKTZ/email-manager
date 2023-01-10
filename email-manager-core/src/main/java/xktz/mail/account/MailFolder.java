package xktz.mail.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.mail.imap.DefaultFolder;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.util.MimeUtil;
import jakarta.mail.*;
import jakarta.mail.internet.MimeUtility;
import xktz.mail.MailException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.Cleaner;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A mail folder
 *
 * @author XKTZ
 * @date 2022-06-14
 */
public class MailFolder implements MailFile<String> {

    /**
     * Cleaner
     */
    private static final Cleaner MAIL_FOLDER_CLEANER = Cleaner.create();

    /**
     * Parent folder key
     */
    private static final String PARENT_FOLDER_KEY = "..";

    /**
     * folder
     */
    private Folder folder;

    /**
     * children
     */
    private MailFolder[] subfolder;

    /**
     * children by id
     */
    private Map<String, MailFolder> subfolderByName;

    /**
     * Messages
     */
    private List<MailMessage> messages;

    /**
     * Messages by id
     */
    private Map<Integer, MailMessage> messagesByID;

    /**
     * Parent mail folder
     */
    private MailFolder parent;

    /**
     * Cleanable
     */
    private final Cleaner.Cleanable cleanable;

    /**
     * Read write lock
     */
    private final ReadWriteLock readWriteLock;

    public MailFolder(Folder folder) throws MessagingException {
        this(folder, null);
    }

    public MailFolder(Folder folder, MailFolder parent) throws MessagingException {
        this.folder = folder;
        this.parent = parent;
        this.readWriteLock = new ReentrantReadWriteLock();
        this.cleanable = MAIL_FOLDER_CLEANER.register(this, () -> {
            if (folder.isOpen()) {
                try {
                    folder.close();
                } catch (MessagingException e) {
                    throw new MailException(e);
                }
            }
        });
        refresh();
    }

    /**
     * Check if this folder is root
     *
     * @return root folder or not
     */
    private boolean isRoot() {
        return parent == null;
    }

    /**
     * Refresh (this folder is lazy load, so all values are set nul)
     */
    private synchronized void refresh() throws MessagingException {
        this.subfolder = null;
        this.messages = null;
        if (folder.isOpen())
            folder.close(true);
    }

    /**
     * Ensure the folder is open
     */
    private void ensureFolderOpen() throws MessagingException {
        if (!folder.isOpen()) {
            folder.open(Folder.READ_WRITE);
        }
    }

    /**
     * Check if message is loaded
     *
     * @return message is loaded or not
     */
    private boolean messageLoaded() {
        return messages != null;
    }

    /**
     * Load the messages
     */
    private void loadMessage() throws MessagingException {
        readWriteLock.writeLock().lock();
        // if a folder is root: parent = null
        // then its message is an empty array
        try {
            ensureFolderOpen();
            for (var message : folder.getMessages()) {
                if (message.isExpunged()) {
                    throw new MailException(message.getSubject() + " is expunged");
                }
            }
            this.messages = Arrays.stream(folder.getMessages()).map(MailMessage::of).collect(Collectors.toList());
            this.messagesByID = this.messages.stream().collect(Collectors.toConcurrentMap(MailMessage::id, Function.identity()));
        } catch (MessagingException e) {
            this.messages = new ArrayList<>();
            this.messagesByID = new ConcurrentHashMap<>();
        } finally {
            readWriteLock.writeLock().unlock();
        }

    }

    /**
     * Ensure the message is loaded
     */
    private void ensureMessageLoaded() throws MessagingException {
        readWriteLock.writeLock().lock();
        try {
            if (!messageLoaded()) loadMessage();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    /**
     * Check if subfolder is loaded
     *
     * @return subfolder is loaded or not
     */
    private boolean subfolderLoaded() {
        return subfolder != null;
    }

    /**
     * Load subfolder
     *
     * @throws MessagingException
     */
    private void loadSubfolder() throws MessagingException {
        readWriteLock.writeLock().lock();
        try {
            this.subfolder = Arrays.stream(folder.list()).map(child -> MailFolder.of(child, this)).toArray(MailFolder[]::new);
            this.subfolderByName = Arrays.stream(this.subfolder).collect(Collectors.toMap(MailFolder::id, Function.identity()));
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    /**
     * Ensure subfolder loaded
     */
    private void ensureSubfolderLoaded() throws MessagingException {
        readWriteLock.writeLock().lock();
        try {
            if (!subfolderLoaded()) loadSubfolder();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    /**
     * Get son folder of folder
     *
     * @param name id
     * @return child
     */
    private MailFolder child(String name) throws MessagingException {
        ensureSubfolderLoaded();
        readWriteLock.readLock().lock();
        try {
            if (PARENT_FOLDER_KEY.equals(name)) {
                return parent == null ? this : parent;
            }
            if (subfolderByName.containsKey(name)) {
                return subfolderByName.get(name);
            } else {
                throw new MailFileNotFoundException(name);
            }
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    /**
     * Refresh the folder
     * @throws MessagingException
     */
    public void refreshFolder() throws MessagingException {
        readWriteLock.writeLock().lock();
        try {
            refresh();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    /**
     * Get all file
     *
     * @return file
     * @throws MessagingException
     */
    public MailFile<?>[] list() throws MessagingException {
        return Stream.concat(Arrays.stream(listFolders()), Arrays.stream(listMessages())).toArray(MailFile[]::new);
    }

    /**
     * Get folders under
     *
     * @return folders
     * @throws MessagingException
     */
    public MailFolder[] listFolders() throws MessagingException {
        ensureSubfolderLoaded();
        readWriteLock.readLock().lock();
        try {
            return this.subfolder;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    /**
     * Get messages under
     *
     * @return messages under
     * @throws MessagingException
     */
    public MailMessage[] listMessages() throws MessagingException {
        ensureMessageLoaded();
        readWriteLock.readLock().lock();
        try {
            return this.messages.toArray(MailMessage[]::new);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    /**
     * Delete self
     *
     * @throws MessagingException
     */
    private void delete() throws MessagingException {
        readWriteLock.writeLock().lock();
        try {
            if (this.folder.isOpen()) {
                this.folder.close();
            }
            this.folder.delete(true);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    /**
     * Deleting the folder
     *
     * @param paths paths
     * @throws MessagingException
     */
    public void removeFolder(String... paths) throws MessagingException {
        var on = this;
        for (var path : paths) {
            on = this.child(path);
        }
        var parent = on.parent;
        on.delete();
        if (parent != null) {
            parent.refresh();
        }
    }

    /**
     * Get a folder from path sequence
     *
     * @param paths path sequence
     * @return folder
     * @throws MessagingException
     */
    public MailFolder direct(String... paths) throws MessagingException {
        var on = this;
        for (var folderName : paths) {
            on = on.child(folderName);
        }
        return on;
    }

    /**
     * Make a new folder under the folder
     *
     * @param name folder name
     * @throws MessagingException
     */
    public void makeFolder(String name) throws MessagingException {
        try {
            readWriteLock.writeLock().lock();
            var folderCreating = folder.getFolder(name);
            if (!folderCreating.exists()) {
                folderCreating.create(Folder.HOLDS_MESSAGES);
            }
            refresh();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    /**
     * Parent of folder
     *
     * @return parent
     */
    public MailFolder parent() {
        return parent;
    }

    /**
     * Get message by id
     *
     * @param id id
     * @return message
     * @throws MessagingException
     */
    public MailMessage messageOf(Integer id) throws MessagingException {
        ensureFolderOpen();
        ensureMessageLoaded();
        readWriteLock.readLock().lock();
        try {
            if (messagesByID.containsKey(id)) {
                return messagesByID.get(id);
            } else {
                throw new MailFileNotFoundException(id);
            }
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    /**
     * Add a draft message into this folder
     * This method is deprecated because it only works when sending the message is at the same program when message is created
     *
     * @param message message
     * @throws MessagingException
     */
    @Deprecated
    public void addDraftMessageInto(Message message) throws MessagingException {
        ensureFolderOpen();
        readWriteLock.writeLock().lock();
        ensureMessageLoaded();
        Message messageNew = null;
        try {
            if (isIMAP()) {
                messageNew = ((IMAPFolder) folder).addMessages(new Message[]{message})[0];
            } else {
                throw new MailException("Folder %s doesn't support move message".formatted(displayName()));
            }
        } finally {
            if (messageNew != null) {
                Message finalMessageNew = messageNew;
                var mailMessageSending = MailMessage.draftOf(messageNew, () -> {
                    try {
                        Transport.send(message);
                        this.removeMessage(finalMessageNew.getMessageNumber());
                    } catch (MessagingException e) {
                        throw new MailException(e);
                    }
                });
                messages.add(mailMessageSending);
                messagesByID.put(mailMessageSending.id(), mailMessageSending);
            }
            readWriteLock.writeLock().unlock();
        }
    }

    /**
     * Move messages into the folder
     *
     * @throws MessagingException
     */
    public void moveMessageTo(MailFolder folderDestination, MailMessage... messages) throws MessagingException {
        ensureFolderOpen();
        ensureMessageLoaded();
        readWriteLock.writeLock().lock();
        try {
            if (isIMAP()) {
                ((IMAPFolder) folder).moveMessages(
                        Arrays.stream(messages).map(MailMessage::message).toArray(Message[]::new),
                        folderDestination.folder);
                refresh();
                folderDestination.refresh();
            } else {
                throw new MailException("Folder %s doesn't support move messages".formatted(displayName()));
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    /**
     * Remove messages
     *
     * @param id id of message
     * @throws MessagingException
     */
    public void removeMessage(int id) throws MessagingException {
        ensureMessageLoaded();
        ensureFolderOpen();
        readWriteLock.writeLock().lock();
        try {
            if (messagesByID.containsKey(id)) {
                var msg = messagesByID.get(id);
                msg.flag(Flags.Flag.DELETED, true);
                refresh();
            } else {
                throw new MailFileNotFoundException(id);
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    /**
     * Check if this folder is imap folder
     *
     * @return is imap folder or not
     */
    @JsonIgnore
    public boolean isIMAP() {
        return folder instanceof IMAPFolder;
    }

    @Override
    public String id() {
        try {
            return MimeUtility.decodeText(folder.getName());
        } catch (UnsupportedEncodingException e) {
            throw new MailException(e);
        }
    }

    @Override
    public String displayName() {
        try {
            return MimeUtility.decodeText(folder.getName());
        } catch (UnsupportedEncodingException e) {
            throw new MailException(e);
        }
    }

    /**
     * Full name of folder
     *
     * @return full name
     */
    public String fullName() {
        try {
            return MimeUtility.decodeText(folder.getFullName());
        } catch (UnsupportedEncodingException e) {
            throw new MailException(e);
        }
    }

    @Override
    public final MailFileType getType() {
        return MailFileType.FOLDER;
    }

    public static MailFolder of(Folder folder) {
        try {
            return new MailFolder(folder);
        } catch (MessagingException e) {
            throw new MailException(e);
        }
    }

    public static MailFolder of(Folder folder, MailFolder parent) {
        try {
            return new MailFolder(folder, parent);
        } catch (MessagingException e) {
            throw new MailException(e);
        }
    }
}
