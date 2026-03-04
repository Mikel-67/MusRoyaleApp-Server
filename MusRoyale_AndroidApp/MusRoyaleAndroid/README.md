## Kodearen egitura: Android bezeroak non du logika?

Android aplikazioan logika normalean **Kotlin fitxategi askotan** banatuta dago (ez dago “Program.cs” bakar batean). Hala ere, antzeko moduan ulertzeko, hona hemen atal nagusi tipikoak eta zertarako diren:

### 1) Datu egiturak / modeloak
- **Jokalaria / User / Player modeloa**
  - ID/izena, posizioa, taldea, egoera… gordetzeko.
- **Partida / Room / Match modeloa**
  - partida mota (publikoa/pribatua), kodea, jokalari zerrenda, puntuazioa…
- **Kartak / Hand modeloa**
  - erabiltzailearen eskua (4 karta), deskartak, eta fasearen araberako kalkuluak (badaude).
- **Mezu/Evento modeloa (protocol message)**
  - zerbitzaritik iristen diren mezuak (INFO, TURN, CARDS, RONDA, LABURPENA…) modu egituratuan tratatzeko.

### 2) Sare konexioa (TCP client)
- **TCP konexioa ireki/itxi**
  - `host:port` erabilita socket-a sortu.
- **Irakurle/idazlea**
  - normalean `BufferedReader/PrintWriter` edo antzekoak, lineaz-linea mezuak bidali/jasotzeko.
- **Listener loop / coroutine**
  - atzeko harian (Thread/Coroutine) zerbitzaritik datozen mezuak etengabe irakurri eta aplikazioari “event” moduan pasatu.

### 3) Sarrera + matchmaking (publikoa/pribatua)
- **Menu/logika**
  - erabiltzaileak aukeratzen du:
    - publikoa
    - pribatua sortu
    - pribatua sartu (kodea)
- **Lehen mezuak zerbitzarira**
  - aukeraren arabera, zerbitzariari “modua” bidali eta erantzuna jaso (`OK`, `CODIGO:####`, errorea…).
- **Lobby pantaila**
  - 4 jokalari bete arte zain egon, edo abandonua/errorea gertatuz gero atzera bueltatu.

### 4) UI geruzak (pantailak)
Normalean pantaila edo “screen” hauek egoten dira:
- **Home/Menu** (publikoa/pribatua aukeratzeko)
- **Lobby/Waiting room** (itxaron gela)
- **Game screen** (kartak, botoiak, txanda, erronda)
- **Result/End screen** (amaierako emaitza)

### 5) Partidaren hasiera eta egoera sinkronizazioa
- **INFO tratamendua**
  - jokalariak/taldeak/posizioak jaso eta UI eguneratu.
- **CARDS tratamendua**
  - `CARDS` mezuaren ondoren 4 karta jaso eta eskua erakutsi.
- **Egoera globala**
  - “zein erronda/zein fase” gauden eta puntuazioa, lokalean gordeta UI berriz marrazteko.

### 6) Txandak eta erabiltzailearen erabakiak
- **TURN tratamendua**
  - “nire txanda” bada botoiak aktibatu; bestela desaktibatu.
- **Erabakiak bidaltzea**
  - botoiek bidaltzen dituzten mezuak:
    - mus fasea (`mus` / ez mus)
    - enbido/jokua (`paso`, `quiero`, `ordago`, zenbakiak…)
    - deskartak (karta zerrenda)
- **Beste jokalarien mugimenduak**
  - zerbitzar
