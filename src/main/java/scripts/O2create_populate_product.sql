-- Create the Product table if it doesn't already exist
CREATE TABLE IF NOT EXISTS products
(
    product_id          SERIAL PRIMARY KEY,
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
        'https://cdn11.bigcommerce.com/s-1e5d9p00e3/images/stencil/1280x1280/products/15799/108158/liquitex-basics-acrylic-paint-set-of-36-22ml__27395.1706461250.jpg?c=1'),
       ('Watercolor Brushes', 'Set of 10 high-quality watercolor brushes', 15.50, 50, 'Brushes',
        'https://www.daler-rowney.com/global/_product-images/aquafine-watercolour/aquafine-watercolour-brushes/dal_aquafine_bru_group_1_1080px.jpg'),
       ('Canvas Pack', 'Pack of 5 stretched canvases', 30.00, 75, 'Canvases',
        'https://img.kwcdn.com/product/1e133b340fd/4b31321c-af35-49a4-be61-6459838b87c6_1000x1000.jpeg?imageView2/2/w/800/q/70/format/webp'),
       ('Oil Paint Set', 'Complete set of 18 oil paints', 35.99, 40, 'Paints',
        'https://artsup.com.au/cdn/shop/products/Oil18Set.jpg?crop=center&height=2048&v=1633573039&width=2048'),
       ('Sketch Pad', '50 sheets of high-quality paper', 8.99, 200, 'Paper',
        'https://m.media-amazon.com/images/I/61Fr2AQGsJL._AC_SL1000_.jpg');
