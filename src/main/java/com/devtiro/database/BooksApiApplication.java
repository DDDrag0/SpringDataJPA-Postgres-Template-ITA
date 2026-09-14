package com.devtiro.database;

import lombok.extern.java.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <h2>CLASSE DI INGRESSO (ENTRY POINT): {@link BooksApiApplication}</h2>
 *
 * <p><b>Ruolo nell'Architettura:</b><br>
 * Questa classe rappresenta il punto di partenza assoluto (Bootstrap) e l'ancora di avvio
 * dell'intera applicazione Spring Boot. Contiene il metodo {@code main} standard di Java
 * che avvia l'inizializzazione del container IoC e del server Tomcat incorporato.</p>
 *
 * <p><b>Analisi delle Annotazioni e Scelte di Design:</b></p>
 * <ul>
 *   <li><b>{@code @SpringBootApplication}</b>: Meta-annotazione fondamentale che funge da alias
 *       per tre annotazioni chiave del framework, automatizzando la configurazione:
 *       <ul>
 *         <li>{@code @Configuration}: Abilita la classe a registrare e definire Bean nel contesto.</li>
 *         <li>{@code @ComponentScan}: Avvia la scansione automatica e ricorsiva di tutti i sotto-package
 *             a partire da {@code com.devtiro.database} alla ricerca di componenti di business
 *             (Controller, Service, Repository).</li>
 *         <li>{@code @EnableAutoConfiguration}: Abilita il motore di auto-configurazione di Spring Boot,
 *             che configura con sani valori di default i componenti del sistema (come Tomcat o lo strato
 *             di persistenza del database) in base alle librerie incluse nel classpath.</li>
 *       </ul>
 *   </li>
 *   <li><b>{@code @Log}</b>: Annotazione di Lombok che genera automaticamente un logger privato
 *       e statico ({@code log}) di tipo {@code java.util.logging.Logger} all'interno della classe.
 *       Elimina la necessità di scrivere manualmente il codice boilerplate per l'inizializzazione del logger
 *       (es. {@code LoggerFactory.getLogger}), facilitando la tracciabilità delle operazioni di avvio
 *       e di arresto dell'applicazione.</li>
 * </ul>
 *
 * <p><b>Ciclo di Vita e Bootstrap:</b><br>
 * All'avvio dell'applicazione tramite il metodo {@code main}, la chiamata a {@code SpringApplication.run()} esegue:</p>
 * <ol>
 *   <li>Il caricamento del framework e l'inizializzazione dell'Application Context.</li>
 *   <li>Il caricamento e la configurazione automatica dei Bean e delle loro relazioni tramite
 *       la Dependency Injection.</li>
 *   <li>L'avvio del web container Tomcat incorporato, che rimane in ascolto sulla porta predefinita
 *       {@code 8080} (o su porte alternative configurate tramite proprietà esterne).</li>
 * </ol>
 */
@SpringBootApplication
@Log
public class BooksApiApplication {

    /**
     * Metodo di ingresso principale statico dell'applicazione Java.
     *
     * @param args argomenti passati da riga di comando all'avvio
     */
    public static void main(String[] args) {
        SpringApplication.run(BooksApiApplication.class, args);
    }
}