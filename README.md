# Stitch & Spring E-Commerce API (Capstone Project)

## 📖 Overview
This project is a RESTful API for an e-commerce platform, completed as part of the Year Up United Application Development curriculum (Java Focus Academy).

**Full transparency:** I did not build this entire application from scratch. I was provided with a starter codebase containing basic models and database scripts. My primary role was to navigate the existing architecture, troubleshoot and fix lingering bugs, and architect the service and controller layers for several core features to make the application fully functional.

## 🚀 Technologies Used
* **Language:** Java
* **Framework:** Spring Boot (Spring Web, Spring Security, Spring Data JPA)
* **Database:** MySQL
* **Tools:** Postman / Insomnia (API Testing), Git/GitHub

## ✨ Features Implemented
Here are the specific phases and features I contributed to the codebase:

* **Category Management (Phase 1):** Built the REST controllers and service logic to handle full CRUD operations for product categories, secured with role-based access (`ADMIN` only).
* **Debugging & Logic Fixes (Phase 2):** * Diagnosed and fixed a bug in the `ProductService` search logic that was incorrectly filtering out valid products.
    * Resolved a database mapping issue where inventory stock updates were returning a `200 OK` but failing to save to the database.
* **Shopping Cart System (Phase 3):** Engineered a stateful shopping cart feature tied to authenticated users. Implemented endpoints to add items, update quantities, view the cart with calculated line-totals, and clear the cart.
* **User Profiles (Phase 4):** Created secure endpoints utilizing JWT `Principal` data to allow users to view and update their personal shipping profiles without exposing their data to other users.
* **Checkout & Order Processing (Phase 5):** Architected a transactional checkout flow that converts a user's active shopping cart into a finalized `Order`.

## 🛠️ Setup and Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yu26s9-sky0-0glitch/Stitch-Spring
   cd Stitch-Spring
   ```
2. **Database Configuration:**
    * Open MySQL Workbench and run the provided database creation and seed scripts.
    * Update your `application.properties` with your local MySQL username and password.
3. **Build and Run:**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```
   *The API will run on `http://localhost:8080`*

## 🔍 Code Showcase: Transactional Checkout Logic

The feature I am most proud of engineering is the Order Checkout process. Converting a shopping cart into a finalized order requires interacting with multiple services and database tables simultaneously.

To prevent data corruption (e.g., charging a user but failing to generate the items, or crashing before the cart clears), I utilized Spring's `@Transactional` annotation. If any step in this method fails, the entire database transaction rolls back safely.

```java
@Transactional
public Order createOrder(int userId) {
    // 1. Fetch the user's cart
    ShoppingCart cart = shoppingCartService.getByUserId(userId);

    if (cart == null || cart.getItems().isEmpty()) {
        throw new RuntimeException("Cannot create an order from an empty cart.");
    }

    // 2. Fetch the profile for shipping details
    Profile profile = profileService.getById(userId);

    // 3. Build and save the main Order record
    Order newOrder = new Order();
    newOrder.setUserId(userId);
    newOrder.setDate(LocalDateTime.now());
    newOrder.setShippingAmount(0.0);
    
    if (profile != null) {
        newOrder.setAddress(profile.getAddress());
        newOrder.setCity(profile.getCity());
        newOrder.setState(profile.getState());
        newOrder.setZip(profile.getZip());
    }

    Order savedOrder = orderRepository.save(newOrder);

    // 4. Convert Cart Items to Order Line Items
    for (Map.Entry<Integer, ShoppingCartItem> entry : cart.getItems().entrySet()) {
        ShoppingCartItem cartItem = entry.getValue();
        Product product = cartItem.getProduct();

        OrderLineItem lineItem = new OrderLineItem();
        lineItem.setOrderId(savedOrder.getOrderId());
        lineItem.setProductId(product.getProductId());
        lineItem.setQuantity(cartItem.getQuantity());
        lineItem.setSalesPrice(product.getPrice());
        lineItem.setDiscount(cartItem.getDiscountPercent());

        orderLineItemRepository.save(lineItem);
    }

    // 5. Clear the cart upon success
    shoppingCartService.clearCart(userId);

    return savedOrder;
}
```
### Checkout Architecture & Data Flow

Converting a shopping cart into an order involves linking multiple database tables (Users, Profiles, Carts, Orders, and Line Items) in a single, safe transaction.

When a user clicks "Checkout" (sending a `POST` request to `/orders`), the `@Transactional` method executes these exact steps:

1.  **Validate the Cart:** Checks the user's shopping cart. If it is empty, the server throws a `400 Bad Request` and stops the process.
2.  **Fetch Shipping Details:** Retrieves the user's `Profile` to grab their current address, city, state, and zip code.
3.  **Generate the Order:** Creates a brand new `Order` record populated with the user's ID, today's date, and the shipping address.
4.  **Convert the Items:** Loops through every item in the cart. For each item, it creates a permanent `OrderLineItem` linked to the new Order, locking in the specific product and price at that exact moment.
5.  **Clear the Cart:** Deletes all items from the user's active shopping cart now that they are secured in an official order.
6.  **Return Success:** Returns a `201 Created` status along with the finalized JSON order data for the front-end receipt.


---
*Developed by Sahar Omer*