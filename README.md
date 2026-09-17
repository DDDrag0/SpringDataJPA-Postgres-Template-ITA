# SpringTutorialBasics-ITA 📚

Benvenuto! Questo progetto nasce come **risorsa didattica e guida pratica per la community italiana**, pensata specificamente per ragazzi appena usciti dall'università, sviluppatori junior o professionisti che vogliono aggiornarsi sulle tecnologie dell'ecosistema Spring Boot 3.

L'obiettivo principale è mostrare la metodologia reale di sviluppo di un'**API RESTful** enterprise-ready (nello specifico, il backend di una biblioteca), offrendo un **completo commentary didattico in italiano** all'interno di ogni singola classe, file di configurazione e script del progetto.

Il codice e la struttura del progetto sono liberamente ispirati e basati sul celebre video tutorial internazionale di Devtiro: [The ULTIMATE Guide to Spring Boot: Spring Boot for Beginners](https://www.youtube.com/watch?v=Nv2DERaMx-4).

---

## 🗺️ Indice del Progetto
1. [Perché questo progetto è utile per formarsi?](#-perché-questo-progetto-è-utile-per-formarsi)
2. [Documentazione Teorico-Pratica Allegata](#-documentazione-teorico-pratica-allegata)
3. [Suite di Test d'Integrazione Robustec](#-suite-di-test-dintegrazione-robusta-37-test)
4. [Stack Tecnologico Utilizzato](#%EF%B8%8F-stack-tecnologico-utilizzato)
5. [Come Usare Questo Repository](#-come-usare-questo-repository)

---

## 🎯 Perché questo progetto è utile per formarsi?
A differenza dei classici tutorial "copia e incolla", ogni riga di codice qui è documentata per spiegare il **"perché"** dietro a ogni scelta ingegneristica. Studiando questo repository scoprirai come affrontare i problemi reali del mondo del lavoro:

* **Clean Architecture & Disaccoppiamento:** Capire perché separiamo nettamente lo Strato di Presentazione (DTO) dallo Strato di Persistenza (Entity) tramite un'interfaccia di mappatura generica per proteggere l'integrità dei dati ed evitare che variazioni del DB rompano l'API esterna.
* **Solidità del Codice:** L'uso della *Constructor Injection* abbinata alla parola chiave `final` per garantire componenti stabili, immutabili e thread-safe secondo le best practice di Spring Boot.
* **Integrità del Database:** Come gestire le chiavi primarie naturali (ISBN) con logiche di *ISBN Override* nello strato di servizio per impedire manomissioni dei payload e gestire le relazioni nidificate complesse tramite salvataggi a cascata (`CascadeType.ALL`) con PostgreSQL.
* **Scalabilità:** L'utilizzo pratico dello standard `Pageable` e dell'oggetto `Page` di Spring Data per paginare le letture collettive, integrando parametri di query (`size`, `page`), per evitare di saturare la memoria del server.

---

## 📖 Documentazione Teorico-Pratica Allegata
Per completare l'esperienza formativa, all'interno della cartella `/DOCS` del repository vengono messe a disposizione due documentazioni testuali estese in italiano (la prima è stata già caricata, mentre la seconda è in fase di revisione e correzione), strutturate in capitoli didattici sequenziali:

1. **Guida Teorica a Spring Boot & Architettura REST:** Un manuale completo che analizza l'architettura logica di Spring (Inversion of Control, ciclo di vita dei Beans, Autoconfiguration), sviscera i principi guida delle **REST API** e ripercorre l'evoluzione dello strato di persistenza dati: dai concetti di basso livello con **JDBC e DAO**, fino alla gestione avanzata con **Spring Data JPA**, PostgreSQL e il database in-memory H2 per i test.
2. **Guida alla Costruzione della REST API (Passo-Dopo-Passo):** Una documentazione tecnica focalizzata sul *Presentation Layer* e sul *Service Layer*. Analizza nel dettaglio la conversione bidirezionale dei dati, il funzionamento dei processi di *Marshalling/Unmarshalling* operati da Jackson JSON e le regole di design per strutturare endpoint RESTful, sicuri e idempotenti.

> ⚠️ **Nota sulla Revisione:**  
> Una volta caricate entrambe le documentazioni, vi sarà sicuramente un lavoro di correzione ulteriore: essendo state scritte con l'ausilio di **NotebookLM**, non si escludono eventuali errori o imperfezioni da affinare, oltre a quelli di natura umana.

---

## 🧪 Suite di Test d'Integrazione Robusta (37 Test)
Troppo spesso i corsi universitari trascurano i test. In questo progetto troverai **37 test d'integrazione robusti** scritti con `MockMvc`, `AssertJ` e `JsonPath`:
* **Prevenzione del Test Pollution:** Gestione dell'isolamento totale tra gli scenari di test mediante l'annotazione `@DirtiesContext` (modalità `AFTER_EACH_TEST_METHOD`), combinata con l'uso di ID dinamici auto-incrementali per eliminare i fallimenti casuali dovuti a chiavi rigide bloccate nelle sequenze RAM del DB.
* **Asserzioni Flessibili aziendali:** Utilizzo mirato degli Hamcrest Matchers (`hasItem`) per analizzare le risposte JSON collettive, rendendo la suite flessibile e tollerante a fluttuazioni o accumuli di record nel database di prova.
* **Continuous Integration (CI):** Configurazione di una pipeline automatizzata nativa tramite **GitHub Actions** (`.github/workflows/main.yml`) che compila, isola le risorse in UTF-8 e testa autonomamente l'applicazione ad ogni `git push` o *Pull Request* sul ramo principale.

---

## 🛠️ Stack Tecnologico Utilizzato
* **Java 17** (OOP avanzato, Record, Lambdas e Streams API)
* **Spring Boot 3.1.0** (Spring Web, Spring Data JPA)
* **PostgreSQL** (Database relazionale di produzione gestito localmente in container via *Docker Compose*)
* **H2 Database** (Database volatile in-memory configurato in *PostgreSQL Mode* con dialetto dedicato per emulare fedelmente i vincoli dello strato dati reale nei test).
* **ModelMapper (v3.0.0)** (Configurazione con strategia *LOOSE* abilitata per la mappatura atomica di grafi e DTO nidificati profondi).
* **Lombok & Jackson** (Meta-programmazione per l'eliminazione del codice boilerplate e libreria nativa per la serializzazione/deserializzazione JSON).

---
*Questo progetto è una risorsa ad accesso libero e gratuito. Se stai studiando per un esame universitario, per superare un colloquio tecnico o per aggiornare le tue competenze aziendali, usa il codice e i commenti all'interno dei file come una vera e propria accademia passo-passo!*
