# MusRoyale Android App

Karpeta honek **MusRoyale** proiektuaren **Android bezeroa** biltzen du (Kotlin). Aplikazio honek jokalariaren interfazea eskaintzen du eta **TCP zerbitzariarekin** konektatzen da partida bat sortu/elkartu eta jokatzeko.

## Nola abiarazi (Android)

### Aukera 1: Android Studio (gomendatua)
1. Ireki Android Studio.
2. Ireki proiektua:
   - `MusRoyale_AndroidApp/MusRoyaleAndroid/`
3. Itxaron **Gradle Sync** amaitu arte.
4. Aukeratu gailua (emuladorea edo gailu fisikoa).
5. Sakatu **Run** (▶).

### Aukera 2: Gradle CLI (terminaletik)
```bash
cd MusRoyale_AndroidApp/MusRoyaleAndroid
./gradlew assembleDebug
```

(Windows-en: `gradlew.bat assembleDebug`.)

## Konexio konfigurazioa (host / port)

Aplikazioak TCP zerbitzarira konektatzeko **host** eta **port** balioak behar ditu:

- **Portua (zerbitzarian):** `13000`
- **Host-a:** zerbitzaria exekutatzen ari den makinaren IP/hostname-a

### Android Emulator oharra
Zerbitzaria zure PC-an badago eta aplikazioa **emuladorean** exekutatzen baduzu:
- erabili **`10.0.2.2`** host gisa (PC-ko localhost-era heltzeko).
