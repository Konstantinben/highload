# ДЗ 9 Разделение монолита на микросервисы

#### Highloaded Social Network

## Запускаем проект через докер
- скачиваем исходники git checkout
- заходим в корень проекта
- запускаем docker-compose up
- докер скачивает образы Postgres, Maven, Java-17, билдит артефакт Springboot приложения и запускает его на http://localhost:8080. Сервис вебсокетов запускается на http://localhost:8082.

## Описание изменений
1. Выделен отдельный микросервис для диалогов, его API: <br/>
- **http://localhost:8083/api/v1/dialog**  - этот эндпоинт для обработки запросов со старого АПИ, которые досутпны по старому адресу **http://localhost:8080/dialog**
- **http://localhost:8083/api/v2/dialog** - это новое API
2. Общение между микросервисами безопасно и организовано на REST. Отдельный Auth service не выделял (хотя он уже нужен), но сделал Auth эндпоинт, на который может ходить только администратор, занесенный в БД. Межсервисное общение осуществляется секьюрно с JWT токеном администратора.
3. Базы микросервисов разделены
4. Для сервисов core и dialog добавлены максимально подробные логи Input/Output, содержащие traceId. На входе/выхода REST эндпоинтов и REST клиентов логируется запрос и отправляемые данные. Такие подробные и тяжелые логи включены для иллюстрации. Аггрегаторов логов или трейсов не добавлял, поскольку это не требовалось в задании.


## Использование приложения
- **отправить сообщению пользователю [Basic/JWT] POST http://localhost:8080/dialog/<user-UUID>/send**
- **отправить сообщению пользователю [Basic/JWT] POST http://localhost:8083/api/v1/dialog/<user-UUID>/send**
- **отправить сообщению пользователю [Basic/JWT] POST http://localhost:8080/dialog/<user-UUID>/send**
  <br/><br/>
- **посмотреть диалог с пользователем [Basic/JWT] http://localhost:8083/api/v2/dialog/<user-UUID>/get**
- **посмотреть диалог с пользователем [Basic/JWT] POST http://localhost:8083/api/v1/dialog/<user-UUID>/get**
- **посмотреть диалог с пользователем [Basic/JWT] POST http://localhost:8083/api/v2/dialog/<user-UUID>/get**
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


#### Swagger UI приложения 
Пока что не поддерживает аутентификацию, доступен по адресу<br/>
- **http://localhost:8080/swagger-ui/index.html**
- **http://localhost:8083/swagger-ui/index.html**

#### Redis Comander
http://localhost:8081/ <br/>
Логин / пароль : root / qwerty

#### **Пояснения по поводу имплементации**
Посты попадают в очередь событий сделанную на основе Redis Pub-Sub <br/>
Вебсокеты на основе SockJS и Stomp протокола. Серверная часть - SpringBoot<br/>
Для вебсокетов создан отдельный микросервис. Подъем новыъ инстансов позволит масштабировать количество одновременно обслуживаемых вебсокетов.<br/>
<br/>
Пример страницы: <br/>
http://localhost:8082/?uuid=884dfc75-bf94-4a44-bf4b-977ba1bc7d4b&token=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJnZW5lcmF0ZWQxQHRlc3QucnUiLCJyb2xlIjoiVVNFUiIsInV1aWQiOiI4ODRkZmM3NS1iZjk0LTRhNDQtYmY0Yi05NzdiYTFiYzdkNGIiLCJpYXQiOjE2OTQwMjkwNzYsImV4cCI6MTY5NDEyOTU3Nn0.79JiVAVNuoQGGcXxP0INtJSkJyvWEuxNmRs4GjNMrKg
