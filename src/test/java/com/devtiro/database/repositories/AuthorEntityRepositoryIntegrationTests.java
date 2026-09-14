package com.devtiro.database.repositories;

import com.devtiro.database.TestDataUtil;
import com.devtiro.database.domain.entities.AuthorEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * <h2>TEST DI INTEGRAZIONE REPOSITORY: {@link AuthorEntityRepositoryIntegrationTests}</h2>
 *
 * <p><b>Ruolo nell'Architettura:</b><br>
 * Questa classe è responsabile dell'esecuzione dei <b>Test d'Integrazione dello Strato di Persistenza</b>
 * per l'entità {@code AuthorEntity}. Interagisce direttamente con {@code AuthorRepository} per
 * verificare che le operazioni di lettura e scrittura sul database avvengano correttamente,
 * isolando i test dello strato dati dalle logiche dei controller REST.</p>
 *
 * <p><b>Analisi delle Annotazioni e dell'Ambiente di Test:</b></p>
 * <ul>
 *   <li><b>{@code @SpringBootTest}</b>: Carica l'intero contesto applicativo di Spring Boot in modalità
 *       di test, consentendo di instanziare i reali componenti Spring Data JPA e l'EntityManager.</li>
 *   <li><b>{@code @ExtendWith(SpringExtension.class)}</b>: Abilita il supporto del test context di Spring
 *       all'interno del ciclo di vita di JUnit 5.</li>
 *   <li><b>{@code @DirtiesContext(classMode = ...AFTER_EACH_TEST_METHOD)}</b>: Previene il fenomeno del
 *       <i>"Test Pollution"</i> (inquinamento dei dati). Svuota e ripristina il database in-memory H2
 *       allo stato iniziale dopo l'esecuzione di ogni singolo metodo di test, garantendo l'assoluta
 *       indipendenza di ciascuno scenario.</li>
 * </ul>
 *
 * <p><b>Infrastruttura Dati (H2 in Modalità PostgreSQL):</b><br>
 * I test non interrogano il database PostgreSQL fisico di produzione, ma si collegano a un database
 * in-memory <b>H2</b> configurato in <i>PostgreSQL Mode</i>. Questo permette di emulare fedelmente i
 * vincoli sintattici, le sequenze e i comportamenti del database relazionale reale all'interno di un
 * ambiente di test volatile e ultra-rapido.</p>
 *
 * <p><b>Scenari e Metodologie di Test:</b></p>
 * <ul>
 *   <li><b>Operazioni CRUD Standard:</b> Valida i metodi nativi ereditati da {@code CrudRepository}
 *       come {@code save()} (creazione e aggiornamento), {@code findById()}, {@code findAll()} e
 *       {@code deleteById()}.</li>
 *   <li><b>Query Derivate (Query Method Derivation):</b> Testa la capacità di Spring Data JPA di generare
 *       automaticamente codice SQL a partire dal nome del metodo dell'interfaccia (es. la ricerca di
 *       autori con età inferiore a una certa soglia: {@code findByAgeLessThan} o {@code ageLessThan}).</li>
 *   <li><b>Query HQL Personalizzate:</b> Verifica il corretto funzionamento di query complesse dichiarate
 *       manualmente tramite l'annotazione {@code @Query} utilizzando il linguaggio ad oggetti HQL
 *       (Hibernate Query Language).</li>
 * </ul>
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthorEntityRepositoryIntegrationTests {

    private final AuthorRepository underTest;

    /**
     * Costruttore per l'iniezione della repository sotto test.
     *
     * @param underTest la repository degli autori cablata da Spring
     */
    @Autowired
    public AuthorEntityRepositoryIntegrationTests(AuthorRepository underTest) {
        this.underTest = underTest;
    }

    @Test
    public void testThatAuthorCanBeCreatedAndRecalled() {
        AuthorEntity authorEntity = TestDataUtil.createTestAuthorEntityA();
        underTest.save(authorEntity);
        Optional<AuthorEntity> result = underTest.findById(authorEntity.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(authorEntity);
    }

    @Test
    public void testThatMultipleAuthorsCanBeCreatedAndRecalled() {
        AuthorEntity authorEntityA = TestDataUtil.createTestAuthorEntityA();
        underTest.save(authorEntityA);
        AuthorEntity authorEntityB = TestDataUtil.createTestAuthorB();
        underTest.save(authorEntityB);
        AuthorEntity authorEntityC = TestDataUtil.createTestAuthorC();
        underTest.save(authorEntityC);

        Iterable<AuthorEntity> result = underTest.findAll();
        assertThat(result)
                .hasSize(3)
                .containsExactly(authorEntityA, authorEntityB, authorEntityC);
    }

    @Test
    public void testThatAuthorCanBeUpdated() {
        AuthorEntity authorEntityA = TestDataUtil.createTestAuthorEntityA();
        underTest.save(authorEntityA);
        authorEntityA.setName("UPDATED");
        underTest.save(authorEntityA);
        Optional<AuthorEntity> result = underTest.findById(authorEntityA.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(authorEntityA);
    }

    @Test
    public void testThatAuthorCanBeDeleted() {
        AuthorEntity authorEntityA = TestDataUtil.createTestAuthorEntityA();
        underTest.save(authorEntityA);
        underTest.deleteById(authorEntityA.getId());
        Optional<AuthorEntity> result = underTest.findById(authorEntityA.getId());
        assertThat(result).isEmpty();
    }

    @Test
    public void testThatGetAuthorsWithAgeLessThan() {
        AuthorEntity testAuthorAEntity = TestDataUtil.createTestAuthorEntityA();
        underTest.save(testAuthorAEntity);
        AuthorEntity testAuthorBEntity = TestDataUtil.createTestAuthorB();
        underTest.save(testAuthorBEntity);
        AuthorEntity testAuthorCEntity = TestDataUtil.createTestAuthorC();
        underTest.save(testAuthorCEntity);

        Iterable<AuthorEntity> result = underTest.ageLessThan(50);
        assertThat(result).containsExactly(testAuthorBEntity, testAuthorCEntity);
    }

    @Test
    public void testThatGetAuthorsWithAgeGreaterThan() {
        AuthorEntity testAuthorAEntity = TestDataUtil.createTestAuthorEntityA();
        underTest.save(testAuthorAEntity);
        AuthorEntity testAuthorBEntity = TestDataUtil.createTestAuthorB();
        underTest.save(testAuthorBEntity);
        AuthorEntity testAuthorCEntity = TestDataUtil.createTestAuthorC();
        underTest.save(testAuthorCEntity);

        Iterable<AuthorEntity> result = underTest.findAuthorsWithAgeGreaterThan(50);
        assertThat(result).containsExactly(testAuthorAEntity);
    }
}
