# ДЗ 12 Мониторинг

#### Highloaded Social Network

## Запускаем проект через докер
- скачиваем исходники git checkout
- заходим в корень проекта
- запускаем docker-compose up
- докер скачивает образы Postgres, Maven, Java-17, билдит артефакт Springboot приложения и запускает его на http://localhost:8080. Сервис вебсокетов запускается на http://localhost:8082.
- для наблюдения мониторинга в графану `http://localhost:3000` нужно импортировать дашборд `./hl-sonet-hw-9.postman.json`

## Описание проделанной работы
1. Добавлены grafana, prometheus
2. Подготовлены метрики по принципу RED. Отдельно для каждого урла. Ниже примеры prometheus queries для сбора метрик
- **RPC**
- `sum(rate(http_server_requests_seconds_count{uri!~"/actuator/prometheus"}[1m]))` - общая
- `sum(rate(http_server_requests_seconds_count{uri!~"/actuator/prometheus"}[1m])) by (method, uri, instance)` - с разбивкой по эндпоинтам и сервисам
- **Errors** - отношение ошибочных к успешным запросам
- `sum(increase(http_server_requests_seconds_count{uri!="/actuator/prometheus", status=~"4.+|5.+"}[1m])) / sum(increase(http_server_requests_seconds_count{uri!="/actuator/prometheus"}[1m]))`
- **Duration** - cделаны для каждого эндпоинта индивидуально, ниже пример для одного эндпоинта. Можно конечно не разбивать по uri, но я не вижу смысла в такой диаграмме
- `rate(http_server_requests_seconds_sum{uri="/dialog/{toUuid}/get"}[1m]) / rate(http_server_requests_seconds_count{uri="/dialog/{toUuid}/get"}[1m])`
3. Созданный дашбоард можно найти в ***./hw-12_report/red.dashboard.json***
4. Проведено нагрузочное тестирование при котором некоторые запросы гарантировано ошибочны (падает авторизация). Использовалась postman коллекция запросов `./hl-sonet-hw-9.postman.json` Графики RED дашбоарда представлены ниже:
![img.png](img.png)
![img_1.png](img_1.png)
![img_2.png](img_2.png)
![img_3.png](img_3.png)
5. К сожалению не могу показать Zabbix. JMX порт выставлен в java в java приложении в докере. JMX мониторинг сирвиса диалогов нормально работает в любом другом приложении к примеру JConsole или Java Mission Control. Но в zabbix успеха я не добился.


## Использование приложения
- отправить сообщению пользователю [Basic/JWT] POST http://localhost:8080/dialog/<user-UUID>/send
- отправить сообщению пользователю [Basic/JWT] POST http://localhost:8083/api/v1/dialog/<user-UUID>/send
- отправить сообщению пользователю [Basic/JWT] POST http://localhost:8080/dialog/<user-UUID>/send
  <br/><br/>
- посмотреть диалог с пользователем [Basic/JWT] http://localhost:8083/api/v2/dialog/<user-UUID>/get
- посмотреть диалог с пользователем [Basic/JWT] POST http://localhost:8083/api/v1/dialog/<user-UUID>/get
- посмотреть диалог с пользователем [Basic/JWT] POST http://localhost:8083/api/v2/dialog/<user-UUID>/get
  <br/><br/>
- регистрация: POST http://localhost:8080/user/update - достаточно email, firstname, password
- логин: POST http://localhost:8080/user/login - выдает JWT token
- изменение профайла [Basic/JWT] POST http://localhost:8080/user/update - тут можно добавить/поменять свой профиль. Необходима авторизация Basic или по JWT токену, подключены оба типа авторизации.
- просмотр анкет зарегистрированных пользователей GET http://localhost:8080/user/get/{uuid}
- добавить друга [Basic/JWT] PUT http://localhost:8080/friend/set/<friend-UUID>  
- добавить пост [Basic/JWT] PUT http://localhost:8080/post/create/ 
- просмотр ленты [Basic/JWT] GET http://localhost:8080/post/feed
- Для получений уведомлений по вебсокету создано две html страницы. ОНИ пустые, в них только JS код. Можно присоединиться разными пользователями, и смотреть обновление ленты в JS консоли браузера <br/>
- http://localhost:8082/index.html?uuid=<uuid>&token=<JWT> <br/>
- http://localhost:8082/index2.html?uuid=<uuid>&token=<JWT> <br/>
- JWT возвращается при логине, его так прямо и нужно скопипастить в параметр урла страницы <br/>
- Подписаться можно только на свой канал, при попытках подписаться на чужой получите ошибку, вебсокет закроется<br/>

#### Grafana
- **http://localhost:3000**

#### Prometheus
- **http://localhost:9090**

#### Swagger UI приложения 
Пока что не поддерживает аутентификацию, доступен по адресу<br/>
- **http://localhost:8080/swagger-ui/index.html**
- **http://localhost:8083/swagger-ui/index.html**
- **http://localhost:8082/swagger-ui/index.html** - тут только /actuator эндпоинты

#### Redis Comander
**http://localhost:8081/** <br/>
Логин / пароль : root / qwerty

#### **Пояснения по поводу имплементации**
Посты попадают в очередь событий сделанную на основе Redis Pub-Sub <br/>
Вебсокеты на основе SockJS и Stomp протокола. Серверная часть - SpringBoot<br/>
Для вебсокетов и диалогов созданы отдельные микросервисы. <br/>
<br/>
Пример страницы для прослушивания вебсокета (нужно открыть консоль): <br/>
http://localhost:8082/?uuid=884dfc75-bf94-4a44-bf4b-977ba1bc7d4b&token=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJnZW5lcmF0ZWQxQHRlc3QucnUiLCJyb2xlIjoiVVNFUiIsInV1aWQiOiI4ODRkZmM3NS1iZjk0LTRhNDQtYmY0Yi05NzdiYTFiYzdkNGIiLCJpYXQiOjE2OTQwMjkwNzYsImV4cCI6MTY5NDEyOTU3Nn0.79JiVAVNuoQGGcXxP0INtJSkJyvWEuxNmRs4GjNMrKg



- https://github.com/mjp91/zabbix-spring-boot-actuator
