# MusRoyalePC.Tests

Karpeta honek **MusRoyale** WPF bezeroaren test proiektua biltzen du (C#). Helburua aplikazioaren logika (ViewModel-ak, komandoak, zerbitzuak eta abar) egiaztatzea da, erregresioak saihesteko.

## Edukia

- Unit test klaseak (adib. `LoginTests`, `LoginFirebaseTests`, `RelayCommandTests`, `BikoteakViewModelTests`)
- Proiektu fitxategia: `MusRoyalePC.Tests.csproj`

## Zer test egiten dira (fitxategiz fitxategi)

### `BikoteakViewModelTests.cs`
`BikoteakViewModel`-aren portaera egiaztatzen du:
- **Hasierako balioak** zuzenak direla:
  - `ReyesSeleccionados == 4`
  - `BetAmount == "0"`
  - `IsFriendsPopupOpen == false`
  - `Amigos` zerrenda ez dela hutsa (seed zerrenda badago)
- **ToggleFriendsCommand** komandoak `IsFriendsPopupOpen` egoera toggle egiten duela (false→true→false)
- **SelectReyesCommand** komandoak parametroa "1" | "4" | "8" denean `ReyesSeleccionados` eguneratzen duela
- **SelectReyesCommand**-i `null` emanda, `ReyesSeleccionados` ez dela aldatzen
- **InviteFriendCommand** exekutatzean popup-a ixten dela (`IsFriendsPopupOpen == false`)
- Komandoak ez direla null:
  - `ToggleFriendsCommand`, `SelectReyesCommand`, `InviteFriendCommand`, `StartMatchCommand`

### `LoginTests.cs`
`LoginViewModel`-aren login logika egiaztatzen du **mock**-ekin (`IAuthService` + Moq):
- Erabiltzailea hutsik → login `false` eta `ErrorMessage == "Eremu guztiak bete"`
- Pasahitza hutsik → login `false` eta `ErrorMessage == "Eremu guztiak bete"`
- Kredentzial okerrak (`ValidateUserAsync == false`) → login `false` eta `ErrorMessage == "Kredentzial okerrak"`, eta ez du `IsAdminAsync` deitzen
- Erabiltzaile normala (`ValidateUserAsync == true`, `IsAdminAsync == false`) → login `true` eta `ErrorMessage` hutsik
- Admin (`ValidateUserAsync == true`, `IsAdminAsync == true`) → login `true`
- Exception/konexio errorea (`ValidateUserAsync`-k exception botatzen du) → login `false` eta `ErrorMessage == "Konexio errorea"`

### `LoginFirebaseTests.cs` *(Integration)*
`AuthService` erabiliz **Firebase/DB errealaren** aurka integrazio-testak egiten ditu (`Trait("Type","Integration")`):
- Admin seed erabiltzailearekin: `IsAdminAsync(...) == true`
- Erabiltzaile normalarekin: `ValidateUserAsync(...) == true` eta `IsAdminAsync(...) == false`
- Pasahitz okerrarekin: `ValidateUserAsync(...) == false`
- Existitzen ez den erabiltzailearekin: `ValidateUserAsync(...) == false`

### `RelayCommandTests.cs`
`RelayCommand` (ICommand helper) klasearen funtzionamendua egiaztatzen du:
- `execute == null` → `ArgumentNullException`
- `canExecute` predicate-rik ez badago → `CanExecute(...)` beti `true`
- Predicate-a badago → `CanExecute(...)`-k predicate-a errespetatzen du
- `Execute(param)`-ek parametroa action-era pasatzen du
- `RaiseCanExecuteChanged()`-ek `CanExecuteChanged` event-a jaurtitzen du

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