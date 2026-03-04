# MusRoyale WPF (Windows bezeroa)

Karpeta honek **MusRoyale** proiektuaren Windows bezeroa biltzen du, **WPF (C#)** erabiliz egina. Aplikazio honek erabiltzaileari mahaigaineko interfaze bat eskaintzen dio, eta TCP zerbitzariarekin konektatuta partidetan sartu/sortu eta jokatzeko erabiltzen da.

## Repo egitura (WPF)

- `MusRoyalePC/` — WPF aplikazio nagusia (soluzioa eta proiektua)
- `MusRoyalePC.Tests/` — Test proiektua

## Nola abiarazi (WPF)

### Aukera 1: Visual Studio (gomendatua)
1. Ireki soluzioa: `MusRoyale_WPF/MusRoyalePC/MusRoyalePC.sln`
2. Aukeratu proiektu nagusia: `MusRoyalePC`
3. `Restore`/`Build` egin.
4. Exekutatu (**Start**).

### Aukera 2: MSBuild / dotnet (baldin badago bateragarria)
Proiektua .NET Framework-koa bada, ohikoena Visual Studio erabiltzea da. `.csproj`-aren target frameworkaren arabera, `dotnet build` funtzionatu dezake.

## Konexio konfigurazioa (host / port)

Aplikazioak TCP zerbitzarira konektatzeko **host** eta **port** balioak behar ditu.

- **Portua (zerbitzarian):** `13000`
- **Host-a:** zerbitzaria exekutatzen ari den makinaren IP/hostname-a

## Barrutik (labur): proiektuaren atal nagusiak

WPF aplikazioa normalean geruza hauetan antolatzen da:

- `Views/` — XAML pantailak (UI)
- `Controllers/` edo `Services/` — negozio-logika / sare komunikazioa
- `Models/` — datu egiturak (jokalaria, partida, kartak…)
- `Infrastructure/` — utilitateak, komandoak, helper-ak

> Oharra: proiektu honen sarrera puntua `App.xaml` / `App.xaml.cs` da, eta leiho nagusia `MainWindow.xaml`.