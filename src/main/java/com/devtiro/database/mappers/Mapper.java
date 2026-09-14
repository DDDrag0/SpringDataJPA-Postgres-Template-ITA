package com.devtiro.database.mappers;

/**
 * <h2>INTERFACCIA CONTRATTO: {@link Mapper}</h2>
 *
 * <p><b>Ruolo nell'Architettura:</b><br>
 * Questa interfaccia generica definisce il contratto formale e standardizzato dello
 * <b>Strato di Mappatura (Mapping Layer)</b> dell'applicazione. Fornisce le firme dei metodi
 * indispensabili per la conversione bidirezionale dei dati, garantendo coerenza architetturale
 * e un disaccoppiamento ottimale tra i vari strati di sistema.</p>
 *
 * <p><b>Vantaggi di Design di un'Interfaccia Generica:</b></p>
 * <ul>
 *   <li><b>Standardizzazione del Codice:</b> Obbliga tutte le implementazioni concrete
 *       (es. {@code AuthorMapperImpl}, {@code BookMapperImpl}) a esporre le medesime firme
 *       e comportamenti, semplificando la manutenibilità.</li>
 *   <li><b>Flessibilità e Sostituibilità:</b> Definisce un contratto astratto che consente di
 *       sostituire o aggiornare la libreria di mappatura sottostante (es. migrando da ModelMapper
 *       a MapStruct) senza dover modificare i controller o i servizi che utilizzano i mappatori.</li>
 *   <li><b>Testabilità:</b> Agevola la scrittura di unit test e test di integrazione, permettendo
 *       di mockare facilmente i comportamenti di mappatura tramite framework come Mockito.</li>
 * </ul>
 *
 * @param <A> Rappresenta la classe legata allo strato interno dell'applicazione,
 *            ovvero l'entità di persistenza (tipicamente le classi {@code *Entity} gestite da JPA).
 * @param <B> Rappresenta la classe legata allo strato esterno di presentazione,
 *            ovvero l'oggetto di trasferimento dati (tipicamente le classi {@code *Dto} destinate a Jackson/REST).
 */
public interface Mapper<A,B> {

    /**
     * Converte un'entità di persistenza (classe sorgente) nel rispettivo DTO
     * di presentazione (classe di destinazione) per la risposta verso il client.
     *
     * @param a L'oggetto sorgente di tipo {@code A} (tipicamente un'entità).
     * @return L'oggetto mappato di tipo {@code B} (tipicamente un DTO).
     */
    B mapTo(A a);

    /**
     * Converte un DTO di presentazione (classe sorgente) nella rispettiva entità
     * di persistenza (classe di destinazione) per l'elaborazione interna o il salvataggio.
     *
     * @param b L'oggetto sorgente di tipo {@code B} (tipicamente un DTO).
     * @return L'oggetto mappato di tipo {@code A} (tipicamente un'entità).
     */
    A mapFrom(B b);
}