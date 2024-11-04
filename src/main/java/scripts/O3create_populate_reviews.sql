-- Create the reviews table
CREATE TABLE IF NOT EXISTS reviews
(
    review_id   SERIAL PRIMARY KEY,
    product_id  INT NOT NULL,
    user_id     INT NOT NULL,
    rating      INT CHECK (rating BETWEEN 1 AND 5),
    review_text TEXT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products (product_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

-- Insert sample data into the reviews table
INSERT INTO reviews (product_id, user_id, rating, review_text)
VALUES (1, 1, 5, 'Amazing quality! Highly recommend.'),
       (2, 2, 4, 'Good product but a bit pricey.'),
       (3, 3, 3, 'Average experience. Could be better.'),
       (4, 1, 5, 'Perfect for my needs. Excellent quality.'),
       (5, 2, 2, 'Not as expected. Quality could be improved.');
