package davymbaimbai;

import davymbaimbai.records.NoteRequest;
import davymbaimbai.records.NoteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NoteTakingFrontendApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void init() {
        this.webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:8080/api/v1/notes")
                .responseTimeout(Duration.ofSeconds(30))
                .build();
    }

    @Test
    void postTwoNotesAndPrintSuccess() {
        NoteRequest note1 = new NoteRequest(null, "Coding", "Enjoy it");
        NoteRequest note2 = new NoteRequest(null, "School", "Keep learning");
        postNoteAndPrintSuccess(note1);
        postNoteAndPrintSuccess(note2);
    }

    private void postNoteAndPrintSuccess(NoteRequest noteRequest) {
        webTestClient.post()
                .uri("/save")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(noteRequest), NoteRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .consumeWith(response -> {
                    Integer noteId = response.getResponseBody();
                    System.out.println("Successfully posted note with ID: " + noteId);
                });
    }

    @Test
    void retrieveAllNotesAndPrint() {
        webTestClient.get()
                .uri("/all")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(NoteResponse.class)
                .consumeWith(response -> {
                    List<NoteResponse> notes = response.getResponseBody();
                    System.out.println("Retrieved notes:");
                    notes.forEach(note -> System.out.println(note));
                });
    }
}
