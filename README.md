# hl-sonet-core 
#### Highloaded Social Network Core

## Запускаем проект через докер
- скачиваем исходники git checkout
- заходим в корень проекта
- запускаем docker-compose up
- докер скачивает образы Postgres, Maven, Java-17, билдит артефакт Springboot приложения и запускает его на http://localhost:8080. Сервис вебсокетов запускается на http://localhost:8082.

## Использование приложения
**Для получений уведомлений по вебсокету создано две html страницы. ОНИ пустые, в них только JS код. Можно присоединиться разными пользователями, и смотреть обновление ленты в JS консоли браузера** <br/>
**http://localhost:8082/index.html?uuid=<uuid>&token=<JWT>** <br/>
**http://localhost:8082/index2.html?uuid=<uuid>&token=<JWT>** <br/>
**JWT возвращается при логине, его так прямо и нужно скопипастить в параметр урла страницы** <br/>
**Подписаться можно только на свой канал, при попытках подписаться на чужой получите ошибку, вебсокет закроется** 

- регистрация: POST http://localhost:8080/user/update - достаточно email, firstname, password
- логин: POST http://localhost:8080/user/login - выдает JWT token
- изменение профайла [Basic/JWT] POST http://localhost:8080/user/update - тут можно добавить/поменять свой профиль. Необходима авторизация Basic или по JWT токену, подключены оба типа авторизации.
- просмотр анкет зарегистрированных пользователей GET http://localhost:8080/user/get/{uuid}
- добавить друга [Basic/JWT] PUT http://localhost:8080/friend/set/<friend-UUID>  
- добавить пост [Basic/JWT] PUT http://localhost:8080/post/create/ 
- просмотр ленты [Basic/JWT] GET http://localhost:8080/post/feed
- отправить сообщению пользователю [Basic/JWT] POST http://localhost:8080/dialog/<user-UUID>/send
- посмотреть диалог с пользователем [Basic/JWT] POST http://localhost:8080/dialog/<user-UUID>/get


#### Swagger UI приложения 
Пока что не поддерживает аутентификацию, доступен по адресу<br/>
http://localhost:8080/swagger-ui/index.html

#### **Пояснения по поводу имплементации**
Посты попадают в очередь событий сделанную на основе Redis Pub-Sub <br/>
Вебсокеты на основе SockJS и Stomp протокола. Серверная часть - SpringBoot<br/>
**Для вебсокетов создан отдельный микросервис. Подъем новыъ инстансов позволит масштабировать количество одновременно обслуживаемых вебсокетов.**<br/>
<br/>
Пример страницы: <br/>
http://localhost:8082/?uuid=884dfc75-bf94-4a44-bf4b-977ba1bc7d4b&token=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJnZW5lcmF0ZWQxQHRlc3QucnUiLCJyb2xlIjoiVVNFUiIsInV1aWQiOiI4ODRkZmM3NS1iZjk0LTRhNDQtYmY0Yi05NzdiYTFiYzdkNGIiLCJpYXQiOjE2OTQwMjkwNzYsImV4cCI6MTY5NDEyOTU3Nn0.79JiVAVNuoQGGcXxP0INtJSkJyvWEuxNmRs4GjNMrKg
