# Задача
Есть некий сервис, который отдает нам поток биржевых данных. Мы хотели бы раздавать этот поток другим внутренним клиентам, заодно агрегируя данные в формат, достаточный для отрисовки [candlestick chart](https://en.wikipedia.org/wiki/Candlestick_chart). Нужно реализовать сервер для этого на Scala или Java. Желательно использование фреймворков, облегчающих написание сетевых серверов (Netty, Mina, Akka IO).

Для тестирования можно использовать тестовый `upstream.py`, запустив его при помощи команды `python upstream.py`. После запуска он будет ждать соединений на `127.0.0.1:5555` и генерировать поток случайных сделок.

# Протокол (сервер/upstream)
Наш сервер соединяется к провайдеру данных по протоколу TCP/IP, после чего тот начинает присылать биржевые сделки в виде сообщений следующего формата:

`[ LEN:2 ] [ TIMESTAMP:8 ] [ TICKER_LEN:2 ] [ TICKER:TICKER_LEN ] [ PRICE:8 ] [ SIZE:4 ]`

где поля имеют следующую семантику:

* `LEN`: длина последующего сообщения (целое, 2 байта)
`TIMESTAMP`: дата и время события (целое, 8 байт, milliseconds since epoch)
* `TICKER_LEN`: длина биржевого тикера (целое, 2 байта)
* `TICKER`: биржевой тикер (ASCII, TICKER_LEN байт)
* `PRICE`: цена сделки (double, 8 байт)
* `SIZE`: объем сделки (целое, 4 байта)
# Протокол (клиент/сервер)
Клиенты подсоединяются к серверу также по TCP/IP. При подключении нового клиента сервер посылает ему минутные свечи за последние 10 минут, после чего в начале каждой новой минуты присылает свечи за предыдущую минуту. Данные сериализуются в JSON-сообщения следующего вида, разделенные символом перевода строки (\n):

`{ "ticker": "AAPL", "timestamp": "2016-01-01T15:02:00Z", "open": 112.1, "high": 115.2, "low": 110.0, "close": 114.2, "volume": 13000 }`
# Пример
Сообщения c upstream (в человекочитаемом формате):

2016-01-01 15:02:10 AAPL 101.1 200  
2016-01-01 15:02:15 AAPL 101.2 100  
2016-01-01 15:02:25 AAPL 101.3 300  
2016-01-01 15:02:35 MSFT 120.1 500  
2016-01-01 15:02:40 AAPL 101.0 700  
2016-01-01 15:03:10 AAPL 102.1 1000  
2016-01-01 15:03:11 MSFT 120.2 1000  
2016-01-01 15:03:30 AAPL 103.2 100  
2016-01-01 15:03:31 MSFT 120.0 700  
2016-01-01 15:04:21 AAPL 102.1 100  
2016-01-01 15:04:21 MSFT 102.1 200

Клиент подсоединяется в 15:04:04, получает свечи за последние 10 минут:

{ "ticker": "AAPL", "timestamp": "2016-01-01T15:02:00Z", "open": 101.1, "high": 101.3, "low": 101, "close": 101, "volume": 1300 }  
{ "ticker": "MSFT", "timestamp": "2016-01-01T15:02:00Z", "open": 120.1, "high": 120.1, "low": 120.1, "close": 120.1, "volume": 500 }  
{ "ticker": "AAPL", "timestamp": "2016-01-01T15:03:00Z", "open": 102.1, "high": 103.2, "low": 102.1, "close": 103.2, "volume": 1100 }  
{ "ticker": "MSFT", "timestamp": "2016-01-01T15:03:00Z", "open": 120.2, "high": 120.2, "low": 120, "close": 120, "volume": 1700 }  

В 15:05:00 клиент получает свечи за предыдущую минуту:

{ "ticker": "AAPL", "timestamp": "2016-01-01T15:04:00Z", "open": 102.1, "high": 102.1, "low": 102.1, "close": 102.1, "volume": 100 }  
{ "ticker": "MSFT", "timestamp": "2016-01-01T15:04:00Z", "open": 120.1, "high": 120.1, "low": 120.1, "close": 120.1, "volume": 200 }