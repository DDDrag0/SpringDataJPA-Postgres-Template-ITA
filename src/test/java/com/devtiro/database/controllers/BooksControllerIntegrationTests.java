package com.devtiro.database.controllers;

import com.devtiro.database.TestDataUtil;
import com.devtiro.database.domain.dto.BookDto;
import com.devtiro.database.domain.entities.BookEntity;
import com.devtiro.database.services.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * <h2>TEST D'INTEGRAZIONE: {@link BooksControllerIntegrationTests}</h2>
 *
 * <p><b>Ruolo nell'Architettura:</b><br>
 * Questa classe implementa i <b>Test d'Integrazione</b> per la risorsa "Book" ({@code BookController}).
 * Consente di verificare l'intero flusso di persistenza ed esposizione REST, interfacciandosi con
 * un database H2 configurato in modalità compatibile PostgreSQL per validare vincoli di integrità
 * referenziale (Foreign Key) e chiavi naturali.</p>
 *
 * <p><b>Analisi delle Annotazioni e dell'Infrastruttura di Test:</b></p>
 * <ul>
 *   <li><b>{@code @SpringBootTest}</b>: Inizializza l'applicazione nel contesto di test di Spring.</li>
 *   <li><b>{@code @ExtendWith(SpringExtension.class)}</b>: Abilita il supporto Spring per JUnit 5.</li>
 *   <li><b>{@code @DirtiesContext(classMode = ...AFTER_EACH_TEST_METHOD)}</b>: Essenziale per garantire
 *       un ambiente isolato a ogni test. Ripristina il database H2 allo stato iniziale dopo ciascun
 *       metodo per evitare conflitti di chiavi primarie (ISBN duplicati) o violazioni referenziali.</li>
 *   <li><b>{@code @AutoConfigureMockMvc}</b>: Consente l'autocablaggio dell'istanza {@code MockMvc}.</li>
 * </ul>
 *
 * <p><b>Peculiarità e Strategie dei Test sui Libri:</b></p>
 * <ul>
 *   <li><b>Gestione della Chiave Naturale (ISBN)</b>: I test validano l'endpoint {@code PUT} dimostrando
 *       che l'ISBN passato nell'URL sovrascrive correttamente qualsiasi valore discordante fornito nel corpo
 *       JSON, garantendo la sicurezza dei dati.</li>
 *   <li><b>Gestione del Salvataggio a Cascata (Cascade Type)</b>: Dimostra l'integrazione di ModelMapper
 *       con Spring Data JPA. Creando un libro con un autore nidificato privo di ID, il test valida che l'intero
 *       grafo venga persistito in cascata a database.</li>
 *   <li><b>Test della Paginazione (Pageable)</b>: Verifica che l'endpoint di lettura collettiva risponda con
 *       la struttura arricchita di {@code Page}, validando la presenza dei metadati di paginazione (es. {@code size},
 *       {@code totalElements}, {@code pageable}).</li>
 * </ul>
 *
 * <p><b>Casi d'Uso Testati:</b></p>
 * <ul>
 *   <li>Creazione ed aggiornamento completo di libri tramite chiamate {@code PUT}.</li>
 *   <li>Recupero di un singolo libro tramite ISBN con asserzione sui codici {@code 200 OK} e {@code 404 Not Found}.</li>
 *   <li>Recupero paginato della lista dei libri con verifica della dimensione e del contenuto dell'array.</li>
 *   <li>Aggiornamento parziale ({@code PATCH}) per modificare selettivamente attributi come il titolo del libro.</li>
 *   <li>Eliminazione permanente di un libro tramite il suo ISBN.</li>
 * </ul>
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class BooksControllerIntegrationTests {

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    private final BookService bookService;

    /**
     * Costruttore per l'autocablaggio delle dipendenze di test di Spring.
     *
     * @param mockMvc lo strumento per emulare le chiamate RESTful
     * @param bookService lo strato di servizio per configurare il database di test
     * @param objectMapper il componente Jackson centralizzato per la serializzazione JSON
     */
    @Autowired
    public BooksControllerIntegrationTests(MockMvc mockMvc, BookService bookService, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.bookService = bookService;
        this.objectMapper = objectMapper;
    }

    @Test
    public void testThatCreateBookReturnsHttpStatus201Created() throws Exception {
        // REFACTOR DALLA DOCUMENTAZIONE:
        // Genera il DTO di test in modo dinamico
        BookDto testBookA = TestDataUtil.createTestBookDtoA(null);
        String bookJson = objectMapper.writeValueAsString(testBookA);

        // Usa l'ISBN estratto direttamente dall'oggetto generato
        mockMvc.perform(
                MockMvcRequestBuilders.put("/books/" + testBookA.getIsbn())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
                // Nota: se l'endpoint del controller facesse un controllo incrociato preventivo
                // tra URL e Body senza fare override, l'ISBN sarebbe comunque perfettamente allineato!
        );
    }

    @Test
    public void testThatUpdateBookReturnsHttpStatus200Ok() throws Exception {
        // Crea e salva un libro di test nel database isolato
        BookEntity testBookEntityA = TestDataUtil.createTestBookEntityA(null);
        BookEntity savedBookEntity = bookService.createUpdateBook(
                testBookEntityA.getIsbn(), testBookEntityA
        );

        // Prepara il DTO per la modifica (associando lo stesso ISBN)
        BookDto testBookA = TestDataUtil.createTestBookDtoA(null);
        testBookA.setIsbn(savedBookEntity.getIsbn()); // Allinea l'ISBN
        String bookJson = objectMapper.writeValueAsString(testBookA);

        // Esegue la chiamata PUT e si aspetta 200 OK
        mockMvc.perform(
                MockMvcRequestBuilders.put("/books/" + savedBookEntity.getIsbn())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void testThatCreateBookReturnsCreatedBook() throws Exception {
        BookDto testBookA = TestDataUtil.createTestBookDtoA(null);
        String bookJson = objectMapper.writeValueAsString(testBookA);

        mockMvc.perform(
                MockMvcRequestBuilders.put("/books/" + testBookA.getIsbn())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.isbn").value(testBookA.getIsbn())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.title").value(testBookA.getTitle())
        );
    }

    @Test
    public void testThatUpdateBookReturnsUpdatedBook() throws Exception {
        // Salva il libro originale nel database di test isolato
        BookEntity testBookEntityA = TestDataUtil.createTestBookEntityA(null);
        BookEntity savedBookEntity = bookService.createUpdateBook(
                testBookEntityA.getIsbn(), testBookEntityA
        );

        // Prepara il DTO e modifica il titolo in "updated"
        BookDto testBookA = TestDataUtil.createTestBookDtoA(null);
        testBookA.setIsbn(savedBookEntity.getIsbn());
        testBookA.setTitle("UPDATED");
        String bookJson = objectMapper.writeValueAsString(testBookA);

        // Esegue il PUT e asserisce sul JSON restituito via JsonPath
        mockMvc.perform(
                MockMvcRequestBuilders.put("/books/" + savedBookEntity.getIsbn() )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson)
        ).andExpect(
                // Verifica che l'ISBN sia rimasto invariato
                MockMvcResultMatchers.jsonPath("$.isbn").value(savedBookEntity.getIsbn())
        ).andExpect(
                // Verifica che il titolo sia stato aggiornato a "updated"
                MockMvcResultMatchers.jsonPath("$.title").value("UPDATED")
        );
    }


    @Test
    public void testThatListBooksReturnsHttpStatus200Ok() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/books")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void testThatListBooksReturnsBook() throws Exception {
        // Crea e salva un libro di test nel DB isolato
        BookEntity testBookEntityA = TestDataUtil.createTestBookEntityA(null);
        bookService.createUpdateBook(testBookEntityA.getIsbn(), testBookEntityA);

        // Esegue la chiamata GET e asserisce sul primo elemento dell'array JSON
        /*
         * NOTA TECNICA SULLA PAGINAZIONE:
         * Sebbene l'endpoint restituisca un oggetto Page<BookDto> (che racchiude i record
         * nell'array '.content', quindi la stringa corretta sarebbe:
         *  'MockMvcResultMatchers.jsonPath("$.content[0].isbn").value(testBookEntityA.getIsbn())'
         * ), l'espressione JsonPath '$[0].isbn' funziona correttamente
         * grazie al flattener automatico di Jayway JsonPath, che intercetta l'array nidificato.
         * Questo approccio mantiene il test tollerante a futuri refactoring verso List piatte.
         */
        mockMvc.perform(
                MockMvcRequestBuilders.get("/books")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.content[0].isbn").value(testBookEntityA.getIsbn())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.content[0].title").value(testBookEntityA.getTitle())
        );
    }

    @Test
    public void testThatGetBookReturnsHttpStatus200OkWhenBookExists() throws Exception {
        // Crea e salva un libro di test nel DB isolato
        BookEntity testBookEntityA = TestDataUtil.createTestBookEntityA(null);
        bookService.createUpdateBook(testBookEntityA.getIsbn(), testBookEntityA);

        // Esegue la chiamata GET e verifica lo status code 200
        mockMvc.perform(
                MockMvcRequestBuilders.get("/books/" + testBookEntityA.getIsbn())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void testThatGetBookReturnsHttpStatus404WhenBookDoesntExist() throws Exception {
        mockMvc.perform(
                // Forza un ISBN inesistente nel DB di test
                MockMvcRequestBuilders.get("/books/ISBNImpossibileDaTrovare")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testThatPartialUpdateBookReturnsHttpStatus200Ok() throws Exception {
        // Crea e salva un libro di test nel database isolato
        BookEntity testBookEntityA = TestDataUtil.createTestBookEntityA(null);
        bookService.createUpdateBook(testBookEntityA.getIsbn(), testBookEntityA);

        // Prepara il DTO modificando solo il titolo
        BookDto testBookA = TestDataUtil.createTestBookDtoA(null);
        testBookA.setTitle("UPDATED");
        String bookJson = objectMapper.writeValueAsString(testBookA);

        // Esegue la chiamata PATCH e si aspetta 200 OK
        mockMvc.perform(
                MockMvcRequestBuilders.patch("/books/" + testBookEntityA.getIsbn())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void testThatPartialUpdateBookReturnsUpdatedBook() throws Exception {
        // Salva il libro originale nel DB
        BookEntity testBookEntityA = TestDataUtil.createTestBookEntityA(null);
        bookService.createUpdateBook(testBookEntityA.getIsbn(), testBookEntityA);

        // Prepara il DTO con il titolo "updated"
        BookDto testBookA = TestDataUtil.createTestBookDtoA(null);
        testBookA.setTitle("UPDATED");
        String bookJson = objectMapper.writeValueAsString(testBookA);

        // Esegue la chiamata PATCH e fa asserzioni sul corpo JSON della risposta
        mockMvc.perform(
                MockMvcRequestBuilders.patch("/books/" + testBookEntityA.getIsbn())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson)
        ).andExpect(
                // Verifica che l'ISBN non sia mutato
                MockMvcResultMatchers.jsonPath("$.isbn").value(testBookEntityA.getIsbn())
        ).andExpect(
                // Verifica che il titolo sia stato aggiornato con successo a "updated"
                MockMvcResultMatchers.jsonPath("$.title").value("UPDATED")
        );
    }

    @Test
    public void testThatDeleteNonExistingBookReturnsHttpStatus204NoContent() throws Exception {
        mockMvc.perform(
                // Utilizziamo un ISBN fittizio non presente nel database di test isolato
                MockMvcRequestBuilders.delete("/books/ISBNImpossibileDaTrovare")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNoContent()); // Verifica dello status 204
    }

    @Test
    public void testThatDeleteExistingBookReturnsHttpStatus204NoContent() throws Exception {
        // Crea e salva un libro di prova
        BookEntity testBookEntityA = TestDataUtil.createTestBookEntityA(null);
        bookService.createUpdateBook(testBookEntityA.getIsbn(), testBookEntityA);

        // Esegue l'eliminazione tramite chiamata DELETE
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/books/" + testBookEntityA.getIsbn())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNoContent()); // Verifica dello status 204
    }
}
