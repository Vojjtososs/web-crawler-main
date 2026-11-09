# Úkol 5 - WebCrawler

[![UTB FAI Task](https://img.shields.io/badge/UTB_FAI-Task-yellow)](https://www.fai.utb.cz/)
[![Java](https://img.shields.io/badge/Java-007396.svg?logo=java&logoColor=white)](https://www.java.com/)
[![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?logo=gradle&logoColor=white)](https://gradle.org/)

|                                  |                                 |
| -------------------------------- | ------------------------------- |
| __Maximální počet bodů__         | 13 bodů                         |
| __Způsob komunikace s aplikací__ | Parametry předané při spuštění  |
| __Způsob testování aplikace__    | Na základě standardního výstupu |

---

## 📝 Zadání úkolu 

Naprogramujte web crawler pro prohledávání webu a identifikaci 20 nejčastěji se vyskytujících slov. Aplikaci bude možné ovládat přes příkazový řádek a umožní specifikovat parametry pro hloubku zanoření a úroveň ladění programu. Implementujte parsování HTML stránek, procházení odkazů na stránce a analýzu četnosti slov. 

### Požadavky:
* __Parsování HTML stránek:__ K implementaci tohoto úkolu využijte open source parser _JSoup_. Zvolte vhodnou verzi JSoup a přidejte do dependencies v app/build.gradle. 
* __Procházení odkazů:__ Program musí procházet všechny odkazy na stránce a ukládat je do seznamu nalezených odkazů. Musí také udržovat množinu všech navštívených stránek, aby se stejné stránky neanalyzovaly vícekrát.
* __Analýza četnosti slov:__ Aplikace bude provádět analýzu četnosti slov (pouze uživatelem čitelný text na webové stránce).
* __Ošetření kódování stránek:__ Získání kódování stránek z HTTP hlavičky nebo značky META. Aplikace musi zpravně zpracovávat ruzné kódování stránek.
* __Multithreading:__ Načítání a analýza každé stránky v nezávislém vláknu pomocí Executoru pro využití více jádrových procesorů. Seznam "visitedURIs" implementujte pomocí ConcurrentHashMap.

### Výstupu aplikace

Po spuštění aplikace prohledá web a po dokončení analýzy vypíše slova s ​​jejich absolutní četností seřazená sestupně na standardní výstup _System.out_. Výsledek analýzy (tabulka četnosti slov) se musí nacházet viditelně na konci výpisu v terminálu. __Aplikace musí vrátit nějaký výsledek, nesmí dojít k nečekanému pádu nebo nekonečnému prohledávání webu.__

```
the;47861
to;42777
and;37061
of;31670
your;29636
or;27962
you;22063
a;19607
data;19221
for;17302
information;14858
on;14341
our;12409
in;12381
is;11457
we;11143
with;10905
that;10462
as;9936
may;8774
```

### Struktura pole vstupních parametrů
1. __args[0]__ - URL stránky, na které program zahájí analýzu - např. http://yahoo.com _(String)_
2. __args[1]__ - Tento parametr bude udávat maximální hloubku zanoření při analýze. Tento parametr bude nepovinný. Pokud při spuštění aplikace nebude zadán, bude nastaven na 2. (Hloubka 0 znamená, že bude analyzován obsah jen stránky, na které je analýza zahájena).   _(int)_

__Základní kostra programu se nachází v tomto repozitáři. Vaším úkolem ji bude předělat tak, aby splňovala výše uvedené požadavky.__

>_**Poznámka:** Implementace a struktura kódu aplikace je libovolná a je zcela na vás, jak tento problém vyřešíte. Je však důležité, aby aplikace splňovala zadané požadavky._

###  Pro uživatele systému Linux:
Spusťte skript s názvem ```run_local_test.sh```.

### Pro uživatele systému Windows:
Spusťte skript s názvem ```run_local_test.bat```.


