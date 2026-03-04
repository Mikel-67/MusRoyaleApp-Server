# MusRoyalePC.Tests

Karpeta honek **MusRoyale** WPF bezeroaren test proiektua biltzen du (C#). Helburua aplikazioaren logika (ViewModel-ak, komandoak, zerbitzuak eta abar) egiaztatzea da, erregresioak saihesteko.

## Edukia

- Unit test klaseak (adib. `LoginTests`, `LoginFirebaseTests`, `RelayCommandTests`, `BikoteakViewModelTests`)
- Proiektu fitxategia: `MusRoyalePC.Tests.csproj`

## Nola exekutatu testak

### Visual Studio
1. Ireki soluzioa: `MusRoyale_WPF/MusRoyalePC/MusRoyalePC.sln`
2. **Test Explorer** ireki.
3. **Run All**.

### CLI (aukera)
Testak komando lerrotik exekutatzeko (inguruneak onartzen badu):
- `dotnet test MusRoyale_WPF/MusRoyalePC.Tests/MusRoyalePC.Tests.csproj`

## Oharra

Testen egitura eta dependentziak `.csproj` fitxategian definitzen dira. Test bat gehitzean, gomendagarria da izendapen argia erabiltzea eta Arrange/Act/Assert patroia jarraitzea.