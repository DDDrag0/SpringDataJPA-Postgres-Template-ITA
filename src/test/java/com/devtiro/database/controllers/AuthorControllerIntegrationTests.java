package com.devtiro.database.controllers;

import com.devtiro.database.TestDataUtil;
import com.devtiro.database.domain.dto.AuthorDto;
import com.devtiro.database.domain.entities.AuthorEntity;
import com.devtiro.database.services.AuthorService;
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
 * <h2>TEST D'INTEGRAZIONE: {@link AuthorControllerIntegrationTests}</h2>
 *
 * <p><b>Ruolo nell'Architettura:</b><br>
 * Questa classe è responsabile dell'esecuzione dei <b>Test d'Integrazione</b> per lo strato
 * di presentazione di "Author" ({@code AuthorController}). Sfrutta il framework di test
 * di Spring per avviare un contesto applicativo reale in-memory e validare l'intero ciclo
 * di richiesta/risposta HTTP end-to-end senza caricare un server web reale.</p>
 *
 * <p><b>Analisi delle Annotazioni e dell'Infrastruttura di Test:</b></p>
 * <ul>
 *   <li><b>{@code @SpringBootTest}</b>: Avvia il contesto applicativo Spring Boot di test,
 *       caricando in memoria tutti i Bean e simulando fedelmente l'ambiente di produzione.</li>
 *   <li><b>{@code @ExtendWith(SpringExtension.class)}</b>: Integra il motore di JUnit 5 con il
 *       supporto Spring TestContext Framework per abilitare l'autocablaggio e le funzionalità di Spring.</li>
 *   <li><b>{@code @DirtiesContext(classMode = ...AFTER_EACH_TEST_METHOD)}</b>: Risolve il problema
 *       cruciale del <i>"Test Pollution"</i> (inquinamento da test). Svuota e ricrea completamente il
 *       contesto di Spring e il database in-memory (H2) dopo ogni singolo metodo di test, garantendo che lo stato
 *       creato da un test non influenzi l'esito di quelli successivi.</li>
 *   <li><b>{@code @AutoConfigureMockMvc}</b>: Configura automaticamente e inserisce nel contesto un'istanza
 *       di {@code MockMvc}, lo strumento principale per effettuare chiamate HTTP simulate.</li>
 * </ul>
 *
 * <p><b>Componenti Chiave Utilizzati:</b></p>
 * <ul>
 *   <li><b>{@code MockMvc}</b>: Simula richieste HTTP reali (POST, GET, PUT, PATCH, DELETE) dirette ai
 *       controller, permettendo di asserire codici di stato (es. 201 Created), header e strutture JSON.</li>
 *   <li><b>{@code ObjectMapper}</b>: Istanza della libreria Jackson usata per serializzare i DTO in
 *       stringhe JSON da inviare nel corpo delle richieste HTTP (Marshalling).</li>
 *   <li><b>{@code AuthorService}</b>: Iniettato per configurare preventivamente lo stato del database
 *       (es. salvando entità autore prima di testare gli endpoint di lettura o di aggiornamento).</li>
 * </ul>
 *
 * <p><b>Casi d'Uso Testati:</b></p>
 * <ul>
 *   <li>Creazione di un autore tramite {@code POST} con verifica dello stato {@code 201 Created}
 *       e asserzione sui campi della risposta tramite l'uso di <b>JsonPath</b> (es. {@code $.name}).</li>
 *   <li>Recupero collettivo e individuale degli autori con gestione degli stati {@code 200 OK} e {@code 404 Not Found}.</li>
 *   <li>Aggiornamento completo ({@code PUT}) e parziale ({@code PATCH}) di un autore esistente.</li>
 *   <li>Eliminazione di un autore con asserzione sullo stato {@code 204 No Content}.</li>
 * </ul>
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class AuthorControllerIntegrationTests {

    private final AuthorService authorService;

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    /**
     * Costruttore per l'autocablaggio delle dipendenze di test di Spring.
     *
     * @param mockMvc lo strumento per emulare le chiamate RESTful
     * @param authorService lo strato di servizio per configurare il database di test
     * @param objectMapper il componente Jackson centralizzato per la serializzazione JSON
     */
    @Autowired
    public AuthorControllerIntegrationTests(MockMvc mockMvc, AuthorService authorService, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.authorService = authorService;
        this.objectMapper = objectMapper;
    }

    @Test
    public void testThatCreateAuthorSuccessfullyReturnsHttp201Created() throws Exception {
        // 1. Genera l'entità autore di test usando la classe di utilità
        AuthorEntity testAuthorA = TestDataUtil.createTestAuthorEntityA();
        testAuthorA.setId(null); // Assicurati che l'ID sia null perché deve essere autogenerato dal DB

        // 2. Converte l'oggetto Java in una stringa JSON tramite Jackson
        String authorJson = objectMapper.writeValueAsString(testAuthorA);

        // 3. Esegue la chiamata simulata tramite MockMvc
        mockMvc.perform(
                MockMvcRequestBuilders.post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJson)
        ).andExpect(
                // Verifica che lo stato restituito sia 201
                MockMvcResultMatchers.status().isCreated()
        );
    }

    @Test
    public void testThatCreateAuthorSuccessfullyReturnsSavedAuthor() throws Exception {
        AuthorDto testAuthorA = TestDataUtil.createTestAuthorDtoA();
        testAuthorA.setId(null); // ID impostato a null per l'autogenerazione
        String authorJson = objectMapper.writeValueAsString(testAuthorA);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJson)
        ).andExpect(
                // Verifica che nel JSON restituito sia presente un campo "id" e che sia un valore numerico
                MockMvcResultMatchers.jsonPath("$.id").isNumber()
        ).andExpect(
                // Verifica che il campo "name" corrisponda al valore inserito
                MockMvcResultMatchers.jsonPath("$.name").value("Abigail Rose")
        ).andExpect(
                // Verifica che il campo "age" corrisponda al valore inserito
                MockMvcResultMatchers.jsonPath("$.age").value(80)
        );
    }

    @Test
    public void testThatListAuthorsReturnsHttpStatus200() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testThatListAuthorsReturnsListOfAuthors() throws Exception {
        // Inserisce un autore per popolare il DB isolato del test
        AuthorEntity testAuthorEntityA = TestDataUtil.createTestAuthorEntityA();
        authorService.save(testAuthorEntityA); // Salva direttamente tramite lo strato di servizio

        mockMvc.perform(
                MockMvcRequestBuilders.get("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[*].id").value(org.hamcrest.Matchers.hasItem(org.hamcrest.Matchers.isA(Number.class)))
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[*].name").value(org.hamcrest.Matchers.hasItem(testAuthorEntityA.getName()))
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[*].age").value(org.hamcrest.Matchers.hasItem(testAuthorEntityA.getAge()))
        );
    }

    @Test
    public void testThatGetAuthorReturnsHttpStatus200WhenAuthorExist() throws Exception {
        // Salva un autore di test nel database isolato
        AuthorEntity testAuthorEntityA = TestDataUtil.createTestAuthorEntityA();
        authorService.save(testAuthorEntityA);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/authors/" + testAuthorEntityA.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testThatGetAuthorReturnsAuthorWhenAuthorExist() throws Exception {
        // Salva l'autore nel DB
        AuthorEntity testAuthorEntityA = TestDataUtil.createTestAuthorEntityA();
        authorService.save(testAuthorEntityA);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/authors/" + testAuthorEntityA.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                // Trattandosi di un singolo oggetto e non di una lista, usiamo direttamente $.id, $.name e $.age
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").value(testAuthorEntityA.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value("Abigail Rose")
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.age").value(80)
        );
    }

    @Test
    public void testThatGetAuthorReturnsHttpStatus404WhenNoAuthorExists() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/authors/99")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testThatFullUpdateAuthorReturnsHttpStatus404WhenNoAuthorExists() throws Exception {
        AuthorDto testAuthorDtoA = TestDataUtil.createTestAuthorDtoA();
        String authorDtoJson = objectMapper.writeValueAsString(testAuthorDtoA); // Genera un DTO di test
        mockMvc.perform(
                MockMvcRequestBuilders.put("/authors/99") // ID 99 non esiste nel DB di test pulito
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorDtoJson)
        ).andExpect(MockMvcResultMatchers.status().isNotFound()); // Verifica il 404
    }

    @Test
    public void testThatFullUpdateAuthorReturnsHttpStatus200WhenAuthorExists() throws Exception {
        // Crea e salva un autore reale nel DB di test
        AuthorEntity testAuthorEntityA = TestDataUtil.createTestAuthorEntityA();
        AuthorEntity savedAuthor = authorService.save(testAuthorEntityA);

        // Prepara il DTO per la modifica
        AuthorDto testAuthorDtoA = TestDataUtil.createTestAuthorDtoA();
        String authorDtoJson = objectMapper.writeValueAsString(testAuthorDtoA);

        mockMvc.perform(
                MockMvcRequestBuilders.put("/authors/" + savedAuthor.getId()) // Usa l'ID salvato
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorDtoJson)
        ).andExpect(MockMvcResultMatchers.status().isOk()); // Verifica il 200
    }

    @Test
    public void testThatFullUpdateUpdatesExistingAuthor() throws Exception {
        // Crea e salva l'autore "Abigail Rose" (ID originario) nel DB di test
        AuthorEntity testAuthorEntityA = TestDataUtil.createTestAuthorEntityA();
        AuthorEntity savedAuthor = authorService.save(testAuthorEntityA);

        // Prepara il DTO di "Thomas Cronin" per sovrascrivere l'autore esistente
        AuthorDto authorDto = TestDataUtil.createTestAuthorDtoB();
        authorDto.setId(savedAuthor.getId()); // Allinea l'ID con quello salvato nel database

        String authorDtoUpdateJson = objectMapper.writeValueAsString(authorDto);

        mockMvc.perform(
                MockMvcRequestBuilders.put("/authors/" + savedAuthor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorDtoUpdateJson)
        ).andExpect(
                // Verifica che l'ID sia rimasto lo stesso
                MockMvcResultMatchers.jsonPath("$.id").value(savedAuthor.getId())
        ).andExpect(
                // Verifica che il nome sia stato aggiornato a "Thomas Cronin"
                MockMvcResultMatchers.jsonPath("$.name").value(authorDto.getName())
        ).andExpect(
                // Verifica che l'età sia stata aggiornata a 44
                MockMvcResultMatchers.jsonPath("$.age").value(authorDto.getAge())
        );
    }

    @Test
    public void testThatPartialUpdateExistingAuthorReturnsHttpStatus200Ok() throws Exception {
        // Crea e salva un autore di prova nel database di test isolato
        AuthorEntity testAuthorEntityA = TestDataUtil.createTestAuthorEntityA();
        AuthorEntity savedAuthor = authorService.save(testAuthorEntityA);

        // Prepara il DTO modificando solo il nome
        AuthorDto testAuthorDtoA = TestDataUtil.createTestAuthorDtoA();
        testAuthorDtoA.setName("UPDATED");
        String authorDtoJson = objectMapper.writeValueAsString(testAuthorDtoA);

        // Esegue la chiamata PATCH e si aspetta status 200 OK
        mockMvc.perform(
                MockMvcRequestBuilders.patch("/authors/" + savedAuthor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorDtoJson)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testThatPartialUpdateExistingAuthorReturnsUpdatedAuthor() throws Exception {
        // Crea e salva "Abigail Rose" (età 80) nel DB di test
        AuthorEntity testAuthorEntityA = TestDataUtil.createTestAuthorEntityA();
        AuthorEntity savedAuthor = authorService.save(testAuthorEntityA);

        // Prepara il DTO parziale impostando il nome su "updated"
        AuthorDto testAuthorDtoA = TestDataUtil.createTestAuthorDtoA();
        // Esegue il PATCH e convalida le modifiche tramite JsonPath
        testAuthorDtoA.setName("UPDATED");
        String authorDtoJson = objectMapper.writeValueAsString(testAuthorDtoA);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/authors/" + savedAuthor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorDtoJson)
        ).andExpect(
                // Verifica che l'ID corrisponda a quello originale salvato
                MockMvcResultMatchers.jsonPath("$.id").value(savedAuthor.getId())
        ).andExpect(
                // Verifica che il nome sia stato effettivamente aggiornato a "updated"
                MockMvcResultMatchers.jsonPath("$.name").value("UPDATED")
        ).andExpect(
                // Verifica che l'età NON sia cambiata e corrisponda ancora a 80
                MockMvcResultMatchers.jsonPath("$.age").value(testAuthorDtoA.getAge())
        );
    }

    @Test
    public void testThatDeleteNonExistingAuthorReturnsHttpStatus204NoContent() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/authors/999") // ID inesistente nel database di test isolato
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNoContent()); // Verifica dello status 204
    }

    @Test
    public void testThatDeleteAuthorReturnsHttpStatus204ForExistingAuthor() throws Exception {
        // Crea e salva un autore di prova
        AuthorEntity testAuthorEntityA = TestDataUtil.createTestAuthorEntityA();
        AuthorEntity savedAuthor = authorService.save(testAuthorEntityA);

        // Esegue l'eliminazione tramite chiamata DELETE
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/authors/" + savedAuthor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNoContent()); // Verifica dello status 204
    }
}
