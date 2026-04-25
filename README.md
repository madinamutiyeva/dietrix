# 🥗 DIETRIX — AI-Powered Personalized Recipe & Meal Plan Platform

AI-платформа персонализированного подбора рецептов и недельного плана питания.

## 🏗 Architecture

**Modular Monolith** на Spring Boot 3.2 с 9 модулями:

| Модуль | Описание |
|--------|----------|
| `auth` | JWT аутентификация (signup, signin, refresh, reset password) |
| `user-profile` | Профиль пользователя, предпочтения, цели КБЖУ |
| `onboarding` | Пошаговый онбординг (3 шага) |
| `pantry` | Управление продуктами дома |
| `recipes` | Рецепты, AI-генерация, избранное |
| `meal-plan` | Недельные планы питания, шоппинг-лист |
| `assistant` | AI-чат по питанию, FAQ |
| `dashboard` | Агрегированные данные пользователя |
| `common` | Общие DTO, исключения, утилиты, справочники |

## 🛠 Tech Stack

- **Java 17**, Spring Boot 3.2.5
- **Spring Security** + JWT (jjwt 0.12.5)
- **Spring Data JPA** + PostgreSQL
- **Flyway** миграции
- **OpenAI API** (GPT-3.5/4) для генерации рецептов и чата
- **SpringDoc OpenAPI** (Swagger UI)
- **Lombok**
- **Docker** + Docker Compose

## 🚀 Quick Start

### С Docker:
```bash
# Скопировать и настроить переменные окружения
cp .env.example .env
# Отредактировать .env (OPENAI_API_KEY)

# Запустить
docker-compose up -d
```

### Без Docker:
1. Установить PostgreSQL, создать БД `dietrix`
2. Настроить `application.yml` или переменные окружения
3. Собрать и запустить:
```bash
./mvnw spring-boot:run
```

### Swagger UI:
```
http://localhost:8080/swagger-ui.html
```

## 📋 API Endpoints

### Auth (`/api/auth`)
- `POST /signup` — Регистрация
- `POST /signin` — Вход
- `POST /refresh` — Обновление токена
- `POST /logout` — Выход
- `POST /forgot-password` — Запрос сброса пароля
- `POST /reset-password` — Сброс пароля

### User Profile (`/api/users/me`)
- `GET /` — Получить профиль
- `PATCH /` — Обновить профиль
- `GET /preferences` — Предпочтения
- `PUT /preferences` — Обновить предпочтения
- `GET /targets` — Рассчитанные КБЖУ
- `GET /favorites` — Избранные рецепты
- `POST /favorites/{recipeId}` — Добавить в избранное
- `DELETE /favorites/{recipeId}` — Удалить из избранного

### Onboarding (`/api/onboarding`)
- `GET /` — Статус онбординга
- `PUT /basic-info` — Шаг 1: базовая информация
- `PUT /activity-goal` — Шаг 2: активность и цель
- `PUT /preferences` — Шаг 3: пищевые предпочтения

### Pantry (`/api/pantry`)
- `GET /items` — Список продуктов
- `POST /items` — Добавить продукт
- `POST /items/bulk` — Добавить несколько
- `PATCH /items/{id}` — Обновить
- `DELETE /items/{id}` — Удалить
- `GET /summary` — Сводка

### Recipes (`/api/recipes`)
- `GET /` — Список рецептов (фильтры)
- `GET /recommended` — Рекомендации
- `POST /generate` — AI-генерация рецепта
- `GET /recent-generated` — Последние сгенерированные
- `GET /{id}` — Детали рецепта

### Meal Plans (`/api/meal-plans`)
- `GET /current` — Текущий план
- `POST /generate` — Сгенерировать недельный план
- `GET /{id}` — План по ID
- `POST /{id}/days/{day}/meals/{mealId}/complete` — Отметить приём пищи
- `GET /{id}/shopping-list` — Шоппинг-лист

### AI Assistant (`/api/assistant`)
- `POST /chat` — AI-чат
- `GET /faq` — FAQ

### Reference (`/api/reference`)
- `GET /allergies`, `/goals`, `/activity-levels`, `/diet-types`, `/genders`

### Dashboard (`/api/dashboard`)
- `GET /` — Агрегированные данные

## 📊 Database

PostgreSQL. Схема создаётся автоматически Hibernate (`spring.jpa.hibernate.ddl-auto=update`)
на основе `@Entity`-классов — никаких ручных шагов при первом запуске не требуется.

Справочные данные (allergies, goals, activity levels, diet types, genders) хранятся
как Java-enum-ы в пакете `kz.dietrix.common.reference` и отдаются через `/api/reference/*`
— отдельных таблиц-справочников в БД нет.

SQL-файлы в `src/main/resources/db/migration/` оставлены как историческая справка
по эволюции схемы и опциональные seed-скрипты (например, `V11__seed_recipes.sql`
наполняет каталог рецептов). Flyway не подключён, поэтому эти файлы автоматически
не выполняются — при необходимости запускайте их вручную через `psql`.

## 🔒 Security

- JWT access + refresh token
- BCrypt password encoding
- CORS configuration
- Stateless session
- Global exception handling
