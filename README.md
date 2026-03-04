# MusRoyale

**MusRoyale** mus jokoaren inguruko sistema/aplikazio multzo bat da, 3 proiektu nagusitan banatuta:

- **Android aplikazioa** (Kotlin): jokalarientzako bezero mugikorra.
- **WPF aplikazioa** (C#): Windows mahaigaineko bezeroa.
- **TCP Zerbitzaria** (C#): logika nagusia eta bezeroen komunikazioa.

## Repo egitura

- `MusRoyale_AndroidApp/` — Android bezeroa (Kotlin)
- `MusRoyale_WPF/` — WPF bezeroa (C#)
- `MusServerTCP/` — TCP zerbitzaria (C#)

## Hasiera azkarra

### Android (Kotlin)
1. Ireki `MusRoyale_AndroidApp` Android Studio-n.
2. `Run` (emuladorean edo gailu fisikoan).
3. Konfiguratu zerbitzariaren host/port balioak aplikazioan (beharrezkoa bada).

### WPF (C#)
1. Ireki `MusRoyale_WPF` Visual Studio-n.
2. `Restore`/`Build` eta `Start`.
3. Konfiguratu host/port balioak (beharrezkoa bada).

### TCP Server (C#)
1. Ireki `MusServerTCP` Visual Studio-n edo erabil `dotnet` CLI.
2. Exekutatu zerbitzaria (`Start` edo `dotnet run`).
3. Ziurtatu portua irekita dagoela (Windows Firewall).

## Konfigurazioa (TODO)

- Zerbitzariaren portua: `TODO`
- Bezeroen host/port ezarpenak: `TODO`
- Mezuen formatua/protokoloa: `TODO`

## Garapen ingurunea

- Android Studio (Android)
- Visual Studio 2022+ (WPF + Server)
- .NET SDK (repoak eskatzen duen bertsioa)

## Lizentzia

`TODO` (adib. MIT)

## Egilea

- GitHub: **Mikel-67**
