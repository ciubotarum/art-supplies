-- Create the users table
CREATE TABLE users
(
    user_id   SERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email     VARCHAR(100) UNIQUE NOT NULL,
    phone     VARCHAR(15) NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password  VARCHAR(100) NOT NULL,
    role      VARCHAR(20) CHECK ( role IN ('customer', 'admin')) NOT NULL
);

CREATE TABLE categories
(
    category_id   SERIAL PRIMARY KEY,
    category_name VARCHAR(50) UNIQUE NOT NULL
);

-- Create the Product table
CREATE TABLE products
(
    product_id SERIAL PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2),
    quantity INT,
    category_id INT REFERENCES categories(category_id),
    image VARCHAR(255)
);

CREATE TABLE orders
(
    order_id     SERIAL PRIMARY KEY,
    user_id      INT REFERENCES users(user_id),
    order_date   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status       VARCHAR(20) CHECK (status IN ('pending', 'completed', 'shipped')) NOT NULL,
    total_amount DECIMAL(10, 2)                                                    NOT NULL
);

CREATE TABLE order_items
(
    id         SERIAL PRIMARY KEY,
    order_id   INT REFERENCES orders (order_id) ON DELETE CASCADE,
    product_id INT REFERENCES products (product_id),
    quantity   INT            NOT NULL,
    price      DECIMAL(10, 2) NOT NULL
);

-- CREATE TABLE cart_items
-- (
--     cart_id    SERIAL PRIMARY KEY,
--     user_id    INT REFERENCES users (user_id) ON DELETE CASCADE,
--     product_id INT REFERENCES products (product_id),
--     quantity   INT NOT NULL
-- );

CREATE TABLE ratings
(
    rating_id  SERIAL PRIMARY KEY,
    user_id    INT REFERENCES users (user_id) ON DELETE CASCADE,
    product_id INT REFERENCES products (product_id),
    rating     INT CHECK (rating BETWEEN 1 AND 5) NOT NULL
);

-- Create the reviews table
CREATE TABLE reviews
(
    review_id SERIAL PRIMARY KEY,
    product_id INT REFERENCES products (product_id),
    user_id INT REFERENCES users (user_id) ON DELETE CASCADE,
    review_text TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
--     FOREIGN KEY (product_id) REFERENCES products (product_id) ON DELETE CASCADE,
--     FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

-- CREATE TABLE payments
-- (
--     payment_id SERIAL PRIMARY KEY,
--     order_id INT REFERENCES orders (order_id) ON DELETE CASCADE,
--     payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     amount DECIMAL(10, 2) NOT NULL,
--     payment_method VARCHAR(20) CHECK (payment_method IN ('credit_card', 'debit_card', 'paypal')) NOT NULL
-- );

-- Insert sample data into the reviews table
-- INSERT INTO reviews (product_id, user_id, rating, review_text)
-- VALUES (1, 1, 5, 'Amazing quality! Highly recommend.'),
--        (2, 2, 4, 'Good product but a bit pricey.'),
--        (3, 3, 3, 'Average experience. Could be better.'),
--        (4, 1, 5, 'Perfect for my needs. Excellent quality.'),
--        (5, 2, 2, 'Not as expected. Quality could be improved.');

-- Insert sample data into the users table
-- INSERT INTO users (full_name, email, phone)
-- VALUES ('John Doe', 'john.doe@example.com', '123-456-7890'),
--        ('Jane Smith', 'jane.smith@example.com', '234-567-8901'),
--        ('Alice Johnson', 'alice.johnson@example.com', '345-678-9012'),
--        ('Bob Brown', 'bob.brown@example.com', '456-789-0123'),
--        ('Charlie Davis', 'charlie.davis@example.com', '567-890-1234');
