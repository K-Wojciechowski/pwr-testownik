Testownik PWr
=============

Licencja: MIT  
Wykorzystano klasę StretchIcon autorstwa Darryla Burke

Gra z klawiatury
----------------

Klawisze `a-i` wybierają opcję oznaczoną wskazaną literą. Klawisze `1-9`
wybierają opcję zgodnie z kolejnością wyświetlania na ekranie. Spacja sprawdza
odpowiedź (wybór wielokrotny) lub przechodzi do kolejnego pytania.

Edytowanie bazy
---------------

### Format standardowy

Baza to pliki tekstowe, kodowanie UTF-8 (najlepiej bez BOM), zakończenia linii LF (\n, Uniksowe). Notatnik w Windows 10 v2004+ może sobie dać radę, ale bywa różnie.

Pierwsza linia: znaki `QQ`, potem liczby `1`/`0` (prawda/fałsz). Ewentualne opcje po średniku (np. `img=123.png` albo `noshuffle`)  
Druga linia: treść pytania. Numer oddzielony kropką i tabulatorem.  
Kolejne linie: tabulator, potem litera i odpowiedź.

Przykład:

    QQ0100
    1.	Odpowiedź na wielkie pytanie o życie, wszechświat i całą resztę brzmi:
    	(a) 7
    	(b) 42
    	(c) koala
    	(d) półprzewodnik p-n-p

Toolset do masowej edycji pytań w katalogu `TOOLSET`. Przykładowe pytanie: `001.txt`.

Informacja o bazie w pliku `splash.nfo` (pierwsza linia = tytuł bazy)

### Starsze formaty

Pewne starsze formaty są wspierane przez aplikację. Pytania w starszych
formatach mają linię odpowiedzi zaczynającą się od `X` (np. `X0100`), opcje nie
są wspierane. Pytanie może mieć numer (z kropką albo nawiasem okrągłym
zamykającym). Odpowiedzi mogą mieć literkę (w formacie: `(a)`, `a)`, `a.`, duże
lub małe), kreskę i spację (`- `) albo nic. Wspierane jest kodowanie
Windows-1250 (z polskiego Notatnika), UTF-8 z/bez BOM, LF lub CR-LF.

Przykład:

    X0100
    1) Odpowiedź na wielkie pytanie o życie, wszechświat i całą resztę brzmi:
    (a) 7
    (B) 42
    c) koala
    d.   półprzewodnik p-n-p
