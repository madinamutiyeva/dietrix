-- V11: Seed recipes — 4 types (breakfast, main, snack, dessert), 10+ cuisines
-- First update any existing lunch/dinner to 'main'
UPDATE recipes SET meal_type = 'main' WHERE LOWER(meal_type) IN ('lunch', 'dinner');

-- ═══════════════════════════════════════════════════════════════
--  BREAKFAST
-- ═══════════════════════════════════════════════════════════════

INSERT INTO recipes (title, description, instructions, calories, protein, carbs, fat, cook_time_minutes, cuisine, meal_type, diet_type, is_generated, created_at, updated_at)
VALUES
('Classic French Omelette', 'Light and fluffy French-style omelette with herbs.', '1. Whisk 3 eggs with salt. 2. Melt butter in pan on medium heat. 3. Pour eggs, stir gently, fold when set.', 280, 20, 2, 22, 10, 'French', 'breakfast', 'balanced', false, NOW(), NOW()),
('Japanese Tamagoyaki', 'Sweet rolled Japanese egg omelette.', '1. Mix eggs with soy sauce, sugar, mirin. 2. Cook thin layers in rectangular pan. 3. Roll each layer over the previous one.', 180, 12, 8, 10, 15, 'Japanese', 'breakfast', 'balanced', false, NOW(), NOW()),
('Turkish Menemen', 'Scrambled eggs with tomatoes, peppers and spices.', '1. Sauté onion and peppers. 2. Add chopped tomatoes, cook 5 min. 3. Add eggs, stir until just set. Serve with bread.', 250, 14, 12, 16, 15, 'Turkish', 'breakfast', 'balanced', false, NOW(), NOW()),
('Mexican Chilaquiles', 'Crispy tortilla chips simmered in salsa with eggs.', '1. Fry tortilla pieces until crispy. 2. Pour salsa verde over chips. 3. Top with fried eggs and cheese.', 420, 18, 35, 24, 20, 'Mexican', 'breakfast', 'balanced', false, NOW(), NOW()),
('Indian Poha', 'Light flattened rice with peanuts, turmeric and lime.', '1. Rinse poha, drain. 2. Sauté mustard seeds, peanuts, onion. 3. Add poha, turmeric, salt. Garnish with lime and cilantro.', 300, 8, 52, 8, 15, 'Indian', 'breakfast', 'vegetarian', false, NOW(), NOW()),
('Korean Gyeran-ppang', 'Korean egg bread — fluffy bun with a whole egg baked inside.', '1. Prepare sweet batter. 2. Pour into molds, crack egg on top. 3. Bake at 180°C for 15 minutes.', 220, 10, 28, 8, 20, 'Korean', 'breakfast', 'balanced', false, NOW(), NOW()),
('American Pancakes', 'Fluffy buttermilk pancakes with maple syrup.', '1. Mix flour, baking powder, egg, buttermilk. 2. Pour batter onto hot griddle. 3. Flip when bubbles form. Serve with maple syrup.', 380, 10, 55, 12, 15, 'American', 'breakfast', 'balanced', false, NOW(), NOW()),
('Italian Frittata', 'Baked egg dish with vegetables and parmesan.', '1. Sauté zucchini and onion. 2. Pour whisked eggs over vegetables. 3. Sprinkle parmesan, bake at 180°C for 12 min.', 310, 22, 6, 22, 25, 'Italian', 'breakfast', 'vegetarian', false, NOW(), NOW()),
('Kazakh Baursak with Honey', 'Fried dough pieces served with honey and tea.', '1. Make dough from flour, milk, yeast, butter. 2. Cut into pieces, deep fry until golden. 3. Drizzle with honey.', 450, 8, 60, 20, 30, 'Kazakh', 'breakfast', 'balanced', false, NOW(), NOW()),
('Russian Syrniki', 'Pan-fried cottage cheese pancakes with sour cream.', '1. Mix cottage cheese, egg, flour, sugar. 2. Form small patties. 3. Pan-fry until golden on both sides. Serve with sour cream.', 320, 18, 30, 14, 20, 'Russian', 'breakfast', 'vegetarian', false, NOW(), NOW()),

-- ═══════════════════════════════════════════════════════════════
--  MAIN (lunch + dinner)
-- ═══════════════════════════════════════════════════════════════

('Italian Chicken Parmigiana', 'Breaded chicken breast with marinara and mozzarella.', '1. Bread chicken cutlets. 2. Fry until golden. 3. Top with marinara and mozzarella, bake at 200°C for 10 min.', 550, 42, 25, 30, 35, 'Italian', 'main', 'balanced', false, NOW(), NOW()),
('Japanese Chicken Teriyaki', 'Glazed chicken thighs in sweet soy teriyaki sauce.', '1. Pan-sear chicken thighs. 2. Add soy sauce, mirin, sugar glaze. 3. Simmer until sticky. Serve with rice.', 480, 35, 40, 18, 25, 'Japanese', 'main', 'balanced', false, NOW(), NOW()),
('Mexican Chicken Burrito Bowl', 'Rice bowl with seasoned chicken, beans, corn and salsa.', '1. Cook rice. 2. Grill spiced chicken strips. 3. Assemble bowl with beans, corn, avocado, salsa.', 520, 38, 55, 16, 25, 'Mexican', 'main', 'balanced', false, NOW(), NOW()),
('Indian Butter Chicken', 'Tender chicken in creamy tomato-butter sauce.', '1. Marinate chicken in yogurt and spices. 2. Grill or pan-fry chicken. 3. Simmer in tomato-cream sauce with butter and garam masala.', 480, 32, 18, 30, 40, 'Indian', 'main', 'balanced', false, NOW(), NOW()),
('Korean Bibimbap', 'Mixed rice bowl with vegetables, beef and gochujang.', '1. Cook rice. 2. Sauté spinach, carrots, mushrooms separately. 3. Top rice with vegetables, beef, fried egg. Mix with gochujang.', 510, 28, 62, 16, 30, 'Korean', 'main', 'balanced', false, NOW(), NOW()),
('French Ratatouille', 'Provençal vegetable stew with herbs.', '1. Slice eggplant, zucchini, tomatoes, peppers. 2. Layer in baking dish with herbs and olive oil. 3. Bake at 180°C for 45 min.', 220, 6, 28, 10, 50, 'French', 'main', 'vegetarian', false, NOW(), NOW()),
('Turkish Adana Kebab', 'Spiced minced lamb kebab grilled on skewers.', '1. Mix lamb mince with onion, red pepper flakes, cumin. 2. Shape onto skewers. 3. Grill over high heat 8 min. Serve with flatbread.', 460, 34, 8, 32, 20, 'Turkish', 'main', 'balanced', false, NOW(), NOW()),
('American Grilled Salmon', 'Pan-seared salmon fillet with lemon and asparagus.', '1. Season salmon with salt, pepper, lemon. 2. Sear skin-side down 4 min, flip 3 min. 3. Serve with roasted asparagus.', 420, 38, 8, 26, 20, 'American', 'main', 'balanced', false, NOW(), NOW()),
('Kazakh Beshbarmak', 'Traditional boiled meat with flat noodles and onion sauce.', '1. Boil beef or lamb until tender. 2. Roll and cut flat noodles, cook in broth. 3. Serve meat over noodles with onion sauce.', 580, 40, 45, 25, 90, 'Kazakh', 'main', 'balanced', false, NOW(), NOW()),
('Russian Beef Stroganoff', 'Tender beef strips in creamy mushroom sauce.', '1. Sear beef strips in hot pan. 2. Sauté mushrooms and onions. 3. Combine with sour cream sauce. Serve over egg noodles.', 520, 35, 35, 25, 30, 'Russian', 'main', 'balanced', false, NOW(), NOW()),
('Thai Green Curry', 'Creamy coconut curry with chicken and Thai basil.', '1. Fry green curry paste in oil. 2. Add coconut milk, chicken, bamboo shoots. 3. Simmer 15 min. Add Thai basil. Serve with rice.', 490, 30, 25, 30, 25, 'Thai', 'main', 'balanced', false, NOW(), NOW()),
('Italian Pasta Carbonara', 'Classic Roman pasta with egg, pecorino and guanciale.', '1. Cook spaghetti al dente. 2. Fry guanciale until crispy. 3. Toss pasta with egg-pecorino mixture and guanciale off heat.', 550, 25, 60, 22, 20, 'Italian', 'main', 'balanced', false, NOW(), NOW()),

-- ═══════════════════════════════════════════════════════════════
--  SNACK
-- ═══════════════════════════════════════════════════════════════

('Japanese Edamame', 'Steamed salted soybeans — classic Japanese snack.', '1. Boil edamame pods in salted water 5 min. 2. Drain. 3. Sprinkle with sea salt.', 120, 12, 8, 5, 5, 'Japanese', 'snack', 'vegetarian', false, NOW(), NOW()),
('Mexican Guacamole with Chips', 'Fresh avocado dip with tortilla chips.', '1. Mash avocados. 2. Mix in lime juice, onion, tomato, cilantro. 3. Serve with tortilla chips.', 280, 4, 24, 20, 10, 'Mexican', 'snack', 'vegetarian', false, NOW(), NOW()),
('Turkish Hummus', 'Creamy chickpea dip with tahini and olive oil.', '1. Blend chickpeas, tahini, lemon juice, garlic. 2. Add olive oil and salt. 3. Serve with pita bread.', 200, 8, 22, 10, 10, 'Turkish', 'snack', 'vegetarian', false, NOW(), NOW()),
('Indian Samosa', 'Crispy pastry filled with spiced potatoes and peas.', '1. Make dough, roll into cones. 2. Fill with spiced potato-pea mixture. 3. Deep fry until golden.', 260, 6, 30, 14, 30, 'Indian', 'snack', 'vegetarian', false, NOW(), NOW()),
('Korean Tteokbokki', 'Spicy stir-fried rice cakes in gochujang sauce.', '1. Boil rice cakes until soft. 2. Make sauce from gochujang, soy sauce, sugar. 3. Simmer rice cakes in sauce 10 min.', 320, 8, 62, 4, 15, 'Korean', 'snack', 'vegetarian', false, NOW(), NOW()),
('Italian Bruschetta', 'Toasted bread topped with tomato, basil and olive oil.', '1. Toast bread slices. 2. Mix diced tomatoes, garlic, basil, olive oil. 3. Spoon onto bread, drizzle with balsamic.', 180, 5, 22, 8, 10, 'Italian', 'snack', 'vegetarian', false, NOW(), NOW()),
('American Trail Mix', 'Crunchy mix of nuts, dried fruits and dark chocolate chips.', '1. Combine almonds, walnuts, raisins, cranberries. 2. Add dark chocolate chips. 3. Store in airtight container.', 250, 7, 28, 14, 5, 'American', 'snack', 'vegetarian', false, NOW(), NOW()),
('French Cheese Board', 'Selection of brie, camembert with grapes and crackers.', '1. Arrange brie and camembert on board. 2. Add grapes and walnuts. 3. Serve with crackers.', 320, 14, 18, 22, 5, 'French', 'snack', 'vegetarian', false, NOW(), NOW()),
('Russian Blini with Sour Cream', 'Mini pancakes served with sour cream.', '1. Make thin batter from flour, milk, egg. 2. Fry small pancakes. 3. Serve with sour cream.', 240, 8, 30, 10, 15, 'Russian', 'snack', 'vegetarian', false, NOW(), NOW()),
('Kazakh Kurt', 'Dried salty cheese balls — traditional Kazakh snack.', '1. Strain yogurt overnight. 2. Mix with salt, shape into balls. 3. Dry in sun or dehydrator for 2-3 days.', 110, 10, 4, 6, 5, 'Kazakh', 'snack', 'balanced', false, NOW(), NOW()),

-- ═══════════════════════════════════════════════════════════════
--  DESSERT (сладости)
-- ═══════════════════════════════════════════════════════════════

('Italian Tiramisu', 'Classic Italian coffee-mascarpone layered dessert.', '1. Dip ladyfingers in espresso. 2. Layer with mascarpone-egg cream. 3. Refrigerate 4 hours. Dust with cocoa.', 380, 8, 40, 22, 20, 'Italian', 'dessert', 'balanced', false, NOW(), NOW()),
('Japanese Mochi', 'Soft rice cake filled with sweet red bean paste.', '1. Steam glutinous rice flour with sugar. 2. Knead until smooth. 3. Wrap around red bean filling, dust with starch.', 160, 3, 35, 1, 25, 'Japanese', 'dessert', 'vegetarian', false, NOW(), NOW()),
('French Crème Brûlée', 'Creamy vanilla custard with caramelized sugar top.', '1. Heat cream with vanilla. 2. Whisk into egg yolks with sugar. 3. Bake in water bath 40 min. Torch sugar on top.', 340, 6, 30, 22, 50, 'French', 'dessert', 'vegetarian', false, NOW(), NOW()),
('Turkish Baklava', 'Layered phyllo pastry with walnuts and honey syrup.', '1. Layer phyllo sheets with melted butter. 2. Spread crushed walnuts between layers. 3. Bake until golden, pour honey syrup over.', 400, 6, 48, 22, 45, 'Turkish', 'dessert', 'vegetarian', false, NOW(), NOW()),
('Indian Gulab Jamun', 'Deep-fried milk dumplings soaked in rose-cardamom syrup.', '1. Mix milk powder, flour, ghee into dough. 2. Form balls, deep fry on low heat. 3. Soak in warm rose-cardamom sugar syrup.', 350, 5, 52, 14, 30, 'Indian', 'dessert', 'vegetarian', false, NOW(), NOW()),
('Korean Hotteok', 'Sweet filled Korean pancakes with brown sugar and nuts.', '1. Make yeast dough. 2. Fill with brown sugar, cinnamon, crushed peanuts. 3. Press flat, fry until golden and crispy.', 300, 6, 45, 12, 20, 'Korean', 'dessert', 'vegetarian', false, NOW(), NOW()),
('Mexican Churros', 'Fried dough sticks coated in cinnamon sugar.', '1. Pipe choux dough into hot oil. 2. Fry until golden and crispy. 3. Roll in cinnamon sugar. Serve with chocolate sauce.', 350, 5, 42, 18, 20, 'Mexican', 'dessert', 'balanced', false, NOW(), NOW()),
('American Chocolate Brownie', 'Rich fudgy chocolate brownie with walnuts.', '1. Melt butter and chocolate. 2. Mix in sugar, eggs, flour, walnuts. 3. Bake at 180°C for 25 minutes.', 380, 5, 44, 22, 30, 'American', 'dessert', 'vegetarian', false, NOW(), NOW()),
('Kazakh Chak-Chak', 'Fried dough pieces glazed in hot honey.', '1. Make dough from eggs and flour, cut into sticks. 2. Deep fry until golden. 3. Toss in hot honey, shape into mound.', 420, 6, 58, 18, 30, 'Kazakh', 'dessert', 'balanced', false, NOW(), NOW()),
('Russian Medovik', 'Layered honey cake with sour cream filling.', '1. Make thin honey-butter layers. 2. Bake each layer 5 min. 3. Stack with sour cream filling. Refrigerate overnight.', 360, 6, 48, 16, 60, 'Russian', 'dessert', 'balanced', false, NOW(), NOW());

-- ═══════════════════════════════════════════════════════════════
--  INGREDIENTS for each recipe
-- ═══════════════════════════════════════════════════════════════

INSERT INTO recipe_ingredients (recipe_id, name, amount, unit) VALUES
((SELECT id FROM recipes WHERE title='Classic French Omelette'), 'eggs', '3', 'pcs'),
((SELECT id FROM recipes WHERE title='Classic French Omelette'), 'butter', '15', 'g'),
((SELECT id FROM recipes WHERE title='Classic French Omelette'), 'fresh herbs', '5', 'g'),
((SELECT id FROM recipes WHERE title='Classic French Omelette'), 'salt', '2', 'g'),

((SELECT id FROM recipes WHERE title='Japanese Tamagoyaki'), 'eggs', '3', 'pcs'),
((SELECT id FROM recipes WHERE title='Japanese Tamagoyaki'), 'soy sauce', '5', 'ml'),
((SELECT id FROM recipes WHERE title='Japanese Tamagoyaki'), 'sugar', '5', 'g'),
((SELECT id FROM recipes WHERE title='Japanese Tamagoyaki'), 'mirin', '5', 'ml'),

((SELECT id FROM recipes WHERE title='Turkish Menemen'), 'eggs', '3', 'pcs'),
((SELECT id FROM recipes WHERE title='Turkish Menemen'), 'tomatoes', '200', 'g'),
((SELECT id FROM recipes WHERE title='Turkish Menemen'), 'green pepper', '100', 'g'),
((SELECT id FROM recipes WHERE title='Turkish Menemen'), 'onion', '50', 'g'),
((SELECT id FROM recipes WHERE title='Turkish Menemen'), 'olive oil', '15', 'ml'),

((SELECT id FROM recipes WHERE title='Mexican Chilaquiles'), 'tortillas', '4', 'pcs'),
((SELECT id FROM recipes WHERE title='Mexican Chilaquiles'), 'salsa verde', '200', 'ml'),
((SELECT id FROM recipes WHERE title='Mexican Chilaquiles'), 'eggs', '2', 'pcs'),
((SELECT id FROM recipes WHERE title='Mexican Chilaquiles'), 'cheese', '50', 'g'),

((SELECT id FROM recipes WHERE title='Indian Poha'), 'flattened rice', '200', 'g'),
((SELECT id FROM recipes WHERE title='Indian Poha'), 'peanuts', '30', 'g'),
((SELECT id FROM recipes WHERE title='Indian Poha'), 'onion', '80', 'g'),
((SELECT id FROM recipes WHERE title='Indian Poha'), 'turmeric', '3', 'g'),
((SELECT id FROM recipes WHERE title='Indian Poha'), 'lime', '1', 'pcs'),

((SELECT id FROM recipes WHERE title='Korean Gyeran-ppang'), 'egg', '1', 'pcs'),
((SELECT id FROM recipes WHERE title='Korean Gyeran-ppang'), 'flour', '100', 'g'),
((SELECT id FROM recipes WHERE title='Korean Gyeran-ppang'), 'sugar', '20', 'g'),
((SELECT id FROM recipes WHERE title='Korean Gyeran-ppang'), 'milk', '80', 'ml'),

((SELECT id FROM recipes WHERE title='American Pancakes'), 'flour', '150', 'g'),
((SELECT id FROM recipes WHERE title='American Pancakes'), 'buttermilk', '200', 'ml'),
((SELECT id FROM recipes WHERE title='American Pancakes'), 'egg', '1', 'pcs'),
((SELECT id FROM recipes WHERE title='American Pancakes'), 'maple syrup', '30', 'ml'),

((SELECT id FROM recipes WHERE title='Italian Frittata'), 'eggs', '4', 'pcs'),
((SELECT id FROM recipes WHERE title='Italian Frittata'), 'zucchini', '150', 'g'),
((SELECT id FROM recipes WHERE title='Italian Frittata'), 'onion', '50', 'g'),
((SELECT id FROM recipes WHERE title='Italian Frittata'), 'parmesan', '30', 'g'),

((SELECT id FROM recipes WHERE title='Kazakh Baursak with Honey'), 'flour', '300', 'g'),
((SELECT id FROM recipes WHERE title='Kazakh Baursak with Honey'), 'milk', '150', 'ml'),
((SELECT id FROM recipes WHERE title='Kazakh Baursak with Honey'), 'yeast', '5', 'g'),
((SELECT id FROM recipes WHERE title='Kazakh Baursak with Honey'), 'honey', '40', 'ml'),
((SELECT id FROM recipes WHERE title='Kazakh Baursak with Honey'), 'butter', '30', 'g'),

((SELECT id FROM recipes WHERE title='Russian Syrniki'), 'cottage cheese', '300', 'g'),
((SELECT id FROM recipes WHERE title='Russian Syrniki'), 'egg', '1', 'pcs'),
((SELECT id FROM recipes WHERE title='Russian Syrniki'), 'flour', '40', 'g'),
((SELECT id FROM recipes WHERE title='Russian Syrniki'), 'sour cream', '50', 'g'),

((SELECT id FROM recipes WHERE title='Italian Chicken Parmigiana'), 'chicken breast', '200', 'g'),
((SELECT id FROM recipes WHERE title='Italian Chicken Parmigiana'), 'marinara sauce', '100', 'ml'),
((SELECT id FROM recipes WHERE title='Italian Chicken Parmigiana'), 'mozzarella', '80', 'g'),
((SELECT id FROM recipes WHERE title='Italian Chicken Parmigiana'), 'breadcrumbs', '50', 'g'),

((SELECT id FROM recipes WHERE title='Japanese Chicken Teriyaki'), 'chicken thigh', '250', 'g'),
((SELECT id FROM recipes WHERE title='Japanese Chicken Teriyaki'), 'soy sauce', '30', 'ml'),
((SELECT id FROM recipes WHERE title='Japanese Chicken Teriyaki'), 'mirin', '20', 'ml'),
((SELECT id FROM recipes WHERE title='Japanese Chicken Teriyaki'), 'rice', '150', 'g'),

((SELECT id FROM recipes WHERE title='Mexican Chicken Burrito Bowl'), 'chicken breast', '200', 'g'),
((SELECT id FROM recipes WHERE title='Mexican Chicken Burrito Bowl'), 'rice', '150', 'g'),
((SELECT id FROM recipes WHERE title='Mexican Chicken Burrito Bowl'), 'black beans', '100', 'g'),
((SELECT id FROM recipes WHERE title='Mexican Chicken Burrito Bowl'), 'avocado', '80', 'g'),
((SELECT id FROM recipes WHERE title='Mexican Chicken Burrito Bowl'), 'salsa', '60', 'ml'),

((SELECT id FROM recipes WHERE title='Indian Butter Chicken'), 'chicken breast', '250', 'g'),
((SELECT id FROM recipes WHERE title='Indian Butter Chicken'), 'tomato puree', '150', 'ml'),
((SELECT id FROM recipes WHERE title='Indian Butter Chicken'), 'cream', '80', 'ml'),
((SELECT id FROM recipes WHERE title='Indian Butter Chicken'), 'butter', '30', 'g'),
((SELECT id FROM recipes WHERE title='Indian Butter Chicken'), 'garam masala', '5', 'g'),

((SELECT id FROM recipes WHERE title='Korean Bibimbap'), 'rice', '200', 'g'),
((SELECT id FROM recipes WHERE title='Korean Bibimbap'), 'beef', '100', 'g'),
((SELECT id FROM recipes WHERE title='Korean Bibimbap'), 'spinach', '80', 'g'),
((SELECT id FROM recipes WHERE title='Korean Bibimbap'), 'egg', '1', 'pcs'),
((SELECT id FROM recipes WHERE title='Korean Bibimbap'), 'gochujang', '20', 'g'),

((SELECT id FROM recipes WHERE title='French Ratatouille'), 'eggplant', '150', 'g'),
((SELECT id FROM recipes WHERE title='French Ratatouille'), 'zucchini', '150', 'g'),
((SELECT id FROM recipes WHERE title='French Ratatouille'), 'tomatoes', '200', 'g'),
((SELECT id FROM recipes WHERE title='French Ratatouille'), 'bell pepper', '100', 'g'),
((SELECT id FROM recipes WHERE title='French Ratatouille'), 'olive oil', '20', 'ml'),

((SELECT id FROM recipes WHERE title='Turkish Adana Kebab'), 'lamb mince', '250', 'g'),
((SELECT id FROM recipes WHERE title='Turkish Adana Kebab'), 'onion', '80', 'g'),
((SELECT id FROM recipes WHERE title='Turkish Adana Kebab'), 'red pepper flakes', '5', 'g'),
((SELECT id FROM recipes WHERE title='Turkish Adana Kebab'), 'flatbread', '1', 'pcs'),

((SELECT id FROM recipes WHERE title='American Grilled Salmon'), 'salmon fillet', '200', 'g'),
((SELECT id FROM recipes WHERE title='American Grilled Salmon'), 'asparagus', '150', 'g'),
((SELECT id FROM recipes WHERE title='American Grilled Salmon'), 'lemon', '1', 'pcs'),
((SELECT id FROM recipes WHERE title='American Grilled Salmon'), 'olive oil', '15', 'ml'),

((SELECT id FROM recipes WHERE title='Kazakh Beshbarmak'), 'beef', '300', 'g'),
((SELECT id FROM recipes WHERE title='Kazakh Beshbarmak'), 'flour', '200', 'g'),
((SELECT id FROM recipes WHERE title='Kazakh Beshbarmak'), 'onion', '150', 'g'),
((SELECT id FROM recipes WHERE title='Kazakh Beshbarmak'), 'egg', '1', 'pcs'),

((SELECT id FROM recipes WHERE title='Russian Beef Stroganoff'), 'beef', '250', 'g'),
((SELECT id FROM recipes WHERE title='Russian Beef Stroganoff'), 'mushrooms', '150', 'g'),
((SELECT id FROM recipes WHERE title='Russian Beef Stroganoff'), 'sour cream', '100', 'ml'),
((SELECT id FROM recipes WHERE title='Russian Beef Stroganoff'), 'egg noodles', '150', 'g'),

((SELECT id FROM recipes WHERE title='Thai Green Curry'), 'chicken breast', '200', 'g'),
((SELECT id FROM recipes WHERE title='Thai Green Curry'), 'coconut milk', '200', 'ml'),
((SELECT id FROM recipes WHERE title='Thai Green Curry'), 'green curry paste', '30', 'g'),
((SELECT id FROM recipes WHERE title='Thai Green Curry'), 'Thai basil', '10', 'g'),

((SELECT id FROM recipes WHERE title='Italian Pasta Carbonara'), 'spaghetti', '200', 'g'),
((SELECT id FROM recipes WHERE title='Italian Pasta Carbonara'), 'guanciale', '100', 'g'),
((SELECT id FROM recipes WHERE title='Italian Pasta Carbonara'), 'egg yolks', '3', 'pcs'),
((SELECT id FROM recipes WHERE title='Italian Pasta Carbonara'), 'pecorino', '50', 'g'),

((SELECT id FROM recipes WHERE title='Japanese Edamame'), 'edamame pods', '200', 'g'),
((SELECT id FROM recipes WHERE title='Japanese Edamame'), 'sea salt', '3', 'g'),

((SELECT id FROM recipes WHERE title='Mexican Guacamole with Chips'), 'avocados', '2', 'pcs'),
((SELECT id FROM recipes WHERE title='Mexican Guacamole with Chips'), 'lime', '1', 'pcs'),
((SELECT id FROM recipes WHERE title='Mexican Guacamole with Chips'), 'tomato', '80', 'g'),
((SELECT id FROM recipes WHERE title='Mexican Guacamole with Chips'), 'tortilla chips', '60', 'g'),

((SELECT id FROM recipes WHERE title='Turkish Hummus'), 'chickpeas', '200', 'g'),
((SELECT id FROM recipes WHERE title='Turkish Hummus'), 'tahini', '30', 'g'),
((SELECT id FROM recipes WHERE title='Turkish Hummus'), 'lemon juice', '20', 'ml'),
((SELECT id FROM recipes WHERE title='Turkish Hummus'), 'garlic', '5', 'g'),

((SELECT id FROM recipes WHERE title='Indian Samosa'), 'potatoes', '200', 'g'),
((SELECT id FROM recipes WHERE title='Indian Samosa'), 'peas', '80', 'g'),
((SELECT id FROM recipes WHERE title='Indian Samosa'), 'flour', '100', 'g'),
((SELECT id FROM recipes WHERE title='Indian Samosa'), 'cumin', '3', 'g'),

((SELECT id FROM recipes WHERE title='Korean Tteokbokki'), 'rice cakes', '250', 'g'),
((SELECT id FROM recipes WHERE title='Korean Tteokbokki'), 'gochujang', '30', 'g'),
((SELECT id FROM recipes WHERE title='Korean Tteokbokki'), 'soy sauce', '15', 'ml'),
((SELECT id FROM recipes WHERE title='Korean Tteokbokki'), 'sugar', '10', 'g'),

((SELECT id FROM recipes WHERE title='Italian Bruschetta'), 'bread', '4', 'slices'),
((SELECT id FROM recipes WHERE title='Italian Bruschetta'), 'tomatoes', '200', 'g'),
((SELECT id FROM recipes WHERE title='Italian Bruschetta'), 'basil', '5', 'g'),
((SELECT id FROM recipes WHERE title='Italian Bruschetta'), 'olive oil', '15', 'ml'),

((SELECT id FROM recipes WHERE title='American Trail Mix'), 'almonds', '40', 'g'),
((SELECT id FROM recipes WHERE title='American Trail Mix'), 'walnuts', '30', 'g'),
((SELECT id FROM recipes WHERE title='American Trail Mix'), 'raisins', '30', 'g'),
((SELECT id FROM recipes WHERE title='American Trail Mix'), 'dark chocolate chips', '20', 'g'),

((SELECT id FROM recipes WHERE title='French Cheese Board'), 'brie', '80', 'g'),
((SELECT id FROM recipes WHERE title='French Cheese Board'), 'camembert', '80', 'g'),
((SELECT id FROM recipes WHERE title='French Cheese Board'), 'grapes', '100', 'g'),
((SELECT id FROM recipes WHERE title='French Cheese Board'), 'crackers', '50', 'g'),

((SELECT id FROM recipes WHERE title='Russian Blini with Sour Cream'), 'flour', '100', 'g'),
((SELECT id FROM recipes WHERE title='Russian Blini with Sour Cream'), 'milk', '150', 'ml'),
((SELECT id FROM recipes WHERE title='Russian Blini with Sour Cream'), 'egg', '1', 'pcs'),
((SELECT id FROM recipes WHERE title='Russian Blini with Sour Cream'), 'sour cream', '50', 'g'),

((SELECT id FROM recipes WHERE title='Kazakh Kurt'), 'yogurt', '500', 'g'),
((SELECT id FROM recipes WHERE title='Kazakh Kurt'), 'salt', '10', 'g'),

((SELECT id FROM recipes WHERE title='Italian Tiramisu'), 'mascarpone', '250', 'g'),
((SELECT id FROM recipes WHERE title='Italian Tiramisu'), 'ladyfingers', '150', 'g'),
((SELECT id FROM recipes WHERE title='Italian Tiramisu'), 'espresso', '100', 'ml'),
((SELECT id FROM recipes WHERE title='Italian Tiramisu'), 'cocoa powder', '10', 'g'),

((SELECT id FROM recipes WHERE title='Japanese Mochi'), 'glutinous rice flour', '150', 'g'),
((SELECT id FROM recipes WHERE title='Japanese Mochi'), 'sugar', '50', 'g'),
((SELECT id FROM recipes WHERE title='Japanese Mochi'), 'red bean paste', '100', 'g'),

((SELECT id FROM recipes WHERE title='French Crème Brûlée'), 'heavy cream', '200', 'ml'),
((SELECT id FROM recipes WHERE title='French Crème Brûlée'), 'egg yolks', '3', 'pcs'),
((SELECT id FROM recipes WHERE title='French Crème Brûlée'), 'sugar', '60', 'g'),
((SELECT id FROM recipes WHERE title='French Crème Brûlée'), 'vanilla bean', '1', 'pcs'),

((SELECT id FROM recipes WHERE title='Turkish Baklava'), 'phyllo dough', '200', 'g'),
((SELECT id FROM recipes WHERE title='Turkish Baklava'), 'walnuts', '150', 'g'),
((SELECT id FROM recipes WHERE title='Turkish Baklava'), 'butter', '100', 'g'),
((SELECT id FROM recipes WHERE title='Turkish Baklava'), 'honey', '80', 'ml'),

((SELECT id FROM recipes WHERE title='Indian Gulab Jamun'), 'milk powder', '200', 'g'),
((SELECT id FROM recipes WHERE title='Indian Gulab Jamun'), 'flour', '30', 'g'),
((SELECT id FROM recipes WHERE title='Indian Gulab Jamun'), 'sugar', '200', 'g'),
((SELECT id FROM recipes WHERE title='Indian Gulab Jamun'), 'rose water', '10', 'ml'),

((SELECT id FROM recipes WHERE title='Korean Hotteok'), 'flour', '200', 'g'),
((SELECT id FROM recipes WHERE title='Korean Hotteok'), 'brown sugar', '80', 'g'),
((SELECT id FROM recipes WHERE title='Korean Hotteok'), 'peanuts', '30', 'g'),
((SELECT id FROM recipes WHERE title='Korean Hotteok'), 'cinnamon', '3', 'g'),

((SELECT id FROM recipes WHERE title='Mexican Churros'), 'flour', '150', 'g'),
((SELECT id FROM recipes WHERE title='Mexican Churros'), 'butter', '50', 'g'),
((SELECT id FROM recipes WHERE title='Mexican Churros'), 'sugar', '40', 'g'),
((SELECT id FROM recipes WHERE title='Mexican Churros'), 'cinnamon', '5', 'g'),

((SELECT id FROM recipes WHERE title='American Chocolate Brownie'), 'dark chocolate', '150', 'g'),
((SELECT id FROM recipes WHERE title='American Chocolate Brownie'), 'butter', '100', 'g'),
((SELECT id FROM recipes WHERE title='American Chocolate Brownie'), 'sugar', '150', 'g'),
((SELECT id FROM recipes WHERE title='American Chocolate Brownie'), 'eggs', '2', 'pcs'),
((SELECT id FROM recipes WHERE title='American Chocolate Brownie'), 'walnuts', '50', 'g'),

((SELECT id FROM recipes WHERE title='Kazakh Chak-Chak'), 'flour', '200', 'g'),
((SELECT id FROM recipes WHERE title='Kazakh Chak-Chak'), 'eggs', '3', 'pcs'),
((SELECT id FROM recipes WHERE title='Kazakh Chak-Chak'), 'honey', '150', 'ml'),

((SELECT id FROM recipes WHERE title='Russian Medovik'), 'flour', '300', 'g'),
((SELECT id FROM recipes WHERE title='Russian Medovik'), 'honey', '80', 'ml'),
((SELECT id FROM recipes WHERE title='Russian Medovik'), 'sour cream', '400', 'g'),
((SELECT id FROM recipes WHERE title='Russian Medovik'), 'butter', '80', 'g');
