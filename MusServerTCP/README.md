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
## Zerbitzaria nola abiarazi (MusServerTCP)

TCP zerbitzaria `MusServerTCP/MusServer/MusServer/Program.cs` fitxategitik abiatzen da. Une honetan **portua kodean finkatuta** dago:

- **Portua:** `13000` (`Main` metodoan)

### Aukera 1: Visual Studio (gomendatua Windows-en)
1. Ireki soluzioa: `MusServerTCP/MusServer/MusServer.sln`
2. Aukeratu proiektu nagusia: `MusServer`
3. Exekutatu (**Start**). Kontsolan honelako mezua agertu behar da:
   - `Zerbitzaria irekita, zain...`

### Aukera 2: .NET CLI (terminaletik)
`MusServer.csproj` dagoen karpetara joan eta exekutatu:

```bash
cd MusServerTCP/MusServer/MusServer
dotnet run
```

Kontsolan `Zerbitzaria irekita, zain...` ikusten baduzu, zerbitzaria martxan dago.

### Sare/Firewall oharra
Bezeroak beste makina batetik konektatuko badira, ziurtatu:
- **`13000` portua irekita** dagoela (Windows Firewall / router-a)
- Zerbitzariaren **IP/host** zuzena erabiltzen ari zarela bezeroan
