package org.magnasoft.jacoco.server.api;

import java.util.Optional;
import javax.annotation.Nonnull;
import org.magnasoft.jacoco.server.sessions.ExecFileContentsBuilder;
import org.magnasoft.jacoco.server.sessions.SessionRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

  @GetMapping("/session/{sessionId}")
  ResponseEntity<?> getBinary(final @PathVariable String sessionId) {
    return Optional.ofNullable(sessionId)
        .flatMap(sessionRepository::get)
        .map(
            session -> {
              final var body = new ExecFileContentsBuilder(session).build();
              return ResponseEntity.ok()
                  .header(
                      HttpHeaders.CONTENT_DISPOSITION,
                      new ContentDispositionHeaderBuilder(sessionId).build())
                  .contentType(MediaType.APPLICATION_OCTET_STREAM)
                  .body(body);
            })
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
