package com.devtiro.database;

import com.devtiro.database.domain.dto.AuthorDto;
import com.devtiro.database.domain.dto.BookDto;
import com.devtiro.database.domain.entities.AuthorEntity;
import com.devtiro.database.domain.entities.BookEntity;

/**
 * <h2>CLASSE DI UTILITÀ PER I TEST: {@link TestDataUtil}</h2>
 *
 * <p><b>Ruolo nell'Architettura:</b><br>
 * Questa classe funge da <b>Object Mother / Test Data Builder</b> centralizzato per l'intera
 * suite di test dell'applicazione (sia Unitari che di Integrazione).
 * Il suo scopo esclusivo è isolare e centralizzare la creazione di dati fittizi (Mock/Stub),
 * prevenendo la duplicazione di codice boilerplate all'interno delle classi di test.</p>
 *
 * <p><b>Scenari e Pattern Applicati:</b></p>
 * <ul>
 *   <li><b>Classe Final e Costruttore Privato:</b> Segue il design pattern standard delle classi
 *       utility in Java. Essendo {@code final}, non può essere estesa. Inoltre, il costruttore
 *       privato vuoto impedisce l'istanziazione abusiva della classe tramite la parola chiave
 *       {@code new}, forzando l'uso esclusivo dei suoi metodi statici.</li>
 *   <li><b>Metodi Statici di Factory:</b> Espone metodi pubblici e statici (come
 *       {@code createTestAuthorEntityA()} o {@code createTestBookEntityA()}) che restituiscono istanze
 *       preconfigurate di entità e DTO.</li>
 *   <li><b>Flessibilità tramite Builder Pattern:</b> Sfrutta i builder generati da Lombok
 *       sulle entità e sui DTO per istanziare rapidamente oggetti con dati realistici (nomi,
 *       età, codici ISBN) pronti per essere persistiti o mappati.</li>
 * </ul>
 *
 * <p><b>Vantaggi della Centralizzazione:</b><br>
 * Se in futuro la struttura di un'entità dovesse cambiare (es. aggiungendo un nuovo campo obbligatorio
 * a {@code AuthorEntity}), non dovrai modificare manualmente decine di test sparsi nel progetto;
 * ti basterà aggiornare l'oggetto restituito all'interno di questa classe per correggere l'intera suite.</p>
 */
public final class TestDataUtil {
    private TestDataUtil(){
        // Costruttore privato per impedire l'istanziazione
    }

    /**
     * Crea un'entità autore fittizia preconfigurata (Autore A).
     *
     * @return un'istanza preconfigurata di {@link AuthorEntity}
     */
    public static AuthorEntity createTestAuthorEntityA() {
        return AuthorEntity.builder()
                .name("Abigail Rose")
                .age(80)
                .build();
    }

    /**
     * Crea un DTO autore fittizio preconfigurato (Autore A).
     *
     * @return un'istanza preconfigurata di {@link AuthorDto}
     */
    public static AuthorDto createTestAuthorDtoA() {
        return AuthorDto.builder()
                .name("Abigail Rose")
                .age(80)
                .build();
    }

    /**
     * Crea un'entità autore fittizia preconfigurata (Autore B).
     *
     * @return un'istanza preconfigurata di {@link AuthorEntity}
     */
    public static AuthorEntity createTestAuthorB() {
        return AuthorEntity.builder()
                .name("Thomas Cronin")
                .age(44)
                .build();
    }

    /**
     * Crea un DTO autore fittizio preconfigurato (Autore B).
     *
     * @return un'istanza preconfigurata di {@link AuthorDto}
     */
    public static AuthorDto createTestAuthorDtoB() {
        return AuthorDto.builder()
                .name("Thomas Cronin")
                .age(44)
                .build();
    }

    /**
     * Crea un'entità autore fittizia preconfigurata (Autore C).
     *
     * @return un'istanza preconfigurata di {@link AuthorEntity}
     */
    public static AuthorEntity createTestAuthorC() {
        return AuthorEntity.builder()
                .name("Jesse A Casey")
                .age(24)
                .build();
    }

    /**
     * Crea un'entità libro fittizia (Libro A) associata ad un'entità autore.
     *
     * @param authorEntity l'entità dell'autore da associare al libro
     * @return un'istanza preconfigurata di {@link BookEntity}
     */
    public static BookEntity createTestBookEntityA(final AuthorEntity authorEntity) {
        return BookEntity.builder()
                .isbn("978-1-2345-6789-0")
                .title("The Shadow in the Attic")
                .authorEntity(authorEntity)
                .build();
    }

    /**
     * Crea un DTO libro fittizio (Libro A) associato ad un DTO autore.
     *
     * @param authorDto il DTO dell'autore da nidificare nel libro
     * @return un'istanza preconfigurata di {@link BookDto}
     */
    public static BookDto createTestBookDtoA(final AuthorDto authorDto) {
        return BookDto.builder()
                .isbn("978-1-2345-6789-0")
                .title("The Shadow in the Attic")
                .author(authorDto)
                .build();
    }

    /**
     * Crea un'entità libro fittizia (Libro B) associata ad un'entità autore.
     *
     * @param authorEntity l'entità dell'autore da associare al libro
     * @return un'istanza preconfigurata di {@link BookEntity}
     */
    public static BookEntity createTestBookB(final AuthorEntity authorEntity) {
        return BookEntity.builder()
                .isbn("978-1-2345-6789-1")
                .title("Beyond the Horizon")
                .authorEntity(authorEntity)
                .build();
    }

    /**
     * Crea un'entità libro fittizia (Libro C) associata ad un'entità autore.
     *
     * @param authorEntity l'entità dell'autore da associare al libro
     * @return un'istanza preconfigurata di {@link BookEntity}
     */
    public static BookEntity createTestBookC(final AuthorEntity authorEntity) {
        return BookEntity.builder()
                .isbn("978-1-2345-6789-2")
                .title("The Last Ember")
                .authorEntity(authorEntity)
                .build();
    }
}