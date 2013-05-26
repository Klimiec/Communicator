

# Communicator Java SE [instant messenger]
============

Hi! 

I'm publishing source code of my instant messanger written in Java.
Below there is link to my youtube channel where you can see how it works.

Youtube : http://www.youtube.com/watch?v=A39XXzWsrTk




//---------------------------------------------------------------------------


# Java Instant Messenger : komunikator internetowy


Wstęp:
Projekt komunikatora sieciowego zbudowanego w technologi Java SE. 
Funkcjonalnością przypomina chatty internetowe gdzie możliwia jest komunikacja między 
poszczególnymi osobami (unicast) jak i wysyłanie/odbieranie wiadomości rozgłoszeniowych(broadcast)
Całość składa się z dwóch częsci: servera oraz clienta. Komunikacja między tymi dwoma obiektami 
została zrealizowana w oparciu o sockety oraz serializowany obiekt typu message który pełni rolę 
"pojemnika" dla krążących wiadomości na lini Klient - Server - Klient 

Wykorzystane technologie:
- Java SE (OOP, networking, Swing)
- Multithreading (threads, ExecutorService etc) 
- Wzorce: MVC, Observer, Memento

Opis: 
Server : Obsługuje przesyłanie wiadomosci pomiędzy użytkownikami. 
         Rejestruje oraz usuwa użytkowników chcących skorzystać z usługi. 
         Obsługa każdego nowego połaczenia realizowana jest przez osobny wątek. 
         Wątki servera obsługujące połączenie z klientem zarządzane są za pomocą ExecutorService. 

Client : Nadzoruje połączenie z serverem po stronie Klienta. 
         Interfejs użytkownika oparty został o Swing. 
         Istnieje możliwośc komunikacji ze wszystkimi bądź też 
         z wybraną osobą, zmiana statusu oraz powrót do wcześniej wysłanej wiadomości 
         dzięki opcji redo/undo zbudowanej w oparciu o wzorzec memento. 
         Wiadomości wysyłane do konkretnych osób zostają wyświetlane w nowym oknie.
         Całośc zaimplementowana została w oparciu o wzorzec MVC. 


Jest to dość rozbudowany projekt dlatego w celach demonstracyjnych przygotowałem prezentację video. 
Link (video): http://www.youtube.com/watch?v=A39XXzWsrTk 
Proszę włączyć odtwarzanie na cały ekran oraz przestawić jakośc video na HD 720p.

Projekt podzielony na 4 foldery które zawierają: 
Server : kod klasy servera oraz pomocniczej klasy typu message.
Client : Okno glowne programu, umożliwia wysyłanie wiadomości typu broadcast, 
         obserwowanie aktualnie przybywajacych uzytkownikow na serwerze 
         oraz zmienie statustu podczas pracy z programem. 
MessageBox : Okno obsługujące przesyłanie wiadomosci do konkretnych uzytkownikow. 
             Oprócz wysyłania i prezentacji wiadomosci możliwy jest też powrót do 
             wcześniej wysłanych wiadomosci dzięki mechanizmowi undo/redo. 
LoginBox : Okno pojawiające się podczas logowania do serwera. Umożliwia wybranie nicku 
           oraz statusu podczas logowania do serwera. Nazwa uzytkownika przed zalogowaniem 
           jest sprawdzana pod katem poprawności  


Uruchomienie:
Na początku należy włączyć server DeployServer.java który znajduje się w folderze aTesty.
Klasy testowe klientów znajdują się w folderze aTesty, pakiet com.piotrek.testy.clients.



//----------------------------------------------------------------------------


# Java Instant Messenger  

Introduction:
Project of instant messenger written in Java Standard Edition.
Its functionality resembles internet chat rooms where user can send messages to 
particular users (unicast) as well as sending and receiving broadcast messages.
Messenger consist of two parts: server and client. Communication between these two 
is based on sockets and serialized java object (Message) that acts 
like a placeholder for messages sent between client and server.

Technologies overview: 
 -Java SE (OOP, networking, Swing)
- Multithreading (threads, ExecutorService etc)
- Design Pattarns: MVC, Observer, Memento


Description:

Server - Register new clients, takes care of dispatching messages between users.
         Request from each new user is handled by new thread so that do not 
         influence responsivness of the server awaiting new user's requests. 
         ExecutorService manages clients threads which allow efficient resource management

Client - Visual part of the application based on Java Swing.
         Client can send two types of messages (broadcast, unicast), change status, 
         follow others currently being logged in and their statuses.


The project is quite large that's why I prepared video demonstrating its features.
Link(video) : http://www.youtube.com/watch?v=A39XXzWsrTk 
Be aware to change video quality to HD 720p and size to full screen.


Project is divide into folllowing folders:
Server : Contains source code of server and message class that support communication.

Client : Contains main program's window. Client window allows user to send broadcast messages to others, 
         changing status and keep track of users currently being logged in who can be 
         selected to receive private message.

MessageBox : Presentation layer of private messages sending between users.
             Besides sending and receiving private messages it implements 
             undo/redo mechanism that allows to retrive previously sent messages.
             It is based on memento design pattern.

LoginBox : Window that pop up while logging in. It allows user to choose nick and status.
           Nick is validated before being granted access to the server.


Setting up project:
First run server by choosing DeployServer.java class that there is in aTesty folder then 
select one of the client's test classes that there are in com.piotrek.testy.clients package, 
folder aTesty.