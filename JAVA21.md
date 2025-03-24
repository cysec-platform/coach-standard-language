# Updates zu Java 21 - How

## Grosser Blocker:

Bevor ich zum Rest komme, ein Blocker an dem das Upgrade auf 21 momentan scheitern
würde, ist eine Inkompatibilität von JAXB mit dem strikteren Modul-System in neueren
Java-versionen. Wenn es versucht, ein Datum in einem Audit zu klonen (u.A.), kann es
die `.clone()` Methode nicht `public` machen, weil `java.xml` das Package nicht so
exportiert.

Ich hatte leider keinen Ansatz gefunden, ob man das direkt fixen könnte, und/oder ob
eine neuere Version von JAXB das bereits löst. Es ist definitiv nicht unmöglich, aber
es müsste separat Zeit aufgewendet werden.

Schritte zum Wiederholen:

- Dev Container mit `tomcat:9-jdk21` Builden, diesen Branch auswählen und dann Tomcat/catalina starten.
- Im Coach eine Antwort angeben.
- Im Browser sollte ein 500 Error kommen, in der Catalina-Konsole sollte die
  `InaccessibleObjectException` stehen.

## Beschreibung

Wie versprochen hier ein kleiner Überblick, was ein Upgrade auf Java 21 umfassen würde:

- Das Dev Environment kann dieselbe Tomcat-Version (9) beibehalten, indem einfach das
 `tomcat:9-jdk21` Image verwendet wird.
- Anpassungen in den pom-Files sind gering, Version 21 wird im Compiler-Plugin als
  Release-Version festgelegt und das ist es.
- Jabel, das Plugin welches Java 9+ Syntax (v.A. Switch-Expressions und Records)
  in alte Versionen von Java übersetzen kann (Java 8), unterstützt offiziell Java 20-21
  mit der neusten Version von 2023 - die Version ist aber nicht auf Maven-Repositories,
  und eine alternative Version funktionierte auch nicht. Die letzte offizielle Version auf
  Maven (1.0.0) würde mit Java 17 vielleicht gehen, ich habe das aber nicht weiter ausprobiert.
- `Atom` und `Command` können leider nicht so sehr von Records profitieren, wie ich mir
  erst gedacht habe. Beide haben mutablen Zustand, weswegen sie nicht einfach so zu einem
  Interface überführt werden können (Records können keine Superklassen haben, nur -Interfaces).
  - `Atom`  hat das Problem, dass `parentPointer` als mutables Feld modelliert wird.
    Soweit ich das beurteilen kann, ist das Feld aber eigentlich "nur" für Methoden-Atome
    relevant und wird auch, soweit ich das sehen kann, nur bei der initialen Erstellung überhaupt gesetzt,
    es wäre also möglich, es in das `MethodAtom` Record zu überführen als erzwungenes Feld,
    und `getExecutorContext` dementsprechend angepasst (wenn es überhaupt für andere Atome wichtig ist).
  - `Command` hat als Feld den Namen des Kommandos, welcher auch beim Parsing verwendet wird.
    Der Wert wird bei Initialisierung der HashMap aller Kommandos zugewiesen, und so wird sichergestellt,
    dass der Key in der Map zum Namen des Kommandos passt.

    Ein alternatives Setup, in dem der Name entweder nicht beim Kommando gespeichert wird, oder
    eine separate (statische) HashMap das Mapping in die andere Richtung übernimmt, wäre zwar möglich,
    aber nicht mehr so ergonomisch und/oder sicher wie es momentan ist.
- Es war aber möglich, zwei sehr logisch kohärente Subsets an Kommandos mittels dem sealed-Modifier
  in jeweils eine Datei zu ziehen, auch wenn `sealed` dafür nicht *notwendig* ist (`CommandAbstractBoolOp`
  und `CommandNumberBinaryPredicate`).
- Ein Redesign von `Atom` zumindest könnte dienlich sein, was die Typ-Sicherheit der `id`-Werte angeht - Zahlen
  könnten den rohen Wert speichern und verfügbar machen, ohne das Interface aufzublähen, und man würde damit
  ständiges Re-Parsing vermeiden.
- `questions.*` ist zu viel mit innerem Zustand beschäftigt, als dass ein Record-Redesign gut reinpassen würde.
- `CySeCExecutorContextFactory` profitiert ein wenig von `instanceof`-Patterns.
- `ParserLine` könnte in der Theorie von Switch-Expressions profitieen, ich habe es aber ungeändert
  gelassen, weil es in meinen Augen die "stabilste" Komponente in der CSL sein könnte (im Sinne, dass
  nicht mehr Sachen geändert/erweitert werden müssen).
- `CoachContext` könnte wesentlich kompakter als Record sein, WENN Java Wither-Methoden anbieten würde.
  Dank der mutablen Felder hilft ein Record aber nicht ohne wesentlich mehr Overhead.
