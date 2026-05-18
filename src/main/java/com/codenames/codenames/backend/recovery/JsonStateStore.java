package com.codenames.codenames.backend.recovery;

import com.codenames.codenames.backend.recovery.snapshot.SystemSnapshot;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class JsonStateStore {

  private final ObjectMapper objectMapper;
  private final Path stateFilePath;
  private final ReentrantLock ioLock = new ReentrantLock();

  public JsonStateStore(
      ObjectMapper objectMapper, @Value("${app.state-file:data/state.json}") String stateFile) {
    this.objectMapper = objectMapper;
    this.stateFilePath = Path.of(stateFile);
  }

  public void save(SystemSnapshot snapshot) {
    ioLock.lock();
    try {
      Path parentDirectory = stateFilePath.getParent();
      if (parentDirectory != null) {
        Files.createDirectories(parentDirectory);
      }

      Path tempFilePath = stateFilePath.resolveSibling(stateFilePath.getFileName() + ".tmp");
      objectMapper.writerWithDefaultPrettyPrinter().writeValue(tempFilePath.toFile(), snapshot);

      moveAtomically(tempFilePath, stateFilePath);
    } catch (IOException exception) {
      throw new IllegalStateException("Failed to persist state snapshot.", exception);
    } finally {
      ioLock.unlock();
    }
  }

  public Optional<SystemSnapshot> load() {
    ioLock.lock();
    try {
      if (!Files.exists(stateFilePath) || Files.size(stateFilePath) == 0L) {
        return Optional.empty();
      }
      return Optional.of(objectMapper.readValue(stateFilePath.toFile(), SystemSnapshot.class));
    } catch (IOException exception) {
      throw new IllegalStateException("Failed to load persisted state snapshot.", exception);
    } finally {
      ioLock.unlock();
    }
  }

  private void moveAtomically(Path source, Path target) throws IOException {
    try {
      Files.move(
          source, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    } catch (AtomicMoveNotSupportedException exception) {
      Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
    }
  }
}
