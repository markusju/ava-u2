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

*Voters* und *Candidates* stehen wiederum in direkter Beziehung zueinander. Beide Knoten-Typen haben eine festgelegte Anzahl an Peers. Bei *Candidates* werden diese als *Partei-Freunde*, bei *Voters* als *Freunde* bezeichnet.
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

Die Beziehungen zwischen den Konten werden zufällig festgelegt, hierbei wird ein zusammenhängender Graph unter bestimmten Auflagen erzeugt:

* Kandidaten dürfen nicht untereinander verbunden sein
* Ein Parteifreund darf nur mit einem Kandidaten verbunden sein
* Ein Kandidat muss exakt **s** Verbindungen besitzen
* Ein Wähler muss exakt **f** Verbindungen besitzen
* Es gelte: **s** >= **f**

## Implementierung

### Getting Started
Die vorliegende Software bietet verschiedene Möglichkeiten die Algorithmen zu Testen. Um dem Nutzer eine möglichst einfache Handhabung zu ermöglichen, haben wir einen Einstiegspunkt "CliElection" eingerichtet, der einen zufällige Umgebung anhand der übermittelten Parameter erstellt und den Wahlkampf bei beiden Kandidaten initiiert.

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

1) Anzahl der Knoten
2) Anzahl der Partei-Freunde
3) Anzahl der Freunde
4) Wahl-Kampagnen direkt starten? (true/false)

Nach Ausführen des Skripts (mit Parameter Nr. 4 auf true) beginnen die Kandidaten ihren Wahlkampf:

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
makingavagreatagain greift auf die Protokoll-Implementierung der vorhergehenden Übungen zurück und verwendet ein auf TCP/IP basierendes einfaches ASCII-basiertes Kommunikationsprotokoll.
Das verwendete Protokoll wird dabei nur zur unidirektionalen Kommunikation verwendet. Das bedeutet, dass eine sendender Client keine direkte Rückmeldung vom Server über die Ausführung des Kommandos erhält.

    <Methode> <MethodenArgument>
    <ParamKey>: <ParamValue
    ...

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
    VOTEFORME 2
    SRC: 12
    ID: 22
    VECTIME: {0=0, 1=34, 2=67, 3=11, 4=13, 5=11, 6=12, 7=13, 8=8, 9=15, 10=17, 11=14, 12=20, 13=13, 14=15, 15=15, 16=17, 17=13, 18=16, 19=13, 20=7}



### Algorithmen
Im nachfolgenden werden die Implementierungsdetails der einzelne Algorithmen erläutert.

#### Flooding
Der Flooding-Algorithmus wird zur Verteilung von `VOTEFORME` Nachrichten genutzt. Er zielt darauf ab, eine Nachricht möglichst schnell in einem Netz zu verteilen. Im vorliegenden Programm wurde er um eine Quittierung ergänzt, die es dem Initiator des Floodings erlaubt, zu messen, wie viele Teilnehmer seine Nachricht erreicht hat.

Verwendete Protokoll-Methoden: `STARTVOTEFORME`, `VOTEFORME`, `APPROVE`, `REJECT`

Der Flooding-Algorithmus wurde im vorliegenden Programm wie folgt implementiert:

###### Initiator
    if not candidate:
        raise Error("Must be candidate to initiate")
    
    for my_party_fellows_id in nodes:
        send(my_party_fellows_id, VOTEFORME, myId, uniqueVoteformeId)
        
Ein Initiator-Knoten -also der Kandidat- bestimmt zunächst einen eindeutigen Identifizierer für seine Wähl-Mich Aktion. Anschließend sendet er ein `VOTEFORME` Kommando an seine Parteifreunde. Diese und alle anderen Knoten verteilen die Aktion anschließend gemäß der Vorschriften aus unten stehendem Pseudo-Code:
###### Restliche Knoten
    if candidate:
        raise Error("Cannot be candidate for this")
     
     (method, candidateId, uniqueVoteformeId) = recv()
     
     if (already_seen(uniqueVoteformeId)):
        raise Warning("I have seen this already")
        return
        
     if i_approve_this_candidate():
        send(candidateId, APPROVE, uniqueVoteformeId)
     else:
        send(candidateId, REJECT, uniqueVoteformeId)
        return
     
     for my_friends_id_except_source in nodes:
        send(my_friends_id, VOTEFORME, candidateId, uniqueVoteformeId)
        
Hier wird zunächst die Nachricht empfangen und ihre Parameter werden aufgeschlüsselt. Im nächsten Schritt prüft der Knoten, ob er den eindeutigen Identifizierer der Aktuíon bereits gesehen hat. Ist dies der Fall bricht er ab und verarbeitet das Kommando nicht weiter.

Ist der aktuelle Knoten vom Kandidaten überzeugt, der die Aktion initiiert hat, sendet er ihm eine Bestätigung in Form einer `APPROVE` Nachricht. Ist er nicht vom Kandidaten signalisiert der Knoten dem Kandidaten mit einer `REJECT` Nachricht seinen Unmut. In diesem Fall bricht er ab und verarbeitet das Kommando nicht weiter.
 
Trifft die Aktion des Kandidaten die Zustimmung des Knotens, so verteilter die Aktion weiter an seine Freunde. (außer an den Knoten, von dem er die Nachricht erhalten hat)
#### Echo
Der Echo-Algorithmus wird zur Verteilung von `CAMPAIGN` Nachrichten verwendet. Er zielt darauf auf ab, alle Teilnehmer in einem Netz zu erreichen und zu bestätigen, dass tatsächlich alle Teilnehmer die Nachrichte erhalten haben. Anders als beim Flooding erhält der Initiator die Quittierung nicht direkt von dem angesprochenen Node, sondern indirekt.


Verwendete Protokoll-Methoden: `STARTCAMPAIGN`, `CAMPAIGN`


Der Echo-Algorithmus wurde wie folgt implementiert:

###### Initiator
    if not a candidate:
        raise Error("Must be candidate to initiate")
    
    if state != WHITE:
        raise Error("Invalid State for init")
    
    state = RED
    
    uniqueCampaignId = genId()
       
    for partyFellow_id in nodes:
        send(partyFellow_id, CAMPAIGN, myId, uniqueCampaignId)
    
Ein Initiator-Knoten -also ein Kandidat- startet die Kampagne in dem er seinen eigenen Zustand von WHITE zu RED ändert, Anschließend sendet er allen seinen Parteifreunden eine `CAMPAIGN`Nachricht mit einer eindeutigen, gleichbleibenden campaignId.

###### Restliche
    (source, candId, campaignId) = recv()

    //EXPLORER
    if (state == WHITE and not a candidate):
        state = RED
        firstNeighbor = source
        
        for neighbors_id in nodes:
            send(neighbors_id, CAMPAIGN, candId, campaignId)
               
    msgCounter += 1
     
    //ECHO
    if (msgCounter == numOfNeighbors):
        state = GREEN
        
        if (ownId == candId):
            msgCounter = 0
            state = WHITE
            triggerFeedback()
            return
            
        send(firstNeighbor, CAMPAIGN, candId, campaignId)
        msgCounter = 0
        state = WHITE

Die restlichen Knoten verarbeiten die ankommenden `CAMPAIGN`Nachrichten gemäß des obenstehenden Pseudo-Codes. Abhängig vom Zustand des Nodes werden die ankommenden Nachrichten entweder als *EXPLORER* oder *ECHO* implementiert.

Ist der Zustand des Knotens WHITE, so interpretiert er die Nachricht als EXPLORER. Er setzt dann seinen Zustand auf RED und sichert die ID des ersten Nachbarn von dem er die Nachricht erhalten hat (firstNeighbor). Anschließend zählt er den msgCounter hoch.
Nachdem er eine EXPLORER Nachricht erhalten hat, werden alle weiteren NAchrichten gezählt, die der Knoten von allen anderen erhält. Erreicht der msgCounter die Zahl der Nachbarn des Knoten, dann ist die Nachricht vollständig an die Umgebung übertragen worden. Der Knoten löst dann den ECHO-Mechanismus aus und bestätigt dem ersten Nachbarn über den er die Kampagne erhalten hat, dass die Nachricht an alle seine Nachbarn übertragen wurde.

Anschließend setzt der Konten seinen Zustand zurück auf WHITE und den msgCounter auf 0. Handelt es sich bei dem Knoten um den Initiator, so löst er zusätzlich den Feedback-Mechanismus für eine erneute Wahlkampfaktion aus.

#### Terminierung
Damit eine laufenden Wahl und die damit laufenden Aktionen unterbrochen werden können um eine Wahlergebnis zu ermitteln, implementiert unsere Software einen Terminierungs-Algorithmus. Er basiert auf der Vektorzeit Implementierung und wird durch den Observer-Node gesteuert.

Im nachfolgenden beschreiben wir die Funktionalität des Algorithmus:

###### Observer-Node (Initiator)
    if not observer:
        raise Error("Must be observer to init")
        
    s = 0
        
    for node in all_nodes:
        send(node, TERMINATE, s)
   
Der Observer nimmt einen Initialisierungsbefehl entgegen und startet den Terminierungs-Prozess. Dazu wird ihm ein Startwert *s* übergeben, der als Startwert für die zu bestimmende Terminierungs-Vektorzeit genutzt wird. Anschließend sondiert er alle anderen Knoten mit einer `TERMINATE` Nachricht. Die Knoten signalisieren dem Observer wiederum mit einer `ACK` oder `NACK` Nachricht, ob sie die gewählte Zeit akzeptieren.

Die Knoten akzeptieren die Zeit nur, wenn sie größer als ihre eigener  Vektor-Zeitstempel ist.

###### Observer-Node (Verarbeitung von ankommenden NACK/ACK)
    
 
    //constructor
    counter = 0
    resetCounter = 0
    nack_recv = false
    
    ---
    
    (type, ...) = recv()
     
    if (type == NACK)
        nack_recv = true
        
    counter += 1
    
    if (counter == num_of_nodes)
        if nack_recv:
            resetCounter += 1
            restart with s=(s+10)*resetCounter

Erhält der Initiator mindestens eine `NACK` Nachricht, so eskaliert er den Startwert annähernd exponentiell um schnell einen gültigen Wert zu finden. Die Formel für die Eskalation ist wie folgt festgelegt:
    
    s = (s+10)*AnzahlVersuche
        
        
###### Restliche Knoten
Wie oben bereits erwähnt, müssen die Knoten die empfangende terminierende Vektorzeit prüfen und dem Observer mitteilen, ob sie für sie gültig ist:

    (type, s, src) = recv()
    
    if (s > myVectime):
        send(src, ACK)
        setTerminatingVectime(s)
    else:
        send(src, NACK)
    
#### Feedback-Mechanismus
Damit die Kandidaten in periodischen Abständen immer wieder neue Wahlkampfaktionen starten, wurde ein Feedback-Mechanismus implementiert. Dieser wählt bei Auslösung eine zufällig Wahlkampfaktion (CAMPAIGN oder VOTEFORME) und löst sie durch eine entsprechende Initiator-Nachricht am jeweiligen Kandidaten aus.

Hierbei mussten wir einige Besonderheiten beachten:

Es war gefordert, dass ein Kandidat nach *r* Rückmeldungen eine neue Aktion startet. Wir haben deshalb einen Counter mit entsprechender Überprüfungs-Routine implementiert:

    counter = 0
    r = 6
    
    def incCounter():
        counter += 1
        if (counter % 6 == 0):
            triggerThreshold()
   
    def incToThreshold():
        counter += r
    
    def triggerThreshold():
        startRandomAction()
        
Dieser lässt sich über eine Methode inkrementieren und löst entsprechend eine neue Aktion aus, wenn der definierte Schwellenwert *r* überschritten wurde.

In den Routinen zur Verarbeitung der `APPROVE` und `REJECT` Nachrichten der VOTEFORME-Aktion haben wir einen Aufruf von *incCounter()* integriert. Bei den CAMPAIG-Aktion lösen wir die Threshold-Aktion direkt bei Beendigung des ECHO-Algorithmus beim Initiator aus.

### Software-Architektur
Die Übung wurde in Java realisiert und ist durch das vorgegebene Sprachen-Paradigma stark objektorientiert aufgebaut.

Eine Klasse `NodeCore` verankert alle Funktionalitäten eines Knoten und hält ensprechende Instanzen der Untermodule:

* NodeCore
    * nodeID
    * nodeType
    * feedhackThreshold
    * TCP-Server
    * TCP-Client
    * Konfigurationsmodul (Adressen der anderen Nodes/Nachbarn)
    * Datenhaltungsmodul (vgl. Einleitung)
    * Logger-Modul
    
