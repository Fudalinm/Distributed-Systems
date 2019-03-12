Celem zadania jest napisanie aplikacji w języku C lub C++ (lub python, ale uwaga na punktację!), która pozwoli użytkownikom na przesyłanie (nadawanie i wyświetlanie) informacji bez wykorzystania serwera centralnego poprzez logiczną symulację architektury token ring. Każdy klient podczas uruchomienia otrzymuje cztery argumenty:

tekstowy identyfikator użytkownika,
port na którym dany klient nasłuchuje,
adres IP i port sąsiada, do którego przekazywane będą wiadomości,
informacja o tym, czy dany użytkownik po uruchomieniu posiada token,
wybrany protokół: tcp lub udp.
Wiadomości przekazywane są tylko w jedną stronę. W sieci znajduje się tylko jeden token i żadna aplikacja nie może nadawać dopóki nie otrzyma wolnego tokenu, pierwotnie token jest wolny. Wysłanie wiadomości polega na zajęciu tokenu i wpisaniu jej zawartości. Token traktujemy jako kopertę, nośnik wiadomości. Odbiorca po przeczytaniu wiadomości zwalnia token (flaga, wyczyszczenie zawartości...) i przekazuje go dalej. Dla celów symulacyjnych przyjmujemy, że token jest przetrzymywany przez każdego klienta przez około 1 sekundę (po otrzymaniu tokenu wywołujemy np. sleep(1000), po tym czasie przesyłamy go dalej po ewentualnym dodaniu wiadomości). Dla uproszczenia zakładamy, że żaden klient nie będzie "złośliwy" i nie doprowadzi do sytuacji, w której w sieci znajdą się dwa tokeny - jednak za implementację mechanizmu, który to wyklucza, zostanie przyznany bonus punktowy. Program ma umożliwiać dodawanie nowych użytkowników w trakcie działania systemu oraz zapewniać dla nich pełną funkcjonalność, a także zabezpieczać przed sytuajcą, w której wiadomość krąży w nieskończoność w sieci (należy odpowiednio przemyśleć protokół komunikacyjny). Dodatkowo, każdy klient ma przesyłać multicastem informację o otrzymaniu tokenu (na dowolny adres grupowy, wspólny dla wszystkich klientów - może być wpisany "na sztywno" w kod). Odbiorcami grupy multicastowej są wyłącznie loggery - proste aplikacje zapisujące ID nadawcy i timestamp otrzymania tokenu, do pliku. Ilość loggerów może być dowolna (co najmniej 2). Logger należy zaimplementować w języku innym niż klientów.

Punktacja przedstawia się następująco:
    Implementacja klientów - wersja TCP: 2pkt
    Implementacja klientów - wersja UDP: 1pkt
    Mechanizm dołączania nowych klientów: 2pkt
    Implementacja loggera i mechanizmu logowania: 1 pkt
    Poprawna obsługa gniazd i protokołu TCP: 0,5pkt
    Poprawna obsługa gniazd i protokołu UDP: 0,5pkt
    Zagwarantowanie braku sytuacji zagłodzenia klientów: 1pkt


RUN:
g++ main.cpp -o main.exe -lws2_32

UDP:
	.\main.exe U1 1200 1201 127.0.0.1 UDP NO_TOKEN
	.\main.exe U2 1201 1202 127.0.0.1 UDP NO_TOKEN
	.\main.exe U3 1202 1200 127.0.0.1 UDP HAVE_TOKEN
	.\main.exe U4 1203 1202 127.0.0.1 UDP NO_TOKEN WANT_TO_JOIN


TCP:
	.\main.exe U1 1200 1201 127.0.0.1 TCP NO_TOKEN
	.\main.exe U2 1201 1202 127.0.0.1 TCP NO_TOKEN
	.\main.exe U3 1202 1200 127.0.0.1 TCP HAVE_TOKEN
	.\main.exe U4 1203 1202 127.0.0.1 TCP NO_TOKEN WANT_TO_JOIN