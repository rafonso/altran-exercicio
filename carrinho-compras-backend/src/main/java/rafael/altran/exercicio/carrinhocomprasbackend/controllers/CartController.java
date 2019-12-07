package rafael.altran.exercicio.carrinhocomprasbackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rafael.altran.exercicio.carrinhocomprasbackend.models.Cart;
import rafael.altran.exercicio.carrinhocomprasbackend.models.CartItem;
import rafael.altran.exercicio.carrinhocomprasbackend.models.CartStatus;
import rafael.altran.exercicio.carrinhocomprasbackend.repositories.CartItemRepository;
import rafael.altran.exercicio.carrinhocomprasbackend.repositories.CartRepository;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/shopping/cart")
@CrossOrigin("*")
public class CartController {

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    public CartController(CartRepository cartRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    private void sortCartItems(Cart cart) {
        cart.getCartItems().sort(Comparator.comparing(ci -> ci.getItem().getName()));
    }

    private void validateOpenCart(Cart cart) {
        if (cart.getStatus() == CartStatus.CLOSED) {
            throw new IllegalStateException("Cart is closed");
        }
    }

    private List<CartItem> consolidateItems(List<CartItem> originalItems) {
        /*
         * Implementation Note: I could have used a Set to avoid repeated CartItems. However the CartItem hashCode
         * and Equals are both defined by ID. In a new CartItem it is null. Therefore useless.
         */
        List<CartItem> consolidatedItems = new ArrayList<>();

        for (CartItem cartItem : originalItems) {
            Optional<CartItem> optCartItem = consolidatedItems.stream()
                    .filter(ci -> ci.getItem().equals(cartItem.getItem())).findFirst();
            if (optCartItem.isPresent()) {
                optCartItem.get().setQuantity(optCartItem.get().getQuantity() + cartItem.getQuantity());
            } else {
                consolidatedItems.add(cartItem);
            }
        }

        return consolidatedItems;
    }

    @GetMapping("/")
    public List<Cart> getAll() {
        List<Cart> carts = cartRepository.findAll();
        carts.sort(Comparator.comparing(Cart::getCartValue));

        return carts;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cart> getById(@PathVariable("id") Long id) {
        return cartRepository.findById(id)
                .map(cart -> {
                    sortCartItems(cart);
                    return ResponseEntity.ok().body(cart);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping("/")
    public ResponseEntity<Cart> create(@Valid @RequestBody Cart cart) {
        cart.setId(ControllerUtils.createUniqueId());
        cart.setCartItems(consolidateItems(cart.getCartItems()));
        cart.getCartItems()
                .forEach(cartItem -> {
                    cartItem.setId(ControllerUtils.createUniqueId());
                    cartItemRepository.save(cartItem);
                });

        Cart savedCart = cartRepository.save(cart);

        return this.getById(savedCart.getId());
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Cart> update(@PathVariable("id") Long id, @Valid @RequestBody Cart cart) {
        return cartRepository.findById(id)
                .map(c -> {
                    validateOpenCart(c);
                    // Delete Removed Cart Items
                    c.getCartItems().stream()
                            .filter(ci -> !cart.getCartItems().contains(ci))
                            .forEach(cartItemRepository::delete);
                    c.setCartItems(consolidateItems(cart.getCartItems()));
                    // Fill IDs of new Cart Items
                    c.getCartItems().stream().filter(ci -> Objects.isNull(ci.getId())).forEach(ci -> ci.setId(ControllerUtils.createUniqueId()));

                    cartItemRepository.saveAll(c.getCartItems());
                    cartRepository.save(c);

                    return this.getById(c.getId());
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(value = "/close/{id}")
    public ResponseEntity<Cart> close(@PathVariable("id") Long id) {
        return cartRepository.findById(id)
                .map(c -> {
                    validateOpenCart(c);

                    c.setStatus(CartStatus.CLOSED);
                    cartRepository.save(c);

                    return this.getById(c.getId());
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        return cartRepository.findById(id)
                .map(cart -> {
                    cartRepository.deleteById(id);

                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.notFound().build());
    }

}
