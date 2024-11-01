-- Create the users table
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       full_name VARCHAR(100) NOT NULL,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       phone VARCHAR(15) NOT NULL
);

-- Insert sample data into the users table
INSERT INTO users (full_name, email, phone) VALUES
                                                ('John Doe', 'john.doe@example.com', '123-456-7890'),
                                                ('Jane Smith', 'jane.smith@example.com', '234-567-8901'),
                                                ('Alice Johnson', 'alice.johnson@example.com', '345-678-9012'),
                                                ('Bob Brown', 'bob.brown@example.com', '456-789-0123'),
                                                ('Charlie Davis', 'charlie.davis@example.com', '567-890-1234');
