package org.magnasoft.jacoco.server.api;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.magnasoft.jacoco.server.sessions.ExecFileContentsBuilder;
import org.magnasoft.jacoco.server.sessions.SessionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
class SessionDumpController {

  private final @Nonnull SessionRepository sessionRepository;

  SessionDumpController(final @Nonnull SessionRepository sessionRepository) {
    this.sessionRepository = sessionRepository;
  }

  @GetMapping(name = "/session/{sessionId}")
  ResponseEntity<?> getBinary(final @PathVariable String sessionId) {
    return Optional.ofNullable(sessionId)
        .flatMap(sessionRepository::get)
        .map(
            session -> {
              final byte[] body;
              try {
                body = new ExecFileContentsBuilder(session).build();
                return ResponseEntity.ok()
                    .header(
                        CONTENT_DISPOSITION, new ContentDispositionHeaderBuilder(sessionId).build())
                    .contentType(APPLICATION_OCTET_STREAM)
                    .body(body);
              } catch (IOException e) {
                throw new UncheckedIOException(e);
              }
            })
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
