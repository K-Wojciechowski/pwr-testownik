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

Baza to pliki tekstowe, kodowanie UTF-8 *bez BOM*, zakończenia linii LF (\n, Uniksowe). Notatnik się nie nadaje.

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
