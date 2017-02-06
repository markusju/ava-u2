Abnahme Übung 2:




1)
	* Rumor mit Vectime

2)
	* Überblick über Struktur
		* Node-Types
			* OBSERVER; CANDIDATE; VOTER

		* Server mit Sync-Mutex für Requests -> Request Queue
			* Nachrichten werden jederzeit angenommen, aber genau in der Reihenfolge verarbeitet wie sie ankamen.
			* Andernfalls überholen sich Nachrichten!

		* DataStore-Klasse mit Managern für verschiedene Anwendungen

		* FileConfig-Klasse mit Konfigurationen
			* Dot-Datentyp

		* Protokoll-Klasse (derzeit etwas verwurstet)
			* Command-Types und Commands
				* ACK, NACK, TERMINATE, STARTTERMINATE, SNAPSHOT, VOTE
				* RUMOR
				* SHUTDOWN
				* VOTEFORME, STARTVOTEFORME, REJECT, APPROVE
				* CAMPAIGN
			* Mapping-Request-Methode auf Klasseninstanz
		* Feste-Inegrierung der Vektorzeit in Sende und Empfangsmodulen

	Implementierung:

	* Graph-Generierung

	* VOTEFORME (Einfaches Flooding)
		* Justierung der Confidence
		* Signalling Approval or Rejection...
			APPROVE, REJECT
		* Relaying VOTEFORME
		* Alle VOTEFORMEs haben eine eindeutige ID
			10 20
			11 21
			12 22
			13 23
			.....
			199 299
 		* Initierung über STARTVOTEFORME
		* Feedback-Mechanismus (FeedbackManager, FeedbackObserver&FeedbackTeam)

	* CAMPAIGN (ECHO)
		* Intierung über STARTCAMPAIGN
		* Alle CAMPAIGNs haben eine eindeutige ID (schöpfen aus dem selber Counter wie VOTEFORME)
			-> Gesamter Evaluierungsblock ist eine CS
		* Datenhaltung (CampaignManager)
			* Zustandsbehaftung (WHITE; RED; GREEN)
			* First Neighbor
			* MessageCounter

	* TERMINATE
		* Initierung mit STARTTERMINATE <initVal> an OBSERVER knoten
		* TERMINATE <termTime> an Knoten
			* Bestätigung ob gültige Zeit mit ACK/NACK
		* Sende solange TERMINATE bis, kein NACK mehr zurückkommt
			* eskaliere die gewählte TERM-time dabei wie folgt
				Sei s die gewählte TERM-time:
					s = (s+10) * AnzahlVersuche
		* Wenn alle Knoten die TERM-time akzeptiert haben sende SNAPSHOT
			* SNAPSHOT setzt VECTOR-Zeit auf gesetztes LIMIT hoch
				-> Konten terminiert und lässt nur noch 
						ALLOWED_AFTER_TERMINATION = {"STATUS", "ACK", "NACK", "TERMINATE", "STARTTERMINATE"}
				   zu

				-> Konten übermittelt dem OBSERVER per VOTE-Nachricht seinen gewählten Präsidenten
				-> Observer verkündetet Wahl-Ergebnis...






