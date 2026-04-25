-- V12: Weight/water tracking, free meal logs, push devices, notification preferences

-- ──────────────────────────────────────────────────────────────────────
-- Weight tracking
-- ──────────────────────────────────────────────────────────────────────
CREATE TABLE weight_logs (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    weight_kg  NUMERIC(5,2) NOT NULL,
    logged_on  DATE NOT NULL,
    note       VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    CONSTRAINT uk_weight_user_day UNIQUE (user_id, logged_on)
);
CREATE INDEX idx_weight_logs_user_date ON weight_logs(user_id, logged_on DESC);

-- ──────────────────────────────────────────────────────────────────────
-- Water tracking (each "drink" event)
-- ──────────────────────────────────────────────────────────────────────
CREATE TABLE water_logs (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    amount_ml  INTEGER NOT NULL CHECK (amount_ml > 0),
    logged_on  DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);
CREATE INDEX idx_water_logs_user_date ON water_logs(user_id, logged_on DESC);

-- ──────────────────────────────────────────────────────────────────────
-- Free meal log — "ate in a cafe", not from any plan
-- ──────────────────────────────────────────────────────────────────────
CREATE TABLE free_meal_logs (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name       VARCHAR(200) NOT NULL,
    meal_type  VARCHAR(30),
    calories   INTEGER,
    protein    INTEGER,
    carbs      INTEGER,
    fat        INTEGER,
    logged_on  DATE NOT NULL,
    note       VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);
CREATE INDEX idx_free_meal_logs_user_date ON free_meal_logs(user_id, logged_on DESC);

-- ──────────────────────────────────────────────────────────────────────
-- Push devices (FCM tokens)
-- ──────────────────────────────────────────────────────────────────────
CREATE TABLE device_tokens (
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token        VARCHAR(500) NOT NULL UNIQUE,
    platform     VARCHAR(20) NOT NULL,           -- WEB | IOS | ANDROID
    last_used_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP
);
CREATE INDEX idx_device_tokens_user ON device_tokens(user_id);

-- ──────────────────────────────────────────────────────────────────────
-- Notification preferences (per user)
-- ──────────────────────────────────────────────────────────────────────
CREATE TABLE notification_preferences (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    push_enabled        BOOLEAN NOT NULL DEFAULT TRUE,
    email_enabled       BOOLEAN NOT NULL DEFAULT TRUE,
    meal_reminders      BOOLEAN NOT NULL DEFAULT TRUE,
    pantry_expiry       BOOLEAN NOT NULL DEFAULT TRUE,
    weekly_report       BOOLEAN NOT NULL DEFAULT TRUE,
    water_reminders     BOOLEAN NOT NULL DEFAULT FALSE,
    breakfast_time      TIME    NOT NULL DEFAULT '08:00',
    lunch_time          TIME    NOT NULL DEFAULT '13:00',
    dinner_time         TIME    NOT NULL DEFAULT '19:00',
    quiet_hours_start   TIME,
    quiet_hours_end     TIME,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP
);

-- ──────────────────────────────────────────────────────────────────────
-- User settings (theme/locale/units)
-- ──────────────────────────────────────────────────────────────────────
CREATE TABLE user_settings (
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    theme        VARCHAR(20) NOT NULL DEFAULT 'SYSTEM',  -- LIGHT | DARK | SYSTEM
    locale       VARCHAR(10) NOT NULL DEFAULT 'ru',      -- ru | kk | en
    units        VARCHAR(20) NOT NULL DEFAULT 'METRIC',  -- METRIC | IMPERIAL
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP
);

