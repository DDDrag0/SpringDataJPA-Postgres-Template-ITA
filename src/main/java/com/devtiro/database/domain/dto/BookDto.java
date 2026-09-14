package com.devtiro.database.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h2>DATA TRANSFER OBJECT: {@link BookDto}</h2>
 *
 * <p><b>Ruolo nell'Architettura:</b><br>
 * Questa classe rappresenta il <b>Data Transfer Object (DTO)</b> per la risorsa "Book".
 * Viene utilizzata nello <b>Strato di Presentazione (Presentation Layer)</b> per la gestione dei
 * payload delle richieste e delle risposte HTTP relative ai libri, garantendo un'interfaccia
 * pulita e indipendente per il client.</p>
 *
 * <p><b>Dettagli Tecnici e Scelte di Design:</b></p>
 * <ul>
 *   <li><b>Gestione delle Relazioni Nidificate:</b> A differenza dell'entità JPA ({@code BookEntity})
 *       che referenzia un'entità di persistenza ({@code AuthorEntity}), questo DTO referenzia un altro DTO
 *       ({@link AuthorDto}). Questa struttura previene l'esposizione accidentale dello strato di persistenza.</li>
 *   <li><b>Mappatura tramite ModelMapper:</b> Quando un client invia un {@code BookDto} con un
 *       {@code AuthorDto} nidificato, la configurazione loose di ModelMapper converte l'intero grafo di
 *       DTO nelle rispettive entità per permettere il salvataggio a cascata sul database.</li>
 *   <li><b>Integrazione con Lombok e Jackson:</b> Utilizza {@code @Data}, {@code @Builder} e i costruttori
 *       standard per facilitare la serializzazione e la deserializzazione JSON (marshalling/unmarshalling)
 *       gestite automaticamente da Jackson.</li>
 * </ul>
 *
 * <p><b>Proprietà Mappate:</b></p>
 * <ul>
 *   <li>{@code isbn} (String): Codice ISBN del libro, utilizzato come chiave primaria naturale fornita dal client.</li>
 *   <li>{@code title} (String): Titolo del libro.</li>
 *   <li>{@code author} (AuthorDto): DTO dell'autore nidificato associato al libro.</li>
 * </ul>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookDto {

    private String isbn;

    private String title;

    private AuthorDto author;

}