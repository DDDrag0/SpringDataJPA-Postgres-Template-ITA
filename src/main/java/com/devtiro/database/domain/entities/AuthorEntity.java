package com.devtiro.database.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h2>ENTITÀ DI PERSISTENZA: {@link AuthorEntity}</h2>
 *
 * <p><b>Ruolo nell'Architettura:</b><br>
 * Questa classe rappresenta l'entità di persistenza dello <b>Strato di Persistenza (Persistence Layer)</b>
 * per la risorsa "Author". Mappa direttamente i record della tabella {@code authors} nel database
 * relazionale e definisce la struttura logica gestita da Spring Data JPA e Hibernate.</p>
 *
 * <p><b>Dettagli Tecnici e Scelte di Design:</b></p>
 * <ul>
 *   <li><b>{@code @Entity}</b>: Identifica la classe come entità JPA gestita dal contesto di persistenza.</li>
 *   <li><b>{@code @Table(name = "authors")}</b>: Specifica la tabella di destinazione a database, denominata
 *       al plurale secondo le convenzioni standard.</li>
 *   <li><b>Strategia di Generazione dell'ID (Sequence)</b>: Utilizza l'annotazione {@code @GeneratedValue}
 *       con strategia {@code GenerationType.SEQUENCE} associata a un generatore di sequenze denominato
 *       {@code author_id_seq}. Questo delega al database l'autogenerazione degli ID numerici
 *       incrementali a partire da 1, sollevando il client dall'obbligo di fornirli.</li>
 *   <li><b>Integrazione Lombok</b>: Utilizza {@code @Data}, {@code @Builder}, {@code @NoArgsConstructor}
 *       e {@code @AllArgsConstructor} per eliminare il codice boilerplate e facilitare la creazione fluida
 *       delle istanze durante i test di integrazione.</li>
 * </ul>
 *
 * <p><b>Proprietà Mappate:</b></p>
 * <ul>
 *   <li>{@code id} (Long): Chiave primaria autogenerata tramite sequenza.</li>
 *   <li>{@code name} (String): Nome dell'autore.</li>
 *   <li>{@code age} (Integer): Età dell'autore.</li>
 * </ul>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "authors")
public class AuthorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "author_id_seq")
    private Long id;

    private String name;

    private Integer age;

}