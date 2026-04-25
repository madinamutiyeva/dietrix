-- V10: Personal recipes (private to each user)

CREATE TABLE personal_recipes (
    id                 BIGSERIAL PRIMARY KEY,
    user_id            BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title              VARCHAR(255) NOT NULL,
    description        TEXT,
    instructions       TEXT,
    calories           INTEGER,
    protein            INTEGER,
    carbs              INTEGER,
    fat                INTEGER,
    cook_time_minutes  INTEGER,
    cuisine            VARCHAR(100),
    image_url          TEXT,
    meal_type          VARCHAR(50),
    diet_type          VARCHAR(50),
    created_at         TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMP
);

CREATE INDEX idx_personal_recipes_user ON personal_recipes(user_id, created_at DESC);

CREATE TABLE personal_recipe_ingredients (
    id                 BIGSERIAL PRIMARY KEY,
    personal_recipe_id BIGINT NOT NULL REFERENCES personal_recipes(id) ON DELETE CASCADE,
    name               VARCHAR(255) NOT NULL,
    amount             VARCHAR(50),
    unit               VARCHAR(50)
);

CREATE INDEX idx_personal_recipe_ingredients ON personal_recipe_ingredients(personal_recipe_id);

