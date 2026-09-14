package com.devtiro.database.repositories;

import com.devtiro.database.domain.entities.AuthorEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * <h2>INTERFACCIA REPOSITORY: {@link AuthorRepository}</h2>
 *
 * <p><b>Ruolo nell'Architettura:</b><br>
 * Questa interfaccia costituisce il cuore dello <b>Strato di Persistenza (Persistence Layer)</b>
 * per la risorsa "Author". Estende le funzionalità di Spring Data JPA per gestire l'accesso
 * ai dati sulla tabella degli autori, eliminando completamente la necessità di scrivere query SQL
 * manuali o classi di implementazione (DAO).</p>
 *
 * <p><b>Dettagli Tecnici e Scelte di Design:</b></p>
 * <ul>
 *   <li><b>{@code @Repository}</b>: Indica a Spring che si tratta di un componente di persistenza,
 *       abilitando la traduzione automatica delle eccezioni SQL in eccezioni della gerarchia di Spring.</li>
 *   <li><b>Estensione di {@code CrudRepository<AuthorEntity, Long>}</b>: Eredita tutti i metodi standard
 *       per le operazioni CRUD (come {@code save()}, {@code findById()}, {@code deleteById()}) utilizzando
 *       un identificatore numerico di tipo {@code Long}.</li>
 *   <li><b>Generazione Automatica delle Query (Query Derivation)</b>: Sfrutta la potenza di Spring Data JPA
 *       per generare query SQL a runtime analizzando la firma dei metodi basati su convenzioni di nomenclatura
 *       (es. {@code ageLessThan(int age)}).</li>
 *   <li><b>Query Personalizzate via {@code @Query} (HQL)</b>: Consente di definire query complesse tramite
 *       il linguaggio ad oggetti HQL (Hibernate Query Language) quando la derivazione automatica dei nomi
 *       non è sufficiente (es. {@code @Query("select a from AuthorEntity a where a.age > ?1")}).</li>
 * </ul>
 *
 * <p><b>Integrazione con il Domain Model:</b><br>
 * Opera direttamente sull'entità {@code AuthorEntity}, le cui chiavi primarie vengono autogenerate a database
 * tramite una sequenza numerica dedicata.</p>
 */
@Repository
public interface AuthorRepository extends CrudRepository<AuthorEntity, Long> {

    /**
     * Recupera gli autori con un'età strettamente inferiore a quella specificata.
     * Sfrutta l'implicazione del prefisso di ricerca implicito di Spring Data.
     *
     * @param age l'età massima (esclusa) come termine di paragone
     * @return un {@link Iterable} contenente gli autori trovati
     */
    Iterable<AuthorEntity> ageLessThan(int age);

    /**
     * Recupera gli autori con un'età strettamente superiore a quella specificata.
     * Esegue una query personalizzata HQL orientata agli oggetti.
     *
     * @param age l'età minima (esclusa) come termine di paragone
     * @return un {@link Iterable} contenente gli autori trovati
     */
    @Query("SELECT a from AuthorEntity a where a.age > ?1")
    Iterable<AuthorEntity> findAuthorsWithAgeGreaterThan(int age);
}