package com.dmkcompany.pizza.controller;

import com.dmkcompany.pizza.facade.OrderFacade;
import com.dmkcompany.pizza.model.Cart;
import com.dmkcompany.pizza.model.CartItem;
import com.dmkcompany.pizza.model.Product;
import com.dmkcompany.pizza.service.CartItemService;
import com.dmkcompany.pizza.service.CartService;
import com.dmkcompany.pizza.service.ProductService;
import com.dmkcompany.pizza.service.PizzaSizeService;
import com.dmkcompany.pizza.strategy.DiscountCalculator;
import com.dmkcompany.pizza.template.StandardOrderProcessor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final CartItemService cartItemService;
    private final OrderFacade orderFacade;
    private final ProductService productService;
    private final PizzaSizeService pizzaSizeService;
    private final DiscountCalculator discountCalculator;

    @GetMapping
    public String viewCart(Model model) {
        Cart cart = cartService.getCart();

        if (model.containsAttribute("showOrderSuccess")) {
            return "cart";
        }

        Double totalAmount = cart.getTotalAmount();
        Double discount = discountCalculator.calculateDiscountAmount(cart);
        Double finalAmount = discountCalculator.calculateFinalAmount(cart);

        Double roundedTotalAmount = (double) Math.round(totalAmount);
        Double roundedFinalAmount = (double) Math.round(finalAmount);
        Double roundedDiscount = (double) Math.round(discount);

        model.addAttribute("cart", cart);
        model.addAttribute("totalAmount", roundedTotalAmount);
        model.addAttribute("discount", roundedDiscount);
        model.addAttribute("finalAmount", roundedFinalAmount);
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") Integer quantity,
                            @RequestParam(defaultValue = "medium") String size) {
        Product baseProduct = productService.getProductById(productId);
        CartItem cartItem;

        if (baseProduct != null && baseProduct.getCategoryId().equals(1L)) {
            Product sizedProduct = pizzaSizeService.createSizeVariant(baseProduct, size);

            cartItem = new CartItem(
                    baseProduct.getId(),
                    sizedProduct.getName(),
                    sizedProduct.getBasePrice(),
                    quantity,
                    baseProduct.getImageUrl()
            );
            System.out.println("Cart item created: " + cartItem.getProductName() + ", price: " + cartItem.getPrice());
        }
        else {
            cartItem = cartItemService.createCartItemFromProduct(productId, quantity);
        }
        cartService.addItem(cartItem);

        System.out.println("Item added to cart successfully");
        System.out.println("==================");

        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long productId,
                                 @RequestParam String productName) {
        CartItem cartItem = cartService.getCartItemByProductIdAndName(productId, productName);
        if (cartItem != null) {
            cartService.removeItem(cartItem);
        }
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateQuantity(@RequestParam Long productId,
                                 @RequestParam String productName,
                                 @RequestParam Integer quantity) {
        CartItem cartItem = cartService.getCartItemByProductIdAndName(productId, productName);
        try {
            cartService.updateQuantity(cartItem, quantity);
        } catch (NullPointerException e) {
            System.out.printf("Ошибка: %s", e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart() {
        cartService.clearCart();
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(@RequestParam String address,
                           @RequestParam(required = false) String entrance,
                           @RequestParam(required = false) String floor,
                           @RequestParam(required = false) String flat,
                           @RequestParam String paymentMethod,
                           @RequestParam(required = false) String comment,
                           @RequestParam(required = false) String customerEmail,
                           @RequestParam(required = false) String customerPhone,
                           RedirectAttributes redirectAttributes) {
        try {
            StringBuilder fullAddress = new StringBuilder(address);
            if (entrance != null && !entrance.trim().isEmpty()) {
                fullAddress.append(", подъезд ").append(entrance);
            }
            if (floor != null && !floor.trim().isEmpty()) {
                fullAddress.append(", этаж ").append(floor);
            }
            if (flat != null && !flat.trim().isEmpty()) {
                fullAddress.append(", кв. ").append(flat);
            }

            String paymentMethodReadable = paymentMethod.equals("card") ? "Картой онлайн" : "Наличными при получении";

            var orderResult = orderFacade.placeOrder(
                    fullAddress.toString(),
                    paymentMethodReadable,
                    comment != null ? comment : "",
                    customerEmail,
                    customerPhone
            );

            redirectAttributes.addFlashAttribute("showOrderSuccess", true);
            redirectAttributes.addFlashAttribute("orderNumber", orderResult.orderNumber());
            redirectAttributes.addFlashAttribute("finalAmount", orderResult.finalAmount());
            redirectAttributes.addFlashAttribute("successMessage", orderResult.message());

            return "redirect:/cart?success";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("showOrderError", true);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/cart?error";
        }
    }
}