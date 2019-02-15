Music Player

Wykorzystane technologie:<br/>
Java<br/>
JavaFX<br/>
PostgresSQL<br/>
CSS<br/>

<strong>Funkcjonalności aplikacji:</strong><br/>
-odtwarzanie muzyki<br/>
-segregowanie utworów według gatunku lub nastroju<br/>
-wyszukiwanie utworów według albumu, artysty lub nazwy<br/>
-wyszukiwanie tekstu z strony azlyrics.com<br/>
-możliwość automatycznego połączenia z YouTube i wyświetlenie tam danej piosenki<br/>
<br/>

Funkcjonalności których nie udało się na czas zrobić, ale pojawią sie wkrótce:<br/>
-tworzenie własnych playlist<br/>
-usuwanie piosenek<br/>
-wyswietlanie wideo z Youtube w oknie aplikacji<br/>

UML bazy danych piosenek:
![Image description](https://github.com/DanielVeB/Player/blob/master/src/main/resources/baza/playerbaza.png)

Wykorzystane wzorce:
MVC
Observer -podświetlanie w tabeli danej piosenki 
Builder - w klasie Song


Screenshoty z aplikacji:<br/>
Strona główna aplikacji
![Image description](https://github.com/DanielVeB/Player/blob/master/src/main/resources/screenshots/1.png)
Wyświetlanie albumów. Tak samo wygląda wyświetlanie po artystach.
![Image description](https://github.com/DanielVeB/Player/blob/master/src/main/resources/screenshots/2.png)
Przykład wyświetlania piosenek według albumu. W planach jest dodanie obok nazwy albumy buttona będącym odnośnikiem do sklepu empik, do strony z tej albumem aby można było zamówić album.
![Image description](https://github.com/DanielVeB/Player/blob/master/src/main/resources/screenshots/3.png)

Przykład edycji piosenki. Możemy przypisać piosence jakie genres i moods do niej pasują, a także wyszukać i wyświetlać tekst.
Po znalezieniu tekstu zapisujemy je w bazie danych, i na następny raz mamy już tekst bez konieczności ponownego wyszukiwania.
![Image description](https://github.com/DanielVeB/Player/blob/master/src/main/resources/screenshots/4.png)

