package org.yearup.service;

import org.springframework.stereotype.Service;
import org.yearup.models.CartItem;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.repository.ShoppingCartRepository;

import java.util.List;

@Service
public class ShoppingCartService
{
    // a shopping cart is built from cart rows plus a product lookup for each row
    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductService productService;

    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository, ProductService productService)
    {
        this.shoppingCartRepository = shoppingCartRepository;
        this.productService = productService;
    }

    public ShoppingCart getByUserId(int userId)
    {
        ShoppingCart cart = new ShoppingCart();

        List<CartItem> cartEntities = shoppingCartRepository.findByUserId(userId);

        for (CartItem entity : cartEntities) {
            Product product = productService.getById(entity.getProductId());

            if (product != null) {
                ShoppingCartItem item = new ShoppingCartItem();
                item.setProduct(product);
                item.setQuantity(entity.getQuantity());


                cart.add(item);
            }
        }

        return cart;
    }
    public void addProductToCart(int userId, int productId) {
        CartItem existingItem = shoppingCartRepository.findByUserIdAndProductId(userId, productId);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + 1);
            shoppingCartRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setUserId(userId);
            newItem.setProductId(productId);
            newItem.setQuantity(1);
            shoppingCartRepository.save(newItem);
        }
    }

    // add additional methods here
    public void updateQuantity(int userId, int productId, int quantity) {

        CartItem existingItem = shoppingCartRepository.findByUserIdAndProductId(userId, productId);

        if (existingItem != null) {

            if (quantity <= 0) {
                shoppingCartRepository.delete(existingItem);
            } else {

                existingItem.setQuantity(quantity);
                shoppingCartRepository.save(existingItem);
            }
        }
    }

    public void clearCart(int userId) {

        List<CartItem> usersCartItems = shoppingCartRepository.findByUserId(userId);

        shoppingCartRepository.deleteAll(usersCartItems);
    }
}
