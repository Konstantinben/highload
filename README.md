# ДЗ 8 Применение tarantool'а как хранилища

#### Highloaded Social Network

## Запускаем проект через докер
- скачиваем исходники git checkout
- заходим в корень проекта
- запускаем docker-compose up
- докер скачивает образы Postgres, Maven, Java-17, билдит артефакт Springboot приложения и запускает его на http://localhost:8080. Сервис вебсокетов запускается на http://localhost:8082.
- заходим в контейнер tarantool-simple и создаем схему БД и хранимые процедуры см. ```/opt/tarantool/scripts.lua ``` (```./tartantool/scripts.lua``` из корня проекта).


## Результаты нагрузочного тестирования
1. Тестирование с сохранением диалогов в ***Postgre SQL***
- Производилось на пустой базе диалогов
- Конфигурация тестов (на грани, одна ошибка проскочила. Выше уже растет кол-во ошибок)
![img_4.png](img_4.png)
- RPC
![img_5.png](img_5.png)
- Latency
![img_6.png](img_6.png)
- Сводная таблица
![img_7.png](img_7.png)
2. Тестирование с сохранением диалогов в ***Tarantool***
- Производилось на пустом спейсе. Первоначальное тестирование показала высокий процент ошибок из-за того, что был выбран индекс Date + keyhash и множество записей создавалось в ту же самую милисекунду. Пропускная способность стала очень высокой. После добавления в индекс UUID на последнюю позицию, ситуация поменялась и пропускная способность понизилась, ошибки пропали.
- первоначальный индекс tarantool
```lua
box.space.dialog_messages:create_index('key_hash_created_date_idx', { type = "TREE", unique = true, parts = { 2, 'number', 6, 'string' }, if_not_exists = true })
```
- новый индекс tarantool
```lua
box.space.dialog_messages:create_index('key_hash_created_date_idx', { type = "TREE", unique = true, parts = { 2, 'number', 6, 'string', 1, 'uuid'}, if_not_exists = true })
```
- Конфигурация тестов - при той же конфигурации ошибок не было
- RPC
![img_11.png](img_11.png)
- Latency
![img_12.png](img_12.png)
- Сводная таблица
![img_13.png](img_13.png)
3. Выводы
В Tarantool при использовании такого же индекса, как и в Postre SQL наблюдался резкий рост RPC, что создало большое количесво ошибок. При усложнении структуры индекса все показатели приблизились к Postgre SQL. При этом в Tarantool не было ошибок и пики RPC были выше.

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