# MusServerTCP (TCP Zerbitzaria)

Karpeta honek **MusRoyale** proiektuaren **TCP zerbitzaria** biltzen du. Zerbitzariaren lana da bezeroak (Android eta WPF) konektatzea, partida/gelak sortzea eta kudeatzea, eta mus partidaren logika eta mezuen trukea koordinatzea.

## Zer egiten du zerbitzariak?

- **TCP konexioak** onartu eta kudeatu.
- **Partidak** sortu eta mantendu:
  - partida publikoa (matchmaking modukoa)
  - partida pribatua (kode bidez)
- **Jokalarien egoera** eta **taldeak** antolatu.
- **Partidaren fluxua** gidatu:
  - kartak sortu eta banatu
  - “mus” fasea eta deskarten kudeaketa
  - enbido / jokoaren errondak (handiak, txikiak, pareak, jokua, puntua...)
- **Mezuak bidali** bezeroei (turnoak, informazioa, emaitzak, amaiera, etab.)
- **Deskonexioak/abandonoak** detektatu eta partida “garbi” uzten saiatu.

## Egitura (karpetak)

- `MusServer/`
  - `MusServer.sln` — Soluzioa (Visual Studio)
  - `MusServer/` — Proiektu nagusia
    - `Program.cs` — Zerbitzariaren logika nagusia (matchmaking + partida logika gehiena hemen dago)
    - `MusServer.csproj` — Proiektuaren konfigurazioa
    - `bin/`, `obj/` — Build-aren irteerak (generatuak)

> Oharra: `.vs/`, `bin/` eta `obj/` karpetak normalean ez lirateke Git-era igo behar (generatutako fitxategiak direlako).

## Program.cs: atal nagusiak eta zertarako diren

Zerbitzariaren logika ia guztia `Program.cs` fitxategi bakarrean dago antolatuta. Hona hemen atal garrantzitsuenak:

### 1) Datu egiturak / klaseak

- **`Partida`**
  - Partida/gelaren informazioa gordetzen du (ID-a, kode pribatua, jokalari zerrenda, puntuazioa, baraja/deskarten baraja, etab.).
  - `LockObj` erabiltzen du egoera partekatua babesteko (thread-safety).

- **`Bezeroak`**
  - Jokalarien konexioari lotutako informazioa gordetzen du: `TcpClient`, `NetworkStream`, `StreamReader/Writer`, jokalari zenbakia, taldea, `Id`, eta eskua (kartak).

- **`Pareja`**
  - Egituratuta dago baina gaur egun ez dirudi logika nagusian funtsezko pieza denik (bezero zerrenda + kodea).

### 2) Partiden kudeaketa (publikoa/pribatua)

- **`CrearPartida(...)`**
  - Partida berri bat sortzen du (publikoa edo pribatua, kodearekin).
  - `partidas` eta `partidasPorCodigo` mapetan gordetzen du.

- **`GenerarCodigoUnico()`**
  - Partida pribaturako 4 digituko kode bat sortzen du, errepikatu gabe.

- **`BuscarPartidaPorCodigo(codigo)`**
  - Kode bidez partida pribatua bilatzen du eta beteta ez dagoela egiaztatzen du.

- **`BilatuPartidaById(id)`**
  - ID bidez partida bilatzeko logika (case berezi batzuetarako erabilia).

### 3) Bezero berria prozesatzea (matchmaking + sarrera)

- **`ProcesarNuevoJugador(TcpClient client, ref Partida partidaPublicaActual)`**
  - Bezeroa sartzean, lehen mezuaren arabera erabakitzen du zein “modu” den:
    - `PUBLICA`: partida publikoan sartzen da
    - `CREAR_PRIVADA`: sala pribatua sortu eta kodea bidali
    - `UNIRSE_PRIVADA`: kodea eskatu eta sala pribatuan sartu
    - `ID_ESKATU`: ID bidezko logika osagarria
  - 4 jokalari daudenean, **partida abiarazten** du.

### 4) Zerbitzariaren “main loop”-a (TCP listener)

- **`Main(string[] args)`**
  - TCP listener-a abiarazten du eta bezeroak onartzen ditu loop batean.
  - Une honetan portua **kodean finkatuta** agertzen da: `13000`.
  - Bezero bakoitza task batean tratatzen da (konkurrentzia).

### 5) Partidaren abioa eta bizitza-zikloa

- **`IniciarPartida(Partida partida)`**
  - Partidaren hasierako informazioa bidali (`INFO:` moduko mezua).
  - Kartak banatu (`KartakBanatu`).
  - Partida hasi (`PartidaHasi`).
  - Amaieran partida garbitu eta sala pribatuen kasuan kode-mapetik kendu.

- **`EsperarAbandonoEnSala(Partida partida, Bezeroak b)`**
  - Sala osatu baino lehen norbait ateratzen bada, beste jokalariei `END_GAME` bidali eta sala reset egiten du.

### 6) Jokoko fluxua eta errondak

- **`PartidaHasi(Partida partida)`**
  - Eskua (hasierako jokalaria) aukeratu, txandak kudeatu, eta erronda desberdinak exekutatu:
    - `GRANDES`, `PEQUEÑAS`, `PARES`, `JUEGO` / `PUNTO`
  - Erronda bakoitzean, bezeroen erabakiak jaso eta zabaltzen ditu.

- **Txanden komunikazioa**
  - `zeinenTurnDa(...)`: beste jokalariei noren txanda den jakinarazteko
  - `LeerRespuestaSegura(...)`: bezeroaren erantzuna seguruki irakurri (deskonexio/abandono kasuak kudeatuz)
  - `mezuaJokalariguztientzat(...)`: jokalari guztiei broadcast

### 7) Enbido / puntuazioaren kudeaketa

- **`EnvidoKudeaketa(...)`**
  - Erronda bakoitzeko enbido/jokua/pareen logika antolatzen du.
  - Zeinek daukan “jokua” edo “pareak” lehenik egiaztatzeko zatia ere badu.

- **`ProcesarErabakia(...)`**
  - Jokalarien erabakiak interpretatzen ditu (`paso`, `quiero`, `ordago`, zenbakiak...) eta puntuazioari eragiten dio.
  - Emaitzen laburpenak (`LABURPENA:...`) eta puntu eguneraketak bidaltzen ditu.

### 8) Kartak / baraja / laguntzaile funtzioak

- **`KartakSortu()`**
  - Baraja sortu (paloei eta balioei jarraituz) eta nahasi.

- **`KartakBanatu(Partida partida)`**
  - Jokalari bakoitzari 4 karta bidali.

- **`DeskarteKudeaketa(...)`**, **`musBanatu(...)`**
  - Deskartatutako kartak kudeatu eta berriak banatu (baraja agortzen denean deskarten baraja birziklatuz).

- **Konparazio / kalkulu laguntzaileak**
  - `kartakZenbakiraBihurtu(...)`, `konparatuEskuak(...)`, `analizarPares(...)`, etab.

## Oharrak / Hobekuntza ideiak (aukera)

- Logika handia `Program.cs` fitxategi bakarrean dago; etorkizunean hobe litzateke:
  - `Models/` (Partida, Bezeroak)
  - `Networking/` (listener, protocol handlers)
  - `Game/` (mus logic, envido logic, scoring)
- Portua eta konfigurazioa `appsettings.json` edo antzeko batera eramatea.
