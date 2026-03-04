## Kodearen egitura: WPF bezeroak non du logika?

WPF aplikazioan logika **C# fitxategi askotan** banatuta dago, eta normalean MVVM antzeko egitura erabiltzen da: **View (XAML)** + **ViewModel/Model (C#)** + **Services (sarea/DB/kanpokoak)**. Android bezeroaren adibidearen egitura berdina jarraituz, hona hemen MusRoyalePC proiektuan logika non dagoen (zure karpeten izenekin):

### 1) Datu egiturak / modeloak (Models)
`MusRoyale_WPF/MusRoyalePC/Models/` karpetan daude aplikazioak erabiltzen dituen klase nagusiak:
- **User / erabiltzailearen egoera**
  - `User.cs`, `UserSession.cs`: erabiltzailearen datuak eta saioaren identifikazioa (adib. DocumentId).
- **Nabigazioa / UI egoera**
  - `NavTab.cs`: zein tab/pantaila dagoen hautatua.
- **ViewModel-ak (pantailen logika)**
  - `LoginViewModel.cs`: login formularioaren logika (errore mezuak, validazioa, login ekintza…).
  - `MainViewModel.cs`: aplikazioaren egoera orokorra (menua/flow-a).
  - `LagunakViewModel.cs` + `Laguna.cs`: lagunen zerrenda eta lagun baten eredua.
  - `BikoteakViewModel.cs`: “Bikoteak/Duo” moduko pantailaren logika (aukerak, popup-ak, komandoak…).

### 2) Sare konexioa (TCP client) eta kanpoko zerbitzuak (Services)
`MusRoyale_WPF/MusRoyalePC/Services/` karpetan dago “kanpoko komunikazioa”:
- **TCP bezero nagusia**
  - `MusClientService.cs`: TCP konexioa kudeatu, mezuak bidali/jaso eta serverretik datozen komandoak UI-ra bideratu.
    - Entzute-loop bat abiarazten du (listen task) eta `CARDS`, `TURN`, `PUNTUAKJASO`, `GRANDES/PEQUEÑAS/PARES/JUEGO`, `ERABAKIA`… bezalako seinaleak tratatzen ditu.
- **Autentifikazioa / erabiltzaileak**
  - `IAuthService.cs`: auth zerbitzuaren interfazea (ViewModel-etan mockeatzeko ere erabiltzen da).
  - `FirestoreService.cs`: Firestore-rekin lotutako kontsultak/eragiketak.
- **Matchmaking/duo koordinazioa**
  - `DuoMatchmakingService.cs` eta `DuoInviteCoordinator.cs`: bikoteka jolasteko gonbidapen/parekatze logika.

### 3) Sarrera + matchmaking (publikoa/pribatua/bikoteak)
Aukera/flow nagusiak normalean hemen banatzen dira:
- **Menuko aukeraketa eta view-aldaketak**
  - `Controllers/MainController.cs`: erabiltzaileak modua aukeratzen duenean (PartidaAzkarra / Bikoteak / Pribatua), dagokion view-a kargatzen du.
- **Zerbitzariarekin lehen “handshake”-a**
  - `MusClientService.cs`: modua bidali (PUBLICA/BIKOTEAK/PRIBATUA…) eta “OK” edo “kodea” bezalako erantzunak jaso ondoren, entzutea martxan jarri.

### 4) UI geruzak (pantailak) (Views)
`MusRoyale_WPF/MusRoyalePC/Views/` karpetan daude XAML pantailak (eta code-behind):
- **Login/Erregistroa**
  - `LoginView.xaml`, `RegisterView.xaml`
- **Menu/aukera pantailak**
  - `PartidaAzkarraView.xaml`, `PribatuaView.xaml`, `BikoteakView.xaml`
- **Jokoa**
  - `PartidaView.xaml` (eta `.xaml.cs`): partida barruko UI + server mezuen interpretazio asko hemen egiten da (adib. `TURN;...`, `RONDA:...`, `LABURPENA:...`, `END_GAME`).
- **Soziala**
  - `LagunakView.xaml`, `ChatView.xaml`
- **Profila**
  - `PerfilaView.xaml`
- **Controls/**
  - berrerabil daitezkeen kontrolak/komponenteak.

### 5) Partidaren hasiera eta egoera sinkronizazioa
WPF bezeroan sinkronizazioa bi mailatan ikusten da:
- **Sare maila (Services)**
  - `MusClientService.cs`-k serverretik datozen komando batzuk zuzenean “event” moduan igortzen ditu (adib. `TURN`, `CARDS`, `PUNTUAKJASO`…).
- **UI maila (Views)**
  - `PartidaView.xaml.cs`-k mezu “konplexuagoak” parseatzen ditu, adibidez:
    - `RONDA:...` → erronda/fasearen UI aldatu
    - `LABURPENA:...` → laburpena/puntuak erakutsi
    - `TURN;uid;seat` → nori dagokion txanda kalkulatu eta countdown/markak jarri
    - `END_GAME` → partida amaitu / deskonexio mezuak

### 6) Txandak eta erabiltzailearen erabakiak
- **Txanda jasotzea**
  - `MusClientService.cs`-k `TURN` jasotzean, UI-ra seinale bat bidaltzen du.
  - `PartidaView.xaml.cs`-k `TURN;...` formatuarekin datorrena interpretatu dezake (uid/seat mapatuz) eta UI-n nori tokatzen zaion markatu.
- **Erabakiak bidaltzea**
  - Normalean botoiek/komandoek `MusClientService`-ra deitzen dute, eta honek socket-era bidaltzen du (mus/ez mus, enbidoak, deskartak, etab.).
- **Beste jokalarien mugimenduak**
  - `ERABAKIA` bezalako mezuak UI-ra helarazten dira (adib. `ERABAKIA|uid;serverId;mensaje`), eta “feed/log” moduan erakutsi daitezke.

### 7) Errondak + puntuazioa
- **Erronda aldaketak**
  - Serverretik `RONDA:...` edo fase izenak (GRANDES/PEQUEÑAS/PARES/JUEGO) iritsi daitezke, eta UI-k horren arabera egoera/pantaila egokitzen du.
- **Puntuak**
  - `PUNTUAKJASO` bezalako komandoetan puntuak lerroz-lerro jasotzen dira eta markagailua eguneratzen da.
  - `LABURPENA:...` mezuak errondaren laburpena/puntuak erakusteko erabiltzen dira.

### 8) Error/exit kudeaketa (deskonexioak)
- **END_GAME**
  - `PartidaView.xaml.cs`-k `END_GAME` antzematen duenean, erabiltzaileari amaiera/deskoneksio mezua erakutsi eta flow-a itzuli dezake.
- **Socket itxiera / null ReadLine**
  - `MusClientService.cs`-ko entzute-loop-ean serverrak konexioa ixten badu, loop-a bukatu eta UI-ra egoera/errorea pasa daiteke.

> Oharra: atal hau “eredu” modukoa da, baina zure repoan karpeta eta fitxategi hauek daude benetan: `Controllers/`, `Models/`, `Services/`, `Views/` eta bertan banatzen da logika (UI + state/viewmodels + network/services).
