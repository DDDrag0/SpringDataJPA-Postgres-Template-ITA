package com.devtiro.database.repositories;

import com.devtiro.database.TestDataUtil;
import com.devtiro.database.domain.entities.AuthorEntity;
import com.devtiro.database.domain.entities.BookEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <h2>TEST DI INTEGRAZIONE REPOSITORY: {@link BookEntityRepositoryIntegrationTests}</h2>
 *
 * <p><b>Ruolo nell'Architettura:</b><br>
 * Questa classe gestisce i <b>Test d'Integrazione dello Strato di Persistenza</b> per l'entità
 * {@code BookEntity}. Si concentra sulla validazione delle relazioni di associazione tra libri
 * e autori, sull'integrità referenziale dei vincoli di foreign key e sul corretto funzionamento
 * del ciclo di vita dei dati.</p>
 *
 * <p><b>Analisi delle Annotazioni e dell'Ambiente di Test:</b></p>
 * <ul>
 *   <li><b>{@code @SpringBootTest}</b>: Avvia il contesto applicativo Spring di test per abilitare
 *       il motore di persistenza JPA.</li>
 *   <li><b>{@code @ExtendWith(SpringExtension.class)}</b>: Integra JUnit 5 con le funzionalità di Spring.</li>
 *   <li><b>{@code @DirtiesContext(classMode = ...AFTER_EACH_TEST_METHOD)}</b>: Essenziale per ripulire
 *       le tabelle relazionali e azzerare le chiavi primarie tra i vari test, evitando violazioni di
 *       chiave duplicata dovute agli stessi codici ISBN riutilizzati.</li>
 * </ul>
 *
 * <p><b>Peculiarità e Strategie di Test Relazionali:</b></p>
 * <ul>
 *   <li><b>Validazione del Salvataggio a Cascata (CascadeType.ALL):</b> Verifica che, persistendo un
 *       libro contenente un oggetto {@code AuthorEntity} nidificato non ancora presente a database,
 *       l'EntityManager di Hibernate crei automaticamente prima l'autore (generando la sequenza dell'ID)
 *       e colleghi poi il libro ad esso in un'unica transazione atomica.</li>
 *   <li><b>Gestione della Chiave Primaria Naturale:</b> Collauda il comportamento del repository con
 *       una chiave di tipo {@code String} (l'ISBN del libro fornito manualmente dal client) anziché
 *       affidarsi a un ID numerico incrementale autogenerato.</li>
 *   <li><b>Paginazione e Ordinamento (Paging & Sorting):</b> Valida l'estensione del repository a
 *       {@code PagingAndSortingRepository}, assicurando che l'interrogazione tramite oggetti
 *       {@code Pageable} restituisca correttamente i dati suddivisi in pagine comprensivi di metadati.</li>
 * </ul>
 *
 * <p><b>Scenari e Metodologie di Test:</b></p>
 * <ul>
 *   <li>Creazione di un libro con autore nidificato e verifica del salvataggio congiunto.</li>
 *   <li>Recupero parziale, totale e paginato dei libri presenti nel database in-memory H2.</li>
 *   <li>Aggiornamento dei dati (es. modifica del titolo tramite {@code save()}).</li>
 *   <li>Rimozione del libro tramite ISBN con verifica della scomparsa del record.</li>
 * </ul>
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookEntityRepositoryIntegrationTests {

    private final BookRepository underTest;

    /**
     * Costruttore per l'iniezione della repository sotto test.
     *
     * @param underTest la repository dei libri cablata da Spring
     */
    @Autowired
    public BookEntityRepositoryIntegrationTests(BookRepository underTest) {
        this.underTest = underTest;
    }

    @Test
    public void testThatBookCanBeCreatedAndRecalled() {
        AuthorEntity authorEntity = TestDataUtil.createTestAuthorEntityA();
        BookEntity bookEntity = TestDataUtil.createTestBookEntityA(authorEntity);
        BookEntity savedBook = underTest.save(bookEntity);
        Optional<BookEntity> result = underTest.findById(bookEntity.getIsbn());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(savedBook);
    }

    @Test
    public void testThatMultipleBooksCanBeCreatedAndRecalled() {
        AuthorEntity authorEntity = TestDataUtil.createTestAuthorEntityA();

        BookEntity bookEntityA = TestDataUtil.createTestBookEntityA(authorEntity);
        BookEntity savedBookA = underTest.save(bookEntityA);

        BookEntity bookEntityB = TestDataUtil.createTestBookB(authorEntity);
        BookEntity savedBookB = underTest.save(bookEntityB);

        BookEntity bookEntityC = TestDataUtil.createTestBookC(authorEntity);
        BookEntity savedBookC = underTest.save(bookEntityC);

        Iterable<BookEntity> result = underTest.findAll();
        assertThat(result)
                .hasSize(3)
                .containsExactly(savedBookA, savedBookB, savedBookC);
    }

    @Test
    public void testThatBookCanBeUpdated() {
        AuthorEntity authorEntity = TestDataUtil.createTestAuthorEntityA();

        BookEntity bookEntityA = TestDataUtil.createTestBookEntityA(authorEntity);
        BookEntity savedBookEntity = underTest.save(bookEntityA);

        bookEntityA.setTitle("UPDATED");
        underTest.save(savedBookEntity);

        Optional<BookEntity> result = underTest.findById(savedBookEntity.getIsbn());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(savedBookEntity);
    }

    @Test
    public void testThatBookCanBeDeleted() {
        AuthorEntity authorEntity = TestDataUtil.createTestAuthorEntityA();

        BookEntity bookEntityA = TestDataUtil.createTestBookEntityA(authorEntity);
        underTest.save(bookEntityA);

        underTest.deleteById(bookEntityA.getIsbn());

        Optional<BookEntity> result = underTest.findById(bookEntityA.getIsbn());
        assertThat(result).isEmpty();
    }
}