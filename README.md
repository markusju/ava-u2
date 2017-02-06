# makingavagreatagain: Eine Implementierung von Übung 2
Markus Jungbluth

Dieses Dokument enthält Informationen über den Aufbau und die Funktionen von makingavagreatagain.

## Einführung
Das vorliegende Programm implementiert verschiedene Algorithmen im Bereich verteilter Anwendungen:
* Flooding-Algorithmus
* Echo-Algorithmus
* Vektor-Zeit
* Consistent Snapshot (Vektor-Zeit)

Die Algorithmen sind in eine Testumgebung mit verschiedenen Knoten eingebettet, die eine demokratische Wahl (Mehrheitswahl) simulieren.
### Die Testumgebung
In der Umgebung in der oben aufgeführte Algorithmen implementiert wurden, gibt es 3 verschiedene Typen von Knoten:

* Observer
* Voter
* Candidate

Ein *Observer* steht nicht in direkter Beziehung zu anderen Knoten und dient in der vorliegenden Implementierung als "Wahlleiter". Er terminiert den laufenden Wahlkampf, sammelt die "Wahlzettel" der Wähler und Kandidaten ein und wertet das Wahlergebnis aus.

*Voters* und *Candidates* stehen wiederrum in direkter Beziehung zueinander. Beide Knoten-Typen haben eine festgelegte Anzahl an Peers. Bei *Candidates* werden diese als *Partei-Freunde*, bei *Voters* als *Freunde* bezeichnet.
Beide Typen besitzen eine Fülle von Eigenschaften, Zuständen und Countern um oben beschriebene Algorithmen implementieren zu können:

* Pro Kandidat einen Confidence-Level (Integer, 0 bis 100)
* Datenfelder für den Gerüchte-Algorithmus aus Übung 1
* CampaignManager-Objekt (Echo-Algorithmus)
    * Zustand (WHITE, GREEN, RED)
    * Message-Counter (Integer)
    * ID des ersten Nachbarn
* FeedbackManager-Objekt (Steuerung der periodisch wiederholenden Wahlkampf-Aktionen bei Kandidaten)
    * Approval/Rejection Counter (Integer)
    * FeedbackObservers

Die Beziehungen zwischen den Konten werden zufällig festegelegt, hierbei wird ein zusammenhängender Grap unter bestimmten Auflagen erzeugt:

* Kandidaten dürfen nicht untereinander verbunden sein
* Ein Partei-Freund darf nur mit einem Kandidaten verbunden sein
* Ein Kandidat muss exakt **s** Verbindungen besitzen
* Ein Wähler muss exakt **f** Verbindungen besitzen
* Es gelte: **s** >= **f**

## Implementierung

### Getting Started
Die vorliegende Software bietet verschiedene Möglichkeiten die Algorithmen zu Testen. Um dem Nutzer eine möglichst einfache Handhabung zu ermöglichen, haben wir einen Einstiegspunkt "CliElection" eingerichtet, der einen zufällige Umgebung an Hand der übermittelten Parameter erstellt und den Wahlkampf bei beiden Kandidaten initiiert.

Dieser kann einfach über das Shell-Skript "startElection.sh" angesprochen werden:

    $ ./startElection.sh 20 6 4 true
    /Users/markus/IdeaProjects/ava-u2/out/production/ava-u2
    Starting Political System. Please stand by.
    Making AVA Great Again!
    Initiating Candidates.
    Initiating Constituents.
    Political System successfully started.
    ...

Die übermittelten Parameter setzen sich wie folgt zusammen:

1) Anzahl der Knten
2) Anzahl der Partei-Freunde
3) Anzahl der Freunde
4) Wahl-Kampagnen direkt starten? (true/false)

Nach Ausführen des Skripts (mit Paramter Nr. 4 auf true) beginnen die Kandidaten ihren Wahlkampf:

    06.02.2017 15:18:37 [2] - INFORMATION: Request 'STARTVOTEFORME' from 2  
    06.02.2017 15:18:37 [1] - INFORMATION: Request 'STARTVOTEFORME' from 1  
    06.02.2017 15:18:37 [9] - INFORMATION: Request 'VOTEFORME' from 2  
    06.02.2017 15:18:37 [10] - INFORMATION: Request 'VOTEFORME' from 2  
    06.02.2017 15:18:37 [3] - INFORMATION: Request 'VOTEFORME' from 1  
    06.02.2017 15:18:37 [8] - INFORMATION: Request 'VOTEFORME' from 1  
    06.02.2017 15:18:37 [7] - INFORMATION: Request 'VOTEFORME' from 1  
    06.02.2017 15:18:37 [17] - INFORMATION: Request 'VOTEFORME' from 9  
    06.02.2017 15:18:37 [11] - INFORMATION: Request 'VOTEFORME' from 9  
    06.02.2017 15:18:37 [6] - INFORMATION: Request 'VOTEFORME' from 8  
    06.02.2017 15:18:37 [11] - INFORMATION: Request 'VOTEFORME' from 2  
    06.02.2017 15:18:37 [17] - INFORMATION: Not relaying. Confidence for 2 too low  
    06.02.2017 15:18:37 [4] - INFORMATION: Request 'VOTEFORME' from 1  
    06.02.2017 15:18:37 [19] - INFORMATION: Request 'VOTEFORME' from 10  
    06.02.2017 15:18:37 [19] - INFORMATION: Not relaying. Confidence for 2 too low  
    06.02.2017 15:18:37 [14] - INFORMATION: Request 'VOTEFORME' from 2  
    06.02.2017 15:18:37 [5] - INFORMATION: Request 'VOTEFORME' from 1  
    06.02.2017 15:18:37 [12] - INFORMATION: Request 'VOTEFORME' from 2  
    06.02.2017 15:18:37 [19] - INFORMATION: Request 'VOTEFORME' from 7
    ...

Die Software gibt den Fortschritt des Wahlkampfes detailliert durch Log-Meldungen aus. Neben einem eindeutigen Zeitstempel findet sich hier auch die Angabe der ID des Knotens in eckigen Klammern, der die Meldung ausgelöst hat. 

Die Kandidaten haben 2 Möglichkeiten die Meinung -also den confidence level- und damit das Wahlverhalten der Wähler zu beeinflussen:

* **Wähl-Mich**:
    Bei einer Wähl-Mich Aktion versucht der Kandidat seine Wähler per Mund-zu-Mund-Propaganda zu überzeugen. Er leitet seine Aktion über seine Parteifreunde zu den Wählern, die die Aktion jedoch nur an weitere Freunde verteilen, wenn sie von dem Kandidaten überzeugt sind.
    
    Beeinflussung der Meinung:
    ```
    if confidence[candidate] > confidence[other_candidate] :
        confidence[candidate] = confidence[candidate] + confidence[candidate]/10
    ```
 
* **Kampagne**:
    Bei einer Kampagne macht der Kandidat Werbung für sich und versucht die Wähler von sich zu überzeugen. Anders als bei der Mund-zu-Mund Propaganda (Wähl-Mich) können sich die Wähler aber nicht gegen die Verbreitung wehren. Dieses Verfahren stellt damit in etwa ein national ausgestrahltes TV-Duell dar.
    
    Beeinflussung der Meinung:
    ```
    if confidence[candidate] > confidence[other_candidate]:
        confidence[candidate] += 1
        confidence[other_candidate] -= 1
        
    if confidence[other_candidate] > confidence[candidate]:
        confidence[candidate] -= 1
        confidence[other_candidate] += 1
    ```



Um den Wahlkampf zu beenden und die Wahl durchzuführen, muss sich der Bediener nun mit dem Observer-Knoten verbinden und eine Terminierung anfordern:

    $ nc localhost 5000
    STARTTERMINATE 0
    SRC: 0
Wir empfehlen hierzu die Verwendung des Tools *netcat*, wie im oben gezeigten Beispiel. Die Angabe des Methoden-Arguments (in diesem Fall "0") bestimmt den gewählten Startwert für die Terminierungsvektorzeit.

*Wichtig*: Die Angabe des SRC-Parameters ist zwingend erforderlich.

Nach Absetzen der Terminierungsanforderung handelt der Observer zunächst mit allen Knoten eine gültige Terminierungsvektorzeit aus. Sobald diese gefunden wurde fordert der Observer eine Terminierung an. Die Knoten beginnen dann damit ihre Wahlentscheidung an den Observer zu übermitteln und reagieren nicht mehr auf Kampagnen der Kandidaten. Hat der Observer alle Stimmen erhalten, gibt er das Wahlergebnis bekannt.


    06.02.2017 16:00:48 [0] - INFORMATION: Terminating Vector Time s=3250 has been accepted by all nodes.  
    ...
    06.02.2017 16:00:48 [2] - INFORMATION: Request 'SNAPSHOT' from 0  
    ...
    06.02.2017 16:00:48 [0] - INFORMATION: Vote for 2  
    06.02.2017 16:00:48 [0] - INFORMATION: Request 'VOTE' from 1  
    ...
    06.02.2017 16:00:48 [0] - INFORMATION: All Votes have been collected.  
    06.02.2017 16:00:48 [0] - INFORMATION: Elected President: 2  
    06.02.2017 16:00:48 [0] - INFORMATION: Votes for 1: 9
    06.02.2017 16:00:48 [0] - INFORMATION: Votes for 2: 11

### Protokoll
makingavagreatagain greift auf die Protokollimplementierung der vorhergehenden Übungen zurück und verwendet ein auf TCP/IP basierendes einfaches ASCII-basiertes Kommunikationsprotokoll.
Das verwendete Protokoll wird dabei nur zur unidirektionalen Kommunikation verwendet. Das bedeutet, dass eine sendender Client keine direkte Rückmeldung vom Server über die Ausführung des Kommandos erhält.

#### Methoden
Alle Nachrichten bestehen zwangsläufig aus einer sog. *Methode*.
Die Methode ist ein Bezeichner für das auszuführende Kommando.
Das Protokoll kennt folgende Methoden:
* ACK
* APPROVE
* NACK
* REJECT
* RUMOR
* SHUTDOWN
* SNAPSHOT
* STARTCAMPAIGN
* STARTSNAPSHOT
* STARTVOTEFORME
* TERMINATE
* VOTE
* VOTEFORME

In unserem Protokoll kann ein Methodenname von einem Argument gefolgt werden. Es wird als Methoden Argument bezeichnet.

#### Parameter und Identifizierung
Um Nodes zweifelsfrei und eindeutig identifizieren zu können, kennt das verwendete Kommunikationsprotokoll sog. Parameter.
Parameter sind Key-Value Pairs, die durch Zeilenumbruch getrennt unmittelbar auf die Methode folgen.

Jede Nachricht wird vom Sender somit mit einem "SRC" Parameter versehen, der einen Prozess identifiziert.
Weiterhin wird die Vektor-Zeit über einen "VECTIME" Parameter übertragen.

#### Beispiel
    ACK
    SRC: 1
    VECTIME: 



### Algorithmen
Im nachfolgenden werden die Implementierungsdetails der einzelnene Algorithmen erläutert.

#### Flooding
Der Flooding-Algorithmus wird zu Verteilung von `VOTEFORME` Nachrichten genutzt. Er zielt darauf ab, eine Nachricht möglichst schnell in einem Netz zu verteilen. Im vorliegenden Programm, wurde er um eine Quittierung ergänzt, die es dem Initiator des Floodings erlaubt, zu messen, wie viele Teilnehmer seine Nachricht erreicht hat.
#### Echo

#### Terminierung




### Software-Architektur
Die Übung wurde in Java realisiert, und ist durch das vorgegebene Sprache-Paradigma stark objektorientiert aufgebaut.
