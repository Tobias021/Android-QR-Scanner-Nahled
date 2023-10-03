# __CHANGELOG__

## TODO
- [ ] ProductInfoTableControl - zjednodušit where pro vypisování jednotlivých prvků. Aktuálně řešeno pomocí 3 podmínek (row > 1, row == 0, row == 1).
- [ ] Pro pozadí aktivit vynutit bílé pozadí, nebo změnit "Dark" pozadí na šedé.
## DEV[0.8.4.1] 1.2.22
Oprava chyb verze 0.8.4
### OPRAVENO
Aplikace lze již nainstalovat a lze ji spustit. Chyba byla způsobena nastavením parametru android:exported na "false". Podrobnosti viz chyba #5.

## DEV[0.8.4]
Bugfixy, změna fungování části kódu v MenuActivity.
## OPRAVENO
Opraveny chyby #3 a #4 (bližší info v chybách).

## DEV[0.8.3] 27.7.21

### PŘIDÁNO
- Pro String db_user v strings.xml přidán tag tools:ignore="Typos".
### ZMĚNĚNO
- Vymazány třídy: ExpedovatPaletuActivity.j, PrijemPolotovaruActivity.j, PrijemVakuActivity.j,SiciDilnaActivity.j, VydejKompletaceActivity.java, VylohaActivity.java.
- Všechny třídy jsou nyní vedeny v jazyce Kotlin.
- Pro funkci updateButtonLayout() ve třídě MenuActivity je nyní vytvořeno vlastní vlákno (pomocí MainScope, scope.launch a withContext(Dispatchers.IO)). UI může běžet nezávisle na synchronizaci dat pro tlačítka. Všechny dotazy na prvky UI v novém vlákně musí být volány pomocí funkce runOnUiThread {}.
- Upraveny komentáře tlačítek. Nyní je udána pozice místo třídy.
- Ve třídě LocalIntents upraven blok when{} na return when{}.
- Pro ProgressBar "kolecko" nastaveny rozměry layoutu height/width na 80dp.
- V AndroidManifest.xml vymazány staré aktivity.

### OPRAVENO
- ProgressBar "kolecko" se nyní při spuštění zobrazí. Opraveno vytvořením vlákna pro datovou synchronizaci.
- Kontext v layoutu activity_webactivity.xml opraven na .WebActivity (předtím .SiciDilnaActivity).
- Opraveno barevné schéma a názvy barev.
- V souboru themes.xml ve values-night opraveny názvy barev
## DEV[0.8.2] 26.7.21
Aplikace je stabilní, vylepšeny třídy ProductInfo a xml soubor pro hlavní třídu ProductInfo. Třídy pro jednotlivá tlačítka jsou zastaralé, v aplikaci se již nepoužívají. 

### PŘIDÁNO
- Ve třídě ProductInfoTableControl přibyla podmínka pro hodnotu "row" (přijatou z databáze). Když je hodnota rovna nule, vypíšou se pouze texty. Když je hodnota rovna 1, provede se výpis jména, obrázku a dalších textů. 
- V layoutu activity_product_info_main byl přidán separátor mezi StatusBar a LinearLayout (tabulka). 
### ZMĚNĚNO
- Tlačítka při vytvoření získavají onClickListener s jejich intentem. Není již třeba samostatná třída pro každé tlačítko. Aktivita WebActivity získává URL k zobrazení pomocí extra (putExtra, getCharSequenceExtra().toString()). 
- SqlGoblin třída byla převedena do Kotlinu.
- MenuActivity třída byla převedena do Kotlinu
- funkce resolve() třídy LocalIntents nyní vrací intent, který se spustí přímo v kódu. Předcházím tím NullPointer chybě.
- Aktivity v AndroidManifest.xml dostaly rozhraní @style/Theme.Velveta.NoActionBar. V aktivitách se nezobrazuje název aplikace.
### OPRAVENO
- Názvy položek v aktivitě ProductInfoMain se ořezávají pomocí fce trim() - stringy z databáze se vracejí často dlouhé (s mezerami) a v TextView se ani nezobrazí. Funkce trim() nepotřebné mezery odstraní (ze začátku a konce).
- Při spuštění WebActivity neměl WebView focus. Při načtení kódu se spustil Webový prohlížeč. Přidána funkce requestFocus().


## [0.8.1.1] 9.7.21
Opraveny četné chyby v kódu, otestována implementovaná část kódu, opravy drobných chyb

### ZMĚNĚNO
- Změněna velikost fontu v aktivitě ProductInfo na 30.0 pro název a 25.0 pro další text
- Mezi texty 1 a 2 v aktivitě ProductInfo se nově zobrazuje mezera pro lepší přehlednost
### OPRAVENO
- Opraven intent ve třídě LocalIntents (chybná syntaxe Kotlinu)
- Opraveny četné Null-pointer chyby pomocí odkazů na kontext (this v Javě)
- Opravena chyba v zobrazovaném nadpisu (name) v aktivitě ProductInfo - Databázela vrací string s mnoha mezerami za textem - použita metoda trim()

### POZNÁMKY
- Aktivita ProductInfo - Aktivita lze spouštět pouze z 1. tlačítka. Ostatní nejsou přizpůsobena. Je třeba předělat funkčnost tlačítek - po stisknutí se spustí třída, která rozhodne, zda bude použito lokální, nebo webové rozhraní. Podle toho se dále spustí potřebné intenty.
- Aktivita ProductInfo aktuálně funguje tak, že se spustí aktivita pro tlačítko (default. webové rozhraní) a z této aktivity se pomocí intentu spouští další aktivita. Je třeba to upravit, nefunkční!

## [0.8.1] 9.7.21
První nahraná verze aplikace na GitHub. Aktuálně podporuje webové (java) a lokální aplikační rozhraní; to je nově psáno v kotlinu. V budoucích verzích je třeba vylepšit funkci jednotlivých tříd pro tlačítka. Možná implementace interfejsů?

### PŘIDÁNO
- Implementováno aplikační okno "product-info" (Kotlin)  
### ZMĚNĚNO
- Každé tlačítko nyní ověřuje zadanou URL - když nenajde "http", předá URL třídě LocalIntents, která spustí příslušné okno dle URL.
### OPRAVENO
- opravena chyba status baru - při stisknutí hardwarového tlačítka nemizí zprávy ze status baru.
