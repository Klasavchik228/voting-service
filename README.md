# TrustVote: Java Kafka Service



## Содержание



1.  [Введение](https://www.google.com/search?q=%231-%D0%B2%D0%B2%D0%B5%D0%B4%D0%B5%D0%BD%D0%B8%D0%B5)

2.  [Роль в архитектуре TrustVote](https://www.google.com/search?q=%232-%D1%80%D0%BE%D0%BB%D1%8C-%D0%B2-%D0%B0%D1%80%D1%85%D0%B8%D1%82%D0%B5%D0%BA%D1%82%D1%83%D1%80%D0%B5-trustvote)

3.  [Возможности](https://www.google.com/search?q=%233-%D0%B2%D0%BE%D0%B7%D0%BC%D0%BE%D0%B6%D0%BD%D0%BE%D1%81%D1%82%D0%B8)

4.  [Предварительные требования](https://www.google.com/search?q=%234-%D0%BF%D1%80%D0%B5%D0%B4%D0%B2%D0%B0%D1%80%D0%B8%D1%82%D0%B5%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%82%D1%80%D0%B5%D0%B1%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D1%8F)

5.  [Настройка и установка](https://www.google.com/search?q=%235-%D0%BD%D0%B0%D1%81%D1%82%D1%80%D0%BE%D0%B9%D0%BA%D0%B0-%D0%B8-%D1%83%D1%81%D1%82%D0%B0%D0%BD%D0%BE%D0%B2%D0%BA%D0%B0)

6.  [Конфигурация](https://www.google.com/search?q=%236-%D0%BA%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D1%8F)

7.  [Kafka-топики (Потребитель/Производитель)](https://www.google.com/search?q=%237-kafka-%D1%82%D0%BE%D0%BF%D0%B8%D0%BA%D0%B8-%D0%BF%D0%BE%D1%82%D1%80%D0%B5%D0%B1%D0%B8%D1%82%D0%B5%D0%BB%D1%8C%D0%BF%D1%80%D0%BE%D0%B8%D0%B7%D0%B2%D0%BE%D0%B4%D0%B8%D1%82%D0%B5%D0%BB%D1%8C)

8.  [Структура базы данных](https://www.google.com/search?q=%238-%D1%81%D1%82%D1%80%D1%83%D0%BA%D1%82%D1%83%D1%80%D0%B0-%D0%B1%D0%B0%D0%B7%D1%8B-%D0%B4%D0%B0%D0%BD%D0%BD%D1%8B%D1%85)

9.  [Обработка событий и бизнес-логика](https://www.google.com/search?q=%239-%D0%BE%D0%B1%D1%80%D0%B0%D0%B1%D0%BE%D1%82%D0%BA%D0%B0-%D1%81%D0%BE%D0%B1%D1%8B%D1%82%D0%B8%D0%B9-%D0%B8-%D0%B1%D0%B8%D0%B7%D0%BD%D0%B5%D1%81-%D0%BB%D0%BE%D0%B3%D0%B8%D0%BA%D0%B0)

10. [Устранение неполадок](https://www.google.com/search?q=%2310-%D1%83%D1%81%D1%82%D1%80%D0%B0%D0%BD%D0%B5%D0%BD%D0%B8%D0%B5-%D0%BD%D0%B5%D0%BF%D0%BE%D0%BB%D0%B0%D0%B4%D0%BE%D0%BA)

11. [Будущие улучшения](https://www.google.com/search?q=%2311-%D0%B1%D1%83%D0%B4%D1%83%D1%89%D0%B8%D0%B5-%D1%83%D0%BB%D1%83%D1%87%D1%88%D0%B5%D0%BD%D0%B8%D1%8F)

12. [Лицензия](https://www.google.com/search?q=%2312-%D0%BB%D0%B8%D1%86%D0%B5%D0%BD%D0%B7%D0%B8%D1%8F)



-----



## 1\. Введение



**Java Kafka Service** является ключевым компонентом бэкенда платформы TrustVote. Это Spring Boot приложение, отвечающее за асинхронную обработку данных, взаимодействие с базой данных PostgreSQL и управление бизнес-логикой, связанной с голосованиями и пользователями, через Kafka.



-----



## 2\. Роль в архитектуре TrustVote



В общей архитектуре TrustVote Java Kafka Service выполняет следующие функции:



  * **Потребитель Kafka-сообщений:** Слушает различные Kafka-топики для получения запросов и событий от Go API Gateway (например, запросы данных пользователя, события голосования, запросы на создание голосований).

  * **Производитель Kafka-сообщений:** После обработки запросов публикует ответы и подтверждения обратно в Kafka, чтобы Go API Gateway мог их получить и передать фронтенду.

  * **Взаимодействие с базой данных:** Использует Hibernate (через Spring Data JPA) для сохранения, обновления и извлечения данных о пользователях, голосованиях, вариантах голосования и голосах из базы данных PostgreSQL.

  * **Бизнес-логика:** Реализует логику для:

      * Создания и обновления голосований.

      * Записи голосов пользователей.

      * Получения детальной информации о голосованиях.

      * Предоставления списков последних голосований.

      * Управления пользовательскими данными (профилями, историей голосований).

  * **(Потенциально) Слушатель событий блокчейна:** В будущих итерациях может напрямую потреблять события блокчейна (например, о стейкинге) для поддержания состояния базы данных в актуальном виде.



-----



## 3\. Возможности



  * **Обработка асинхронных запросов:** Эффективная обработка запросов через Kafka, что улучшает масштабируемость и отказоустойчивость.

  * **Управление данными голосований:** Создание, чтение и обновление информации о голосованиях и их вариантах.

  * **Запись голосов:** Надежная запись голосов, поданных пользователями.

  * **Управление пользователями:** Хранение базовой информации о пользователях (адресах кошельков) и их истории взаимодействия с DApp.

  * **Интеграция с PostgreSQL:** Использование реляционной базы данных для структурированного хранения данных.

  * **Spring Boot:** Быстрая разработка и удобное развертывание.



-----



## 4\. Предварительные требования



Для запуска и разработки Java Kafka Service вам потребуются:



  * **Java Development Kit (JDK 17+):** Среда выполнения и компиляции Java-кода.

  * **Maven (3.8+):** Инструмент для сборки проекта и управления зависимостями.

  * **Docker & Docker Compose:** Для локального запуска Kafka и PostgreSQL.

  * **PostgreSQL:** Установленный и запущенный сервер базы данных (или используйте Docker).

  * **Apache Kafka:** Запущенный брокер Kafka (или используйте Docker).



-----



## 5\. Настройка и установка



Следуйте этим шагам, чтобы запустить Java Kafka Service локально.



1.  **Клонируйте репозиторий:**

    Если вы еще этого не сделали, клонируйте основной репозиторий TrustVote:



    ```bash

    git clone https://github.com/your-username/trustvote.git

    cd trustvote

    ```



2.  **Запустите зависимости с помощью Docker Compose:**

    Перейдите в корневой каталог проекта, где находится ваш `docker-compose.yml` (или в соответствующий подкаталог, если он там расположен, например `deploy/`).



    ```bash

    docker-compose up -d kafka zookeeper postgres pgadmin # Убедитесь, что имена сервисов соответствуют вашему docker-compose.yml

    ```



      * Дождитесь полной инициализации всех сервисов. Вы можете проверить их статус с помощью `docker-compose ps`.



3.  **Создайте Kafka-топики:**

    Вам необходимо убедиться, что все необходимые Kafka-топики созданы. Вы можете сделать это вручную:



    ```bash

    # Подключитесь к контейнеру Kafka (замените <kafka-container-id> на фактический ID из `docker-compose ps`)

    docker exec -it <kafka-container-id> bash



    # Создайте топики, используемые сервисом (имена должны соответствовать конфигурации в application.properties)

    kafka-topics --create --topic user_data_request --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

    kafka-topics --create --topic user_data_response --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

    kafka-topics --create --topic vote_cast_event --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

    kafka-topics --create --topic create_voting_request --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

    kafka-topics --create --topic get_voting_info_request --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

    kafka-topics --create --topic voting_info_response --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

    kafka-topics --create --topic get_all_votings_request --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

    kafka-topics --create --topic all_votings_response --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

    # Выйдите из контейнера Kafka

    exit

    ```



4.  **Перейдите в каталог проекта Java Kafka Service:**



    ```bash

    cd <путь_к_вашему_java_сервису> # например, java-kafka-service/

    ```



5.  **Настройте файл конфигурации:**

    Отредактируйте `src/main/resources/application.properties` (или `application.yml`) для соответствия вашей среде. Подробности смотрите в разделе [Конфигурация](https://www.google.com/search?q=%236-%D0%BA%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D1%8F).



6.  **Соберите проект:**



    ```bash

    mvn clean install

    ```



    Это скачает все зависимости и соберет JAR-файл приложения в каталоге `target/`.



7.  **Запустите сервис:**



    ```bash

    java -jar target/<имя_вашего_jar-файла_сервиса>.jar

    ```



      * Держите этот терминал открытым, чтобы видеть логи сервиса.



-----



## 6\. Конфигурация



Конфигурация Java Kafka Service находится в файле `src/main/resources/application.properties` (или `application.yml`).



**Ключевые параметры конфигурации:**



```properties

# Настройки Kafka

spring.kafka.bootstrap-servers=localhost:9092 # Адрес Kafka-брокера

spring.kafka.consumer.group-id=java_kafka_service_consumer_group # ID группы потребителей

spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer

spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer # Для сериализации JSON

spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer

spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer # Для десериализации JSON

spring.kafka.properties.spring.json.trusted.packages=* # Разрешить десериализацию всех пакетов для Kafka



# Конфигурация базы данных PostgreSQL

spring.datasource.url=jdbc:postgresql://localhost:5432/trustvote_db # URL вашей БД

spring.datasource.username=trustvote_user # Имя пользователя БД

spring.datasource.password=trustvote_password # Пароль БД

spring.datasource.driver-class-name=org.postgresql.Driver



# Конфигурация JPA/Hibernate

spring.jpa.hibernate.ddl-auto=update # 'update' для разработки (автоматическое обновление схемы), 'none' для продакшена

spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect # Диалект БД

spring.jpa.show-sql=true # Показать SQL-запросы в логах

spring.jpa.properties.hibernate.format_sql=true # Отформатировать SQL



# Настройки сервера (если сервис предоставляет свои собственные HTTP-эндпоинты, что обычно не для Kafka-сервиса)

# server.port=8081



# Настройки кодировки для логов и вывода (ВАЖНО для русского языка)

server.servlet.encoding.charset=UTF-8

logging.charset.console=UTF-8

logging.charset.file=UTF-8



# Разрешить переопределение определений бинов (может потребоваться в сложных конфигурациях Spring)

spring.main.allow-bean-definition-overriding=true



# Список Kafka-топиков, которые сервис будет слушать/производить (для удобства использования в коде)

# Пример:

kafka.topics.user-data-request=user_data_request

kafka.topics.user-data-response=user_data_response

kafka.topics.vote-cast-event=vote_cast_event

kafka.topics.create-voting-request=create_voting_request

kafka.topics.get-voting-info-request=get_voting_info_request

kafka.topics.voting-info-response=voting_info_response

kafka.topics.get-all-votings-request=get_all_votings_request

kafka.topics.all-votings-response=all_votings_response

```



-----



## 7\. Kafka-топики (Потребитель/Производитель)



Java Kafka Service активно взаимодействует со следующими Kafka-топиками:



### Топики, которые сервис **потребляет (слушает)**:



  * `user_data_request`: Запросы от API Gateway для получения данных профиля пользователя и истории голосований.

  * `vote_cast_event`: События, сигнализирующие о том, что пользователь проголосовал (для записи голоса в БД).

  * `create_voting_request`: Запросы на создание нового голосования.

  * `get_voting_info_request`: Запросы на получение детальной информации о конкретном голосовании.

  * `get_all_votings_request`: Запросы на получение списка всех (или последних) голосований.

  * **(Возможные будущие):** `blockchain_event_stake`, `blockchain_event_unstake`, `blockchain_event_claim` - для синхронизации данных о стейкинге и наградах из блокчейна.



### Топики, в которые сервис **производит (отправляет)** сообщения:



  * `user_data_response`: Ответы на запросы `user_data_request`, содержащие данные профиля пользователя.

  * `voting_info_response`: Ответы на запросы `get_voting_info_request`, содержащие детальную информацию о голосовании.

  * `all_votings_response`: Ответы на запросы `get_all_votings_request`, содержащие список голосований.

  * **(Возможные будущие):** `voting_confirmation_event`, `error_event` - для подтверждений успешной обработки или сообщений об ошибках.



-----



## 8\. Структура базы данных



Сервис использует PostgreSQL для хранения данных. Основные сущности и их отношения:



  * **`users`**:



      * `id` (PRIMARY KEY): Уникальный идентификатор пользователя.

      * `wallet_address` (UNIQUE): Адрес кошелька пользователя.

      * `creation_date`: Дата создания записи пользователя.

      * `last_active`: Дата последней активности пользователя.

      * ... (могут быть добавлены другие поля, например, `email`, `username`)



  * **`votings`**:



      * `id` (PRIMARY KEY): Уникальный идентификатор голосования.

      * `title`: Название голосования.

      * `description`: Описание голосования.

      * `creator_id` (FOREIGN KEY к `users.id`): Ссылка на пользователя, создавшего голосование.

      * `start_date`: Дата и время начала голосования.

      * `end_date`: Дата и время окончания голосования.

      * `is_private`: Булево значение, указывающее, является ли голосование приватным.

      * `min_votes`: Минимальное количество голосов для действительности голосования.

      * `creation_date`: Дата создания записи голосования.



  * **`voting_options`**:



      * `option_id` (PRIMARY KEY): Уникальный идентификатор варианта ответа.

      * `voting_id` (FOREIGN KEY к `votings.id`): Ссылка на голосование, к которому относится вариант.

      * `text`: Текст варианта ответа (например, "За", "Против", "Воздержаться").



  * **`votes`**:



      * `id` (PRIMARY KEY): Уникальный идентификатор голоса.

      * `voting_id` (FOREIGN KEY к `votings.id`): Ссылка на голосование.

      * `voter_id` (FOREIGN KEY к `users.id`): Ссылка на пользователя, который проголосовал.

      * `option_id` (FOREIGN KEY к `voting_options.id`): Ссылка на выбранный вариант ответа.

      * `vote_date`: Дата и время подачи голоса.

      * `transaction_hash` (опционально): Хеш транзакции в блокчейне, если голосование записывается и там.



-----



## 9\. Обработка событий и бизнес-логика



Основная логика обработки находится в классах, помеченных `@KafkaListener`, и связанных с ними компонентах (`@Service`, `@Repository`).



  * **`KafkaConsumerService`**: Содержит методы `@KafkaListener`, которые слушают входящие топики Kafka.

  * **`VoteCastHandler`**: Отвечает за обработку событий о поданных голосах.

      * **Важный аспект:** При получении `vote_cast_event`, этот обработчик **проверяет наличие пользователя** в базе данных по предоставленному адресу кошелька. Если пользователь не найден, он **автоматически создает новую запись пользователя**, прежде чем продолжить обработку голоса. Это предотвращает ошибку "Пользователь не найден".

  * **`VotingService`**: Содержит основную бизнес-логику для работы с голосованиями (получение деталей, списка, создание).

  * **`UserService`**: Управляет данными пользователей, включая их создание и поиск.

  * **`VotingRepository`, `UserRepository`, `VoteRepository`, `VotingOptionRepository`**: Spring Data JPA репозитории для взаимодействия с базой данных.



-----



## 10\. Будущие улучшения



  * **Обработка событий блокчейна:** Реализовать слушателей для потребления событий напрямую из блокчейна (например, о стейкинге ETH), чтобы данные в базе данных всегда соответствовали состоянию контрактов.

  * **Метрики и мониторинг:** Интеграция с Prometheus/Grafana для мониторинга производительности сервиса и состояния Kafka.

  * **Расширенная бизнес-логика:** Добавление проверок для голосований (например, срок действия голосования, право на голос).

  * **Обработка ошибок и повторные попытки:** Улучшенная логика обработки ошибок для Kafka-сообщений, включая повторные попытки и отправку в Dead-Letter Queue (DLQ) для сообщений, которые не удалось обработать.

  * **Кэширование:** Использование Redis или другого кэша для часто запрашиваемых данных, чтобы снизить нагрузку на базу данных.



-----
