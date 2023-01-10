package xktz.mail.ui.backend.web.draft.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import xktz.mail.ui.backend.error.exception.InitializationException;
import xktz.mail.ui.backend.error.exception.RequiredFileNotFoundException;
import xktz.mail.ui.backend.util.FileAccessor;
import xktz.mail.ui.backend.web.draft.domain.DraftMessage;
import xktz.mail.ui.backend.web.draft.domain.DraftMessageContent;
import xktz.mail.ui.backend.web.draft.domain.DraftMessageHeader;
import xktz.mail.ui.backend.web.draft.domain.DraftMessageInfo;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Service managing the drafts
 *
 * @author XKTZ
 * @date 2022-07-08
 */
@Service
public class DraftManagementService {

    /**
     * Object mapper
     */
    private static final ObjectMapper jsonMapper = new ObjectMapper();

    static {
        jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }


    private String folderPath;

    /**
     * All drafts
     */
    private Map<String, DraftMessageAccessor> drafts;

    /**
     * Lock in creating new draft
     */
    private Lock newDraftLock;

    @Autowired
    public DraftManagementService(@Qualifier("draftFolderPath") String folderPath) {
        try {
            this.folderPath = folderPath;
            drafts = Files.walk(Path.of(folderPath))
                    .filter(path -> !path.getFileName().toString().endsWith(".content"))
                    .map(path -> new AbstractMap.SimpleEntry<>(path.getFileName().toString(),
                            new DraftMessageAccessor(path)))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } catch (IOException e) {
            throw new InitializationException(e);
        }
        newDraftLock = new ReentrantLock();
    }

    /**
     * Acquiring new key
     *
     * @return new key
     * @throws IOException
     */
    public synchronized String acquireKey() throws IOException {
        return UUID.randomUUID().toString();
    }

    /**
     * Read draft by id
     *
     * @return value
     */
    public DraftMessage read(String id) throws IOException {
        var accessor = drafts.get(id);
        if (!drafts.containsKey(id)) {
            throw new RequiredFileNotFoundException(id);
        }
        accessor.readWriteLock.readLock().lock();
        try {
            return accessor.read();
        } finally {
            accessor.readWriteLock.readLock().unlock();
        }
    }

    /**
     * Update draft  by id
     *
     * @param id id
     * @throws IOException
     */
    public void write(String id, DraftMessage msg) throws IOException {
        msg.setId(id);
        if (!drafts.containsKey(id)) {
            drafts.put(id, newDraft(id, msg));
        } else {
            drafts.get(id).readWriteLock.writeLock().lock();
            try {
                drafts.get(id).update(msg);
            } finally {
                drafts.get(id).readWriteLock.writeLock().unlock();
            }
        }
    }

    /**
     * Update draft  by id
     *
     * @param id id
     * @throws IOException
     */
    public void write(String id, DraftMessageInfo msgInfo) throws IOException {
        msgInfo.getHeader().setId(id);
        if (!drafts.containsKey(id)) {
            drafts.put(id, newDraft(id, msgInfo));
        } else {
            drafts.get(id).readWriteLock.writeLock().lock();
            try {
                drafts.get(id).update(msgInfo);
            } finally {
                drafts.get(id).readWriteLock.writeLock().unlock();
            }
        }
    }

    /**
     * Remove a draft by id
     *
     * @param id id
     * @throws IOException
     */
    public void removeById(String id) throws IOException {
        var accessor = drafts.get(id);
        accessor.readWriteLock.writeLock().lock();
        try {
            if (!drafts.containsKey(id)) {
                return;
            }
            accessor.delete();
            drafts.remove(id);
        } finally {
            accessor.readWriteLock.writeLock().unlock();
        }
    }

    /**
     * List all drafts
     *
     * @throws IOException
     */
    public List<DraftMessageHeader> listHeader() throws IOException {
        return drafts.values().stream().map((accessor) -> {
            try {
                return accessor.readHeader();
            } catch (IOException e) {
                return null;
            }
        }).filter(Objects::nonNull).toList();
    }

    /**
     * List all drafts
     *
     * @return draft
     * @throws IOException
     */
    public List<DraftMessage> list() throws IOException {
        return drafts.values().stream().map(accessor -> {
            try {
                return accessor.read();
            } catch (IOException e) {
                return null;
            }
        }).filter(Objects::nonNull).toList();
    }

    private synchronized DraftMessageAccessor newDraft(String key, DraftMessage msg) throws IOException {
        var access = new DraftMessageAccessor(Path.of(folderPath + File.separator + key).toAbsolutePath());
        access.update(msg);
        return access;
    }

    private synchronized DraftMessageAccessor newDraft(String key, DraftMessageInfo msg) throws IOException {
        var access = new DraftMessageAccessor(Path.of(folderPath + File.separator + key).toAbsolutePath());
        access.update(msg);
        return access;
    }

    static class DraftMessageAccessor {
        ReadWriteLock readWriteLock;

        FileAccessor header;
        FileAccessor content;

        private DraftMessageHeader headerTemp;

        private DraftMessageContent contentTemp;

        public DraftMessageAccessor(Path headerPath) {
            var contentPath = Path.of(headerPath + ".content");
            this.header = new FileAccessor(headerPath);
            this.content = new FileAccessor(contentPath);
            readWriteLock = new ReentrantReadWriteLock();
        }

        public DraftMessageHeader readHeader() throws IOException {
            return headerTemp == null ?
                    headerTemp = jsonMapper.readValue(new String(header.read(), StandardCharsets.UTF_8), DraftMessageHeader.class)
                    : headerTemp;
        }

        public DraftMessageContent readContent() throws IOException {
            return contentTemp == null ?
                    contentTemp = jsonMapper.readValue(new String(content.read(), StandardCharsets.UTF_8), DraftMessageContent.class)
                    : contentTemp;
        }

        public DraftMessage read() throws IOException {
            var header = readHeader();
            var content = readContent();
            return new DraftMessage(header.getId(), header.getFrom(), header.getTo(), header.getSubject(), content.getRoot());
        }

        public void update(DraftMessageInfo info) throws IOException {
            headerTemp = null;
            contentTemp = null;
            header.update(jsonMapper.writeValueAsString(info.getHeader()).getBytes(StandardCharsets.UTF_8));
            content.update(jsonMapper.writeValueAsString(info.getContent()).getBytes(StandardCharsets.UTF_8));
        }

        public void update(DraftMessage msg) throws IOException {
            headerTemp = null;
            contentTemp = null;
            header.update(jsonMapper.writeValueAsString(DraftMessageHeader.of(msg)).getBytes(StandardCharsets.UTF_8));
            content.update(jsonMapper.writeValueAsString(DraftMessageContent.of(msg)).getBytes(StandardCharsets.UTF_8));
        }

        public void delete() throws IOException {
            header.delete();
            content.delete();
        }
    }
}
