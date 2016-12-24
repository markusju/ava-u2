
AVANode: Eine Python Implementierung von Übung 1
==========================================================
Markus Jungbluth

Dieses Dokument enthält Informationen über den Aufbau und die Funktionen von AVANode.

## Getting Started

Es gibt verschiedene Möglichkeiten AVANode zu nutzen.
Im Hauptverzeichnis finden sich drei Skripte, die die Benutzung der Anwendung auf der Kommandozeile erlauben:

**cli-init.py**
> usage: cli-init.py [-h] nodes edges
> Erzeugt die für den Betrieb der Nodes notwendigen Konfigurationsdateien.

**cli.py**
> usage: cli.py [-h] [--conf-file CONF_FILE] [--dot-file DOT_FILE][--neighbors {dotfile,random}] id
> Startet ein Node mit der angegebenen ID. Standardmäßig sucht das Programm nach den Konfigurationsdateien `file.txt` und `file.dot`

**cli-control.py**
> usage: cli-control.py [-h] host port {rumor,shutdown,shutdownall,init,status} action_args
> Erlaubt unter Angabe von Hostname und Port, eine Verbindung zu einem Node aufzubauen. Die definierte Aktion wird auf dem entfernten Node ausgeführt.


### Beispiel

Folgendes Beispiel zeigt wie ein Netz mit 10 Knoten und 30 Kanten generiert werden kann, welches anschließend zur Verbreitung eines Gerüchts genutzt wird.

Mit Hilfe des `cli-init.py` Skripts generieren wir die für das Netz benötigten Konfigurationsdateien.
> **$** python cli-init.py 10 30

Anschließend starten wir die Server in unterschiedlichen Terminal-Fenstern mit Hilfe des `cli.py` Skripts:
> **$** python cli.py 1

> **$** python cli.py 2

...
> **$** python cli.py 10

Um das Gerücht zu verbreiten, nutzen wir `cli-control.py` und nutzen das erste Node als Initiator:
> **$** python cli-control 127.0.0.1 5000 rumor gurkensalat

Anschließend können wir an einem anderen Node mit Hilfe des Status-Kommandos prüfen, ob und wie sich das Gerücht verbreitet hat:

> $ python cli-control.py 127.0.0.1 5009 status NULL
> [<gurkensalat RECVFROM: set([1, 9]) SENTTO: set([9, 5, 6])>]

## Aufbau
AVANode wurde stark modularisiert und objektorientiert designed.
Die Software ist in verschiedene Teilbereiche untergliedert:
* avanode
    * cli: Funktionen zur Interaktion mit der Kommandozeile
    * client: Command and Control Client zur Manipulation der Nodes
    * file: Klassen für den Zugriff auf Konfigurationsdateien
    * protocol: Klassen für die Verarbeitung, Analyse, Synthese und Evaluation von Protokollnachrichten.
        * analysis
        * commands
    * simu: Klassen für die Durchführung von Testläufen
    * sockets: Klassen für Kommunikationssockets
    * store: Klassen zur Organisation der temporären Datenablage im Node


## Protokoll
AVANode kommuniziert mit einem einfachen ASCII-basierten Protokoll, welches dem HTTP stark ähnelt.

#### Methoden
Alle Nachrichten bestehen zwangsläufig aus einer sog *Methode*.
Die Methode ist ein Bezeichner für das auszuführende Kommando.

Das Protokoll erkennt derzeit folgende Methoden:
* INIT
* SHUTDOWN
* SHUTDOWNALL
* MESSAGE
* RUMOR
* STATUS

In unserem Protokoll kann ein Methodenname von einem Argument gefolgt werden. Es wird als Methoden Argument bezeichnet.

#### Kommunikationsprinzip
Die Kommunikation unter den Nodes und mit dem Initiator Client erfolgt ohne Response. Das andere Node gibt keine Rückmeldung nach dem ein Befehl von einem anderen Node abgesetzt wurde.
Mit Ausnahme des STATUS Kommando sendet das angesprochene Node keine Nachrichten zurück an den Anfragenden.

#### Parameter und Identifizierung
Um Nodes zweifelsfrei und eindeutig identifizieren zu können, kennt das verwendete Kommunikationsprotokoll sog. Parameter.
Parameter sind Key-Value Pairs, die durch Zeilenumbruch getrennt unmittelbar auf die Methode folgt.

Jede Nachricht wird vom Sender somit mit einem "SRC" Parameter versehen, der ein Node identifiziert.

#### Ende-Zeichen
Jede Nachricht wird durch einen doppelten Zeilenumbruch beendet.

#### Beispiel

##### Rumor
```
RUMOR Apfel
SRC: 42
```
##### Status
```
STATUS

RUMOR gurkensalat
RECVDFROM: set([1, 6, 9])
SENTTO: set([9, 5, 6])
BELIEVED: True
```


# Tests

## Automatisierte Tests

Um den Test der Anwendung zu erleichtern, wurde ein Test-Framework erstellt welches die Durchführung von Versuchen erleichtert.
Das Framework startet dazu zunächst eine vordefinierte Anzahl von Knoten, führt die Tests durch, misst die Parameter an allen Nodes und fährt sie anschließend wieder herunter.

Das Testframework wurde in der Datei `test-run.py` abgelegt. Durch Anpassung der dort befindlichen Variable `tests` können verschiedene Parameter für durchzuführende Tests bestimmt werden:

```
tests = [
    (100, 150, 3, "a.1", 1),
    (100, 150, 3, "a.2", 1),
    (100, 150, 3, "a.3", 1),
    (100, 150, 6, "b.1", 1),
    (100, 150, 6, "b.2", 1),
    (100, 150, 6, "b.3", 1),
    (100, 150, 12, "c.1", 1),
    (100, 150, 12, "c.2", 1),
    (100, 150, 12, "c.3", 1),
    (100, 200, 2, "d.1", 1),
    (100, 200, 2, "d.2", 1),
    (100, 200, 2, "d.3", 1),
    (500, 501, 3, "e.1", 1),
    (500, 501, 3, "e.2", 1),
    (500, 501, 3, "e.3", 1),
    (500, 900, 3, "f.1", 1),
    (500, 900, 3, "f.2", 1),
    (500, 900, 3, "f.3", 1),
    (1000, 1900, 3, "g.1", 1),
    (1000, 1900, 3, "g.2", 1)
]
```

Die Testumgebung kann wie folgt gestartet werden:
> **$** python test-run.py

Nach einer Weile gibt das Skript ein detailliertes Test-Protokoll aus:
```
....
Test completed:
Nodes	Edges	    BelieveCount	Rumor	InitNode		Believers       BelieverPercentage
100		150		    6				b.1		1				3				3
500		900		    3				f.3		1				197				39
100		150		    12				c.1		1				0				0
100		150		    3				a.2		1				30				30
1000	1900	    3				g.2		1				453				45
...
```

## Auswertung
Die nachstehende Tabelle zeigt die Ergebnisse von Testläufen unserer Lösung.
Anhand der Ergebnisse, kann man einige offensichtliche Gesetzmäßigkeiten feststellen:

Für einen gleichbleibenden BelieveCount c und eine gleichbleibende Knoten-Zahl, ist die Zahl der Nodes, die das Gerücht glauben um so größer, je größer die Zahl der Kanten ist.

Für eine gleichbleibende Anzahl an Knoten und Kanten ist die Zahl der Nodes, die das Gerücht glauben, um so kleiner, je größer der BelieveCount c ist.



| Nodes | Edges | BelieveCount c   | Rumor | InitNode | Believers | Percentage | AvgNodeDeg |
|-------|-------|------------------|-------|----------|-----------|------------|------------|
| 100   | 150   | 3                | a.1   | 1        | 26        | 26         | 2.01       |
| 100   | 150   | 3                | a.2   | 1        | 23        | 23         | 2          |
| 100   | 150   | 3                | a.3   | 1        | 27        | 27         | 2.01       |
| 100   | 150   | 6                | b.1   | 1        | 1         | 1          | 2.01       |
| 100   | 150   | 6                | b.2   | 1        | 3         | 3          | 2.01       |
| 100   | 150   | 6                | b.3   | 1        | 4         | 4          | 2.01       |
| 100   | 150   | 12               | c.1   | 1        | 0         | 0          | 2.01       |
| 100   | 150   | 12               | c.2   | 1        | 0         | 0          | 2.01       |
| 100   | 150   | 12               | c.3   | 1        | 0         | 0          | 2          |
| 100   | 200   | 2                | d.1   | 1        | 95        | 95         | 3.01       |
| 100   | 200   | 2                | d.2   | 1        | 96        | 96         | 3          |
| 100   | 200   | 2                | d.3   | 1        | 96        | 96         | 3.01       |
| 500   | 501   | 3                | e.1   | 1        | 1         | 0.2        | 1.004      |
| 500   | 501   | 3                | e.2   | 1        | 1         | 0.2        | 1.006      |
| 500   | 501   | 3                | e.3   | 1        | 1         | 0.2        | 1.006      |
| 500   | 900   | 3                | f.1   | 1        | 199       | 39.8       | 2.538      |
| 500   | 900   | 3                | f.2   | 1        | 5         | 1          | 1.312      |
| 500   | 900   | 3                | f.3   | 1        | 203       | 40.6       | 2.602      |
| 1000  | 1900  | 3                | g.1   | 1        | 462       | 46.2       | 2.8        |
| 1000  | 1900  | 3                | g.2   | 1        | 458       | 45.8       | 2.795      |
