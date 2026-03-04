# 🃏 Mus Royale - C# WPF Edizioa

![Build Status](https://img.shields.io/badge/build-passing-brightgreen?style=for-the-badge)
![Platform](https://img.shields.io/badge/platform-Windows-0078d7?style=for-the-badge&logo=windows)
![Framework](https://img.shields.io/badge/.NET-WPF-512bd4?style=for-the-badge&logo=dotnet)
![License](https://img.shields.io/badge/license-MIT-green?style=for-the-badge)

**Mus Royale** Windows-erako joko plataforma konpetitiboa da, **Musa** —euskal kulturako kartajokorik estrategiko eta herrikoi lortuena— zintzotasun osoz birsortzen duena. Osoki **C# eta WPF** bidez garatua, proiektuak interfaze moderno bat eta bezero-zerbitzari arkitektura sendo bat uztartzen ditu.

---

## 🚀 Sistemaren Ezaugarriak

### 🔐 Segurtasuna eta Erabiltzaileen Kudeaketa
Sistemak jokalarien datuen osotasuna lehenesten du, autentifikazio-fluxu profesional baten bidez:
* **Erregistroa eta Logina:** Interfaze intuitiboa, balioztatzeekin denbora errealean.
* **Hasheatutako Pasahitzak:** Banku-mailako segurtasuna. Pasahitzak **hashing** algoritmo baten bidez prozesatzen dira gorde aurretik; horrela, inoiz ez dira testu planean gordetzen.
* **Profilaren Kudeaketa:** Panel pertsonalizatua, non erabiltzaileak bere informazioa eta avatarra alda ditzakeen, eta joko-estatistikak kontsultatu.

### 👥 Geruza Soziala eta Networking-a
* **Lagun-sistema:** Erabiltzaile-bilatzaile integratua. Lagun-eskaerak bidali, onartu edo ezaba ditzakezu.
* **Txata Denbora Errealean:** Komunikazio arina lagunekin mezu pribatuen bidez, partida hasi aurretik estrategiak koordinatzeko.
* **Konexio Egoera:** Ikusi nor dagoen online eta jokatzeko prest.

### 🎮 Joko Modu Dinamikoak
1.  **Partida Azkarra (Matchmaking):** Algoritmo batek jokalari erabilgarriak bilatzen ditu ausaz, berehala jokatzen hasteko.
2.  **Duo Modua:** Gonbidatu lagun espezifiko bat zure zerrendatik. Sistemak gonbidatuak onartu eta biek baieztatu arte itxaroten du, mahai berean parekatzeko.
3.  **Partida Pribatua (Kode Sistema):**
    * **Liderrak** gela bat sortzen du eta **kode bakar** bat sortzen du.
    * Gonbidatuek kode hori idazten dute gela pribatura sartzeko. Ideala tokiko txapelketetarako edo talde itxietarako.

---

## 🎴 Jokoaren Arauak: Musa Mus Royale-n

Partida 4 jokalariren artean garatzen da (bikoteka), bante (aldi) klasikoak jarraituz:

1.  **Musa:** Hasierako fasea, non jokalariek eskuan dituzten kartak aldatu nahi dituzten erabakitzen duten. Denak ados badira, kartak baztertu eta berriak hartzen dira.
2.  **Handia:** Karta-balio handiena nork duen jokatzen da.
3.  **Txikia:** Karta-balio txikiena nork duen jokatzen da.
4.  **Pareak:** Kartekin bikoteak dituzten egiaztatzen da (Pareak, Erdikoak edo Dupleak).
5.  **Jokoa:** 31 puntu edo gehiago batzeko lehia. Inor iristen ez bada, "Puntura" jokatzen da.

*Mus Royale-ko sistemak harrien (puntuak) zenbaketa automatizatzen du eta jokaldi irabazleak berehala hautematen ditu.*



