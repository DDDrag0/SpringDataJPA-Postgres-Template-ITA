package com.devtiro.database.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h2>DATA TRANSFER OBJECT: {@link AuthorDto}</h2>
 *
 * <p><b>Ruolo nell'Architettura:</b><br>
 * Questa classe rappresenta il <b>Data Transfer Object (DTO)</b> per la risorsa "Author".
 * Viene utilizzata esclusivamente nello <b>Strato di Presentazione (Presentation Layer)</b>
 * per scambiare i dati tra il client e l'API, isolando completamente le entità di persistenza
 * ({@code AuthorEntity}) dal mondo esterno.</p>
 *
 * <p><b>Dettagli Tecnici e Scelte di Design:</b></p>
 * <ul>
 *   <li><b>Disaccoppiamento:</b> Impedisce l'esposizione diretta delle logiche del database (come
 *       le annotazioni JPA {@code @Entity}, {@code @Table} o le strategie di autogenerazione degli ID)
 *       all'esterno dell'applicazione, rendendo l'API più sicura e flessibile.</li>
 *   <li><b>Integrazione con Lombok:</b> Sfrutta le annotazioni di Lombok (come {@code @Data},
 *       {@code @Builder}, {@code @NoArgsConstructor} e {@code @AllArgsConstructor}) per eliminare il
 *       codice boilerplate (getter, setter, toString, costruttori).</li>
 *   <li><b>Compatibilità con Jackson:</b> La presenza del costruttore senza argomenti
 *       ({@code @NoArgsConstructor}) è indispensabile per consentire alla libreria Jackson di istanziare
 *       correttamente l'oggetto durante il processo di deserializzazione (unmarshalling) del JSON in ingresso.</li>
 * </ul>
 *
 * <p><b>Proprietà Mappate:</b></p>
 * <ul>
 *   <li>{@code id} (Long): Identificatore univoco dell'autore (autogenerato dal database).</li>
 *   <li>{@code name} (String): Nome dell'autore.</li>
 *   <li>{@code age} (Integer): Età dell'autore (definita come oggetto {@code Integer} per supportare valori nulli).</li>
 * </ul>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthorDto {

    private Long id;

    private String name;

    private Integer age;
}