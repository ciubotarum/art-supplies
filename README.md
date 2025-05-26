[//]: # (# Online Art Supplies Shop)

## Overview
The Online Art Supplies Shop is a web application designed to provide a seamless shopping experience for art enthusiasts. 
Users can register, browse, and purchase a variety of art supplies while having access to features like product ratings
and reviews.

## Business Requirements

1. **User Registration**
    - Customers can register for an account to access personalized features.

2. **User Login**
    - Registered users can log in to their accounts to view order history and manage personal information.

3. **Category filtering**
    - Users can filter the products by categories (e.g., paints, brushes, canvases) to easily find desired items.
   
4. **Product Search**
    - Users can search for specific products by name or keywords to quickly locate items of interest.

5. **Product Details Page**
    - Each product page shows important information, including price, product description, available quantity, and images.

6. **Shopping Cart Functionality**
    - Users can add products to a shopping cart, view total costs.

7. **Order History**
    - Users can view their previous orders, including details about purchased items.

8. **Admin Inventory Management**
    - Administrators can manage inventory by updating stock levels, adding new products, and removing outdated items.

9. **Product Rating System**
    - Users can rate products on a scale (e.g., 1-5 stars) to provide feedback on their purchases.

10. **Product Review System**
     - Customers can leave reviews for products, sharing their experiences and opinions with other users.


## Minimum Viable Product (MVP) Features

### 1. User Registration
Customers can create an account by providing their personal information, such as name, email, and password. This 
feature allows users to access personalized features, such as order history. The registration 
process includes validation to ensure the accuracy and completeness of the provided information.

### 2. Product Details Page
Each product page displays essential information about the product, including its price, detailed description, available
quantity, and images. This feature helps users make informed purchasing decisions by providing all necessary details in 
one place. 

### 3. Order History
Users can view their previous orders, including details about purchased items. This feature allows users to keep track
of their purchases and review past orders for reference.

### 4. Product Search
Users can search for specific products by entering keywords or product names in the search bar. This feature provides 
a list of matching products, allowing users to quickly locate items of interest. 

### 5. Product Rating System
Users can rate products on a scale (e.g., 1-5 stars) to provide feedback on their purchases. This feature allows 
customers to share their satisfaction level with the products they have bought. The ratings are aggregated and 
displayed on the product details page, helping other users make informed purchasing decisions based on the overall 
rating score.

# ER diagram
```mermaid
erDiagram
    users {
        int user_id PK
        varchar email
        varchar full_name
        boolean is_admin
        varchar password
        varchar phone
        varchar username
    }

    categories {
        int category_id PK
        varchar category_name
    }

    products {
        int product_id PK
        varchar description
        varchar image
        numeric price
        varchar product_name
        int quantity
        int category_id FK
        int user_id FK
    }

    carts {
        int cart_id PK
        int user_id FK
        int product_id FK
    }

    cart_items {
        int cart_item_id PK
        int cart_id FK
        int product_id FK
        int quantity
    }

    orders {
        int order_id PK
        timestamp create_date
        numeric total_amount
        int user_id FK
    }

    order_items {
        int order_item_id PK
        int order_id FK
        int product_id FK
        int quantity
        numeric price
    }

    reviews {
        int review_id PK
        timestamp created_at
        varchar review_text
        int product_id FK
        int user_id FK
    }

    ratings {
        int rating_id PK
        int rating
        int product_id FK
        int user_id FK
    }

    users ||--o{ carts : "has"
    users ||--o{ orders : "places"
    users ||--o{ reviews : "writes"
    users ||--o{ ratings : "gives"
    categories ||--o{ products : "contains"
    users ||--o{ products : "adds"
    products ||--o{ carts : "in"
    products ||--o{ cart_items : "has"
    products ||--o{ order_items : "included"
    products ||--o{ reviews : "reviewed in"
    products ||--o{ ratings : "rated in"
    carts ||--o{ cart_items : "has"
    orders ||--o{ order_items : "contains"

```

