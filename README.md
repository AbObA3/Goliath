### сваггер
http://localhost:8080/swagger-ui

### Это скрипт для базы
```
create database online_courses;
create schema if not exists goliath;

-- Создание последовательности для таблицы users
CREATE SEQUENCE online_courses.goliath.users_id_seq
    START WITH 1001000
    INCREMENT BY 1001;

-- Создание таблицы users
CREATE TABLE online_courses.goliath.users (
                       id BIGINT PRIMARY KEY DEFAULT nextval('online_courses.goliath.users_id_seq'), -- Уникальный идентификатор пользователя
                       username VARCHAR(255) NOT NULL UNIQUE,                -- Имя пользователя
                       password VARCHAR(255) NOT NULL,                       -- Пароль
                       role VARCHAR(50) NOT NULL,                            -- Роль пользователя (e.g., USER, ADMIN)
                       metadata JSONB,                                       -- Поле для хранения метаданных в формате JSONB
                       created_at TIMESTAMP DEFAULT NOW(),                   -- Время создания записи
                       updated_at TIMESTAMP DEFAULT NOW()                    -- Время последнего обновления
);

-- Добавление индекса на поле username для ускорения поиска
CREATE UNIQUE INDEX idx_users_username ON online_courses.goliath.users (username);

-- Создание последовательности для таблицы courses
CREATE SEQUENCE online_courses.goliath.courses_id_seq
    START WITH 1001000
    INCREMENT BY 1001;

-- Создание таблицы courses
CREATE TABLE online_courses.goliath.courses (
                         id BIGINT PRIMARY KEY DEFAULT nextval('online_courses.goliath.courses_id_seq'), -- Уникальный идентификатор курса
                         title VARCHAR(255) NOT NULL,                            -- Название курса
                         description TEXT,                                       -- Описание курса
                         image_url TEXT,                                         -- URL изображения курса
                         metadata JSONB,                                        -- Поле для хранения метаданных в формате JSONB
                         created_at TIMESTAMP DEFAULT NOW(),                    -- Время создания записи
                         updated_at TIMESTAMP DEFAULT NOW()                     -- Время последнего обновления
);

insert into online_courses.goliath.courses(title, description,image_url,metadata) values ('python', 'Nihuya sebe pitonchik', 'Здесь будет путь изображения курса на дисковом сервере', '{"info" : "info", "content" : "content"}'::jsonb);
insert into online_courses.goliath.courses(title, description,image_url,metadata) values ('Java', 'David Extra LOH', 'Здесь будет путь изображения курса на дисковом сервере', '{"info" : "info", "content" : "content"}'::jsonb);
```
### это docker-compose.yaml

```
services:
  db:
    image: postgres:15
    restart: always
    ports:
      - "5432:5432"
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
  backend:
    image: aboba3/goliath
    ports:
      - "8080:8080"
    environment:
      QUARKUS_DATASOURCE_URL: jdbc:postgresql://db:5432/online_courses
      QUARKUS_DATASOURCE_USERNAME: postgres
      QUARKUS_DATASOURCE_PASSWORD: postgres
      QUARKUS_ISSUER: http://backend:8080
    depends_on:
      - db
```