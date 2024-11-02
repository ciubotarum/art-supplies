-- Create the Product table if it doesn't already exist
CREATE TABLE IF NOT EXISTS products
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    price       DECIMAL(10, 2),
    quantity    INT,
    category    VARCHAR(50),
    image       VARCHAR(255)
);

-- Insert sample data
INSERT INTO products(name, description, price, quantity, category, image)
VALUES ('Acrylic Paint Set', 'Set of 12 vibrant colors', 25.99, 100, 'Paints',
        'https://example.com/images/acrylic_paint_set.jpg'),
       ('Watercolor Brushes', 'Set of 10 high-quality watercolor brushes', 15.50, 50, 'Brushes',
        'https://example.com/images/watercolor_brushes.jpg'),
       ('Canvas Pack', 'Pack of 5 stretched canvases', 30.00, 75, 'Canvases',
        'https://example.com/images/canvas_pack.jpg'),
       ('Oil Paint Set', 'Complete set of 18 oil paints', 35.99, 40, 'Paints',
        'https://example.com/images/oil_paint_set.jpg'),
       ('Sketch Pad', '50 sheets of high-quality paper', 8.99, 200, 'Paper',
        'https://example.com/images/sketch_pad.jpg');
