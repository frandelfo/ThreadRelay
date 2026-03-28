# ThreadRelay

Simulazione di una staffetta atletica basata su thread Java.
Quattro runner si passano il testimone in sequenza: il runner successivo parte quando il precedente raggiunge quota 90.

---

## Interfaccia utente

La finestra è divisa in due aree principali affiancate da una striscia informativa a destra.

### Area corsie (sinistra / centro)

Contiene quattro corsie orizzontali sovrapposte, una per ogni runner.
All'avvio della simulazione l'icona del runner appare sul margine sinistro della corsia e si sposta verso destra in modo proporzionale all'avanzamento del conteggio (da 0 a 99).

### Pannello informativo (destra)

Una striscia verticale affiancata alle corsie mostra, per ogni runner:
- il **nome** del runner (Runner 1 – Runner 4)
- il **valore corrente** del conteggio, aggiornato in tempo reale

Al termine della corsa il valore viene sostituito dalla scritta **"Fine"**.

### Barra dei controlli (in basso)

I controlli sono disposti da sinistra a destra:

| Controllo | Descrizione |
|---|---|
| **Velocità** (dropdown) | Seleziona la velocità applicata a tutti i runner: `Slow`, `Regular`, `Fast` |
| **Avvia** | Avvia la simulazione dall'inizio |
| **Sospende** | Mette in pausa tutti i runner attivi |
| **Riprende** | Riprende l'esecuzione dopo una pausa |
| **Ferma** | Interrompe la simulazione e riabilita i controlli |

---

## Logica di esecuzione

1. Premere **Avvia** per iniziare. La dropdown velocità viene disabilitata durante la corsa.
2. Il **Runner 1** parte immediatamente dalla posizione iniziale.
3. Quando il suo conteggio raggiunge **90**, il **Runner 2** entra in pista; il Runner 1 continua fino a 99.
4. Lo stesso meccanismo si ripete per Runner 3 e Runner 4.
5. La simulazione termina quando tutti e quattro i runner completano il conteggio fino a 99.
6. Al termine i pulsanti **Avvia** e la dropdown vengono riabilitati.

Durante l'esecuzione è possibile:
- premere **Sospende** per bloccare tutti i runner attivi in qualsiasi momento
- premere **Riprende** per farli ripartire dal punto esatto in cui si erano fermati
- premere **Ferma** per interrompere la simulazione anticipatamente
