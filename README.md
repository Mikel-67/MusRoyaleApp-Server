# MusRoyale

**MusRoyale** mus jokoaren inguruko sistema/aplikazio multzo bat da. Proiektuaren helburua da mus partida bat linean jokatzeko (edo kudeatzeko) aukera ematea, bezero desberdinen bidez eta zerbitzari zentral baten laguntzaz.

Proiektua **3 hilabeteko periodo** batean garatu da, eta **3 ikaslek** osorik egindako lana da (bezeroak zein zerbitzaria barne).

Sistema hau **3 proiektu nagusitan** banatuta dago:

- **Android aplikazioa** (Kotlin): jokalarientzako bezero mugikorra.
- **WPF aplikazioa** (C#): Windows mahaigaineko bezeroa.
- **TCP zerbitzaria** (C#): logika nagusia, partida egoeraren kudeaketa eta bezeroen arteko komunikazioa.

## Proiektuaren ideia orokorra

Bezeroek (Android eta WPF) zerbitzariarekin konektatzen dira, eta zerbitzariak saioak/partidak koordinatzen ditu: jokalariak identifikatu, partida egoera mantendu, eta bezeroen artean mezuak trukatzeko kanal komun bat eskaini.

README hau nahita **sarrerakoa** da: proiektuaren ikuspegi orokorra eta repoaren egitura azaltzea du helburu. (Instalazio edo exekuzio pausoak ez dira hemen sartzen.)

## Repo egitura

- `MusRoyale_AndroidApp/` — Android bezeroa (Kotlin)
- `MusRoyale_WPF/` — WPF bezeroa (C#)
- `MusServerTCP/` — TCP zerbitzaria (C#)
