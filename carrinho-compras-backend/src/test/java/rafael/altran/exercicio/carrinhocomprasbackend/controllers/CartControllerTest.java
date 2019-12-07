package rafael.altran.exercicio.carrinhocomprasbackend.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.NestedServletException;
import rafael.altran.exercicio.carrinhocomprasbackend.models.*;
import rafael.altran.exercicio.carrinhocomprasbackend.repositories.CartItemRepository;
import rafael.altran.exercicio.carrinhocomprasbackend.repositories.CartRepository;
import rafael.altran.exercicio.carrinhocomprasbackend.repositories.ItemRepository;
import rafael.altran.exercicio.carrinhocomprasbackend.repositories.UserRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CartController.class)
@AutoConfigureDataMongo
@ActiveProfiles("test")
class CartControllerTest {

    private static final String USER_JSON_FORMAT = "{\"id\":%d, \"email\":\"%s\",\"name\":\"%s\"}";
    private static final String ITEM_JSON_FORMAT = "{\"id\":%d, \"name\":\"%s\",\"value\":\"%s\"}";
    private static final String CART_ITEM_JSON_FORMAT = "{\"item\":%s, \"quantity\":%d}";
    private static final String CART_JSON_FORMAT = "{\"user\":%s, \"cartItems\":[%s]}";

    private static final String URL = "/shopping/cart/";

    private List<User> storedUsers = Arrays.asList(
            new User(10L, "steve.rogers@avengers.com", "Captain America"),
            new User(20L, "peter.parker@marvel.com", "Spider Man"),
            new User(30L, "arnold.schwarznegger@rambo.com", "Terminator")
    );

    private final Item mobile150 = new Item(100L, "Mobile", BigDecimal.valueOf(150));
    private final Item ringIs250 = new Item(200L, "Ring", BigDecimal.valueOf(250));
    private final Item bluray350 = new Item(300L, "Bluray", BigDecimal.valueOf(350));

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String userToJson(User user) {
        return String.format(USER_JSON_FORMAT, user.getId(), user.getEmail(), user.getName());
    }

    private String itemToJson(Item item) {
        return String.format(ITEM_JSON_FORMAT, item.getId(), item.getName(), item.getValue());
    }

    private String cartItemToJson(CartItem cartItem) {
        return String.format(CART_ITEM_JSON_FORMAT, itemToJson(cartItem.getItem()), cartItem.getQuantity());
    }

    private String cartToJson(Cart cart) {
        return String.format(CART_JSON_FORMAT, userToJson(cart.getUser()),
                cart.getCartItems().stream().map(this::cartItemToJson).collect(Collectors.joining(","))
        );
    }

    private void testCreateNotRegisteredUser(User notRegistered, String json) throws Exception {
        MvcResult result = mvc.perform(post(URL).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        Cart newCart = objectMapper.readValue(contentAsString, Cart.class);

        assertTrue(newCart.getId() > 0);
        assertEquals(notRegistered, newCart.getUser());
        assertTrue(newCart.getCartItems().isEmpty());
        assertEquals(CartStatus.OPEN, newCart.getStatus());
        assertEquals(BigDecimal.ZERO, newCart.getCartValue());
        assertTrue(userRepository.findById(notRegistered.getId()).isPresent());
    }

    private void testCreateWithIrregularQuantity(Integer quantity) throws Exception {
        CartItem cartItem = new CartItem(null, ringIs250, quantity);
        User user = storedUsers.get(0);
        Cart cart = new Cart(null, user, Collections.singletonList(cartItem), null);
        String json = cartToJson(cart);
        System.out.println(json);

        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }


    private void assertUser(Cart newCart, User user, int expectedItemsQuantity) {
        assertTrue(newCart.getId() > 0);
        assertEquals(user, newCart.getUser());
        assertEquals(CartStatus.OPEN, newCart.getStatus());
        assertThat(newCart.getCartItems(), hasSize(expectedItemsQuantity));
    }

    private void assertCartItem(CartItem expectedCartItem, Integer expectedQuantity, CartItem actualCartItem) {
        assertTrue(actualCartItem.getId() > 0);
        assertEquals(expectedCartItem.getItem(), actualCartItem.getItem());
        assertEquals(expectedQuantity, actualCartItem.getQuantity());
        assertEquals(actualCartItem.getItem().getValue().multiply(BigDecimal.valueOf(actualCartItem.getQuantity())),
                actualCartItem.getItemValue());
    }

    private void assertCartItem(CartItem expectedCartItem, CartItem actualCartItem) {
        assertCartItem(expectedCartItem, expectedCartItem.getQuantity(), actualCartItem);
    }


    private void assertCartValue(Cart cart) {
        BigDecimal sumCartValues = cart.getCartItems().stream()
                .map(CartItem::getItemValue).reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(sumCartValues, cart.getCartValue());
    }

    private Cart callPost(String json) throws Exception {
        MvcResult result = mvc.perform(post(URL).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        return objectMapper.readValue(contentAsString, Cart.class);
    }

    private Cart callPut(Cart cart, String json) throws Exception {
        MvcResult result = mvc.perform(put(URL + cart.getId()).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        return objectMapper.readValue(contentAsString, Cart.class);
    }

    private void assertCartClosed(NestedServletException e) {
        assertEquals(e.getCause().getClass(), IllegalStateException.class);
        assertEquals("Cart is closed", e.getCause().getMessage());
    }

    @BeforeEach
    void setUp() {
        storedUsers.forEach(user -> userRepository.save(user));
        Arrays.asList(mobile150, ringIs250, bluray350).forEach(user -> itemRepository.save(user));
    }

    @AfterEach
    void tearDown() {
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    // create() - START

    @Test
    @DisplayName("Create With empty string")
    public void createWithNullJson() throws Exception {
        String json = "";

        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Create With Empty JSON")
    public void createWithEmptyJson() throws Exception {
        String json = "{}";

        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Create With Empty Properties")
    public void createWithJsonEmptyProperties() throws Exception {
        String json = String.format(CART_JSON_FORMAT, "", "");

        mvc.perform(post(URL).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Create With Null User")
    public void createWithNullUser() throws Exception {
        String json = "{\"user\":null}";

        mvc.perform(post(URL).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Create With User with null ID")
    public void createWithUserWithNullId() {
        Long nullId = null;
        String json = "{\"user\":" + String.format(USER_JSON_FORMAT, nullId, "", "") + "}";

        assertThrows(NestedServletException.class, () ->
                mvc.perform(post(URL).content(json).contentType(MediaType.APPLICATION_JSON))
        );
    }

    @Test
    @DisplayName("Create With Not registered User and no items")
    public void createWithNotRegisteredUserNoItems() throws Exception {
        User notRegistered = new User(10L, "who.is@this.user", "Who is this user");
        String jsonUser = userToJson(notRegistered);
        String json = "{\"user\":" + jsonUser + "}";

        testCreateNotRegisteredUser(notRegistered, json);
    }

    @Test
    @DisplayName("Create With Not registered User and null items")
    public void createWithNotRegisteredUserAndNullItems() throws Exception {
        User notRegistered = new User(10L, "who.is@this.user", "Who is this user");
        String jsonUser = userToJson(notRegistered);
        String json = "{\"user\":" + jsonUser + ", \"items\": null}";

        testCreateNotRegisteredUser(notRegistered, json);
    }

    @Test
    @DisplayName("Create With Not registered User and empty items")
    public void createWithNotRegisteredUserAndEmptyItems() throws Exception {
        User notRegistered = new User(10L, "who.is@this.user", "Who is this user");
        String jsonUser = userToJson(notRegistered);
        String json = "{\"user\":" + jsonUser + ", \"items\": []}";

        testCreateNotRegisteredUser(notRegistered, json);
    }

    @Test
    @DisplayName("Create With registered User and a item with no quantity")
    public void createNotRegisteredUser1ItemWithNoQuantity() throws Exception {
        testCreateWithIrregularQuantity(null);
    }

    @Test
    @DisplayName("Create With registered User and a item with negative quantity")
    public void createNotRegisteredUser1ItemWithNegativeQuantity() throws Exception {
        testCreateWithIrregularQuantity(-3);
    }

    @Test
    @DisplayName("Create With registered User and a item with 0 quantity")
    public void createNotRegisteredUser1ItemWith0Quantity() throws Exception {
        testCreateWithIrregularQuantity(0);
    }

    @Test
    @DisplayName("Create With registered User and 1 regular Item")
    public void createWith1Item() throws Exception {
        CartItem cartItemA = new CartItem(null, ringIs250, 2);
        User user = storedUsers.get(0);
        Cart cart = new Cart(null, user, Collections.singletonList(cartItemA), null);
        String json = cartToJson(cart);

        Cart newCart = callPost(json);

        assertUser(newCart, user, 1);
        assertCartItem(cartItemA, newCart.getCartItems().get(0));
        assertCartValue(newCart);
    }

    @Test
    @DisplayName("Create With registered User and 3 regular Items (should be returned in alphabetical order of item)")
    public void createWith3Items() throws Exception {
        CartItem cartItemA = new CartItem(null, mobile150, 3); // Mobile
        CartItem cartItemB = new CartItem(null, ringIs250, 2); // Ring
        CartItem cartItemC = new CartItem(null, bluray350, 1); // Bluray
        User user = storedUsers.get(0);
        Cart cart = new Cart(null, user, Arrays.asList(cartItemA, cartItemB, cartItemC), null);
        String json = cartToJson(cart);

        Cart newCart = callPost(json);

        assertUser(newCart, user, 3);
        assertCartItem(cartItemC, newCart.getCartItems().get(0));
        assertCartItem(cartItemA, newCart.getCartItems().get(1));
        assertCartItem(cartItemB, newCart.getCartItems().get(2));
        assertCartValue(newCart);
    }

    @Test
    @DisplayName("Create With registered User and repeated regular Items (should be returned in alphabetical order of item)")
    public void createWithRepeatedItems() throws Exception {
        CartItem cartItemA = new CartItem(null, mobile150, 3); // Mobile
        CartItem cartItemB = new CartItem(null, ringIs250, 2); // Ring
        CartItem cartItemC = new CartItem(null, bluray350, 1); // Bluray
        CartItem cartItemD = new CartItem(null, ringIs250, 3); // Ring
        CartItem cartItemE = new CartItem(null, mobile150, 1); // Mobile

        User user = storedUsers.get(0);
        Cart cart = new Cart(null, user, Arrays.asList(cartItemA, cartItemB, cartItemC, cartItemD, cartItemE), null);
        String json = cartToJson(cart);

        Cart newCart = callPost(json);

        assertUser(newCart, user, 3);
        assertCartItem(cartItemC, newCart.getCartItems().get(0));
        assertCartItem(cartItemA, cartItemA.getQuantity() + cartItemE.getQuantity(), newCart.getCartItems().get(1));
        assertCartItem(cartItemB, cartItemB.getQuantity() + cartItemD.getQuantity(), newCart.getCartItems().get(2));
        assertCartValue(newCart);
    }

    // create() - END

    // getAll() - START

    @Test
    @DisplayName("Empty cart Repository")
    public void getAllEmptyRepository() throws Exception {
        MvcResult result = mvc.perform(get(URL).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        assertEquals("[]", contentAsString);
    }

    @Test
    @DisplayName("Carts sorted by value")
    public void getAllSortedByValue() throws Exception {
        Cart cartA = new Cart(65L, storedUsers.get(0), Collections.singletonList(
                new CartItem(650L, bluray350, 2)
        ), CartStatus.OPEN); // Bluray -> $ 700
        Cart cartB = new Cart(66L, storedUsers.get(1), Collections.singletonList(
                new CartItem(660L, mobile150, 6)
        ), CartStatus.OPEN); // Mobile -> $ 900
        Cart cartC = new Cart(67L, storedUsers.get(2), Collections.singletonList(
                new CartItem(670L, ringIs250, 2)
        ), CartStatus.OPEN); // Ring -> $ 500
        cartItemRepository.saveAll(cartA.getCartItems());
        cartItemRepository.saveAll(cartB.getCartItems());
        cartItemRepository.saveAll(cartC.getCartItems());
        cartRepository.saveAll(Arrays.asList(cartA, cartB, cartC));

        MvcResult result = mvc.perform(get(URL).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        List<Cart> carts = objectMapper.readValue(contentAsString, new TypeReference<List<Cart>>() {
        });

        assertThat(carts, hasSize(3));
        assertEquals(cartC, carts.get(0));
        assertEquals(cartA, carts.get(1));
        assertEquals(cartB, carts.get(2));
    }

    // getAll() - END

    // getById() - START

    @Test
    @DisplayName("Find By Id With ID not Present")
    public void getByIdNotPresent() throws Exception {
        long id = 10L;

        mvc.perform(get(URL + id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Find By Id With ID Present")
    public void getByIdPresent() throws Exception {
        Cart cartA = new Cart(65L, storedUsers.get(0), Arrays.asList(
                new CartItem(660L, mobile150, 6), // Mobile -> $ 900
                new CartItem(650L, bluray350, 2)// Bluray -> $ 700
        ), CartStatus.OPEN);
        cartItemRepository.saveAll(cartA.getCartItems());
        cartRepository.save(cartA);

        MvcResult result = mvc.perform(get(URL + cartA.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        Cart cart = objectMapper.readValue(contentAsString, Cart.class);

        assertEquals(cartA, cart);
        assertThat(cart.getCartItems(), hasSize(2));
        assertCartItem(cartA.getCartItems().get(1), cart.getCartItems().get(0));
        assertCartItem(cartA.getCartItems().get(0), cart.getCartItems().get(1));
        assertCartValue(cart);
    }

    // getById() - END

    // update - BEGIN

    @Test
    @DisplayName("Update without Id")
    public void updateWithoutUserId() throws Exception {
        String json = String.format(CART_JSON_FORMAT, "", "");

        mvc.perform(put(URL).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Update with Id filled, empty Properties")
    public void updateWithFilledIdEmptyProperties() throws Exception {
        long id = 10L;
        String json = String.format(CART_JSON_FORMAT, "", "");

        mvc.perform(put(URL + id).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Update Closed Cart")
    public void updateClosedCart() throws Exception {
        Cart cartA = new Cart(65L, storedUsers.get(0), Collections.singletonList(
                new CartItem(650L, bluray350, 2)
        ), CartStatus.CLOSED); // Bluray -> $ 700
        cartItemRepository.saveAll(cartA.getCartItems());
        cartRepository.save(cartA);
        String json = cartToJson(cartA);

        try {
            callPut(cartA, json);
        } catch (NestedServletException e) {
            assertCartClosed(e);
        }
    }

    @Test
    @DisplayName("Update Empty cart Adding Items")
    public void updateEmptyCartAddingItems() throws Exception {
        Cart cartA = new Cart(65L, storedUsers.get(0), Collections.emptyList(), CartStatus.OPEN);
        cartRepository.save(cartA);

        Cart cartB = new Cart(cartA.getId(), cartA.getUser(), Collections.singletonList(
                new CartItem(650L, bluray350, 2) // Bluray -> $ 700
        ), cartA.getStatus());
        String json = cartToJson(cartB);

        Cart cart = callPut(cartB, json);

        assertEquals(cartB, cart);
        assertThat(cart.getCartItems(), hasSize(1));
        assertCartItem(cartB.getCartItems().get(0), cart.getCartItems().get(0));
        assertCartValue(cart);
    }

    @Test
    @DisplayName("Update Filled cart removing all items")
    public void updateFilledCartRemovingAllItems() throws Exception {
        Cart cartA = new Cart(65L, storedUsers.get(0), Collections.singletonList(
                new CartItem(650L, bluray350, 2) // Bluray -> $ 700
        ), CartStatus.OPEN);
        cartItemRepository.saveAll(cartA.getCartItems());
        cartRepository.save(cartA);

        Cart cartB = new Cart(cartA.getId(), cartA.getUser(), Collections.emptyList(), cartA.getStatus());
        String json = cartToJson(cartB);

        Cart cart = callPut(cartB, json);

        assertEquals(cartB, cart);
        assertThat(cart.getCartItems(), hasSize(0));
        assertCartValue(cart);
    }

    @Test
    @DisplayName("Update Filled cart changing item quantity")
    public void updateFilledCartChangingItemQuantity() throws Exception {
        Cart cartA = new Cart(65L, storedUsers.get(0), Collections.singletonList(
                new CartItem(650L, bluray350, 2) // Bluray -> $ 700
        ), CartStatus.OPEN);
        cartItemRepository.saveAll(cartA.getCartItems());
        cartRepository.save(cartA);

        Cart cartB = new Cart(cartA.getId(), cartA.getUser(), Collections.singletonList(
                new CartItem(650L, bluray350, 4) // Bluray -> $ 1400
        ), cartA.getStatus());
        String json = cartToJson(cartB);

        Cart cart = callPut(cartB, json);

        assertEquals(cartB, cart);
        assertThat(cart.getCartItems(), hasSize(1));
        assertCartItem(cartB.getCartItems().get(0), cart.getCartItems().get(0));
        assertCartValue(cart);
    }

    @Test
    @DisplayName("Update Filled cart duplicating item ")
    public void updateFilledCartDuplicatingItem() throws Exception {
        Cart cartA = new Cart(65L, storedUsers.get(0), Collections.singletonList(
                new CartItem(650L, bluray350, 2) // Bluray -> $ 700
        ), CartStatus.OPEN);
        cartItemRepository.saveAll(cartA.getCartItems());
        cartRepository.save(cartA);

        Cart cartB = new Cart(cartA.getId(), cartA.getUser(), Arrays.asList(
                new CartItem(650L, bluray350, 2), // Bluray -> $ 700
                new CartItem(null, bluray350, 3) // Bluray -> $ 1050
        ), cartA.getStatus());
        String json = cartToJson(cartB);

        Cart cart = callPut(cartB, json);

        assertEquals(cartB, cart);
        assertThat(cart.getCartItems(), hasSize(1));
        assertCartItem(cartB.getCartItems().get(0), 5, cart.getCartItems().get(0));
        assertCartValue(cart);
    }

    @Test
    @DisplayName("Update Filled cart Replacing item ")
    public void updateFilledCartReplacingItem() throws Exception {
        Cart cartA = new Cart(65L, storedUsers.get(0), Collections.singletonList(
                new CartItem(650L, bluray350, 2) // Bluray -> $ 700
        ), CartStatus.OPEN);
        cartItemRepository.saveAll(cartA.getCartItems());
        cartRepository.save(cartA);

        Cart cartB = new Cart(cartA.getId(), cartA.getUser(), Collections.singletonList(
                new CartItem(null, mobile150, 1) // Mobile --> 150
        ), cartA.getStatus());
        String json = cartToJson(cartB);

        Cart cart = callPut(cartB, json);

        assertEquals(cartB, cart);
        assertThat(cart.getCartItems(), hasSize(1));
        assertCartItem(cartB.getCartItems().get(0), cart.getCartItems().get(0));
        assertCartValue(cart);
    }

    @Test
    @DisplayName("Update Filled cart with 2 items replacing one")
    public void updateFilledCart2ItemsReplacingOne() throws Exception {
        Cart cartA = new Cart(65L, storedUsers.get(0), Arrays.asList(
                new CartItem(330L, ringIs250, 1),
                new CartItem(650L, bluray350, 2) // Bluray -> $ 700
        ), CartStatus.OPEN);
        cartItemRepository.saveAll(cartA.getCartItems());
        cartRepository.save(cartA);

        Cart cartB = new Cart(cartA.getId(), cartA.getUser(), Arrays.asList(
                new CartItem(null, mobile150, 1), // Mobile --> 150
                new CartItem(650L, bluray350, 3)
        ), cartA.getStatus());
        String json = cartToJson(cartB);

        Cart cart = callPut(cartB, json);

        assertEquals(cartB, cart);
        assertThat(cart.getCartItems(), hasSize(2));
        assertCartItem(cartB.getCartItems().get(1), cart.getCartItems().get(0));
        assertCartItem(cartB.getCartItems().get(0), cart.getCartItems().get(1));
        assertCartValue(cart);
    }


    // update - END

    // close - START

    @Test
    @DisplayName("Close with no ID")
    public void closeWithNoId() throws Exception {
        mvc.perform(put(URL + "close/").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Close with null ID")
    public void closeNullId() throws Exception {
        Long id = null;

        mvc.perform(put(URL + "close/" + id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Close with not present ID")
    public void closeNotPresentId() throws Exception {
        long id = 10L;

        mvc.perform(put(URL + "close/" + id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Close with ID Closed")
    public void closePresentIdClosed() throws Exception {
        long id = 10L;
        Cart cart = new Cart(id, storedUsers.get(0), Collections.emptyList(), CartStatus.CLOSED);
        cartRepository.save(cart);

        try {
            mvc.perform(put(URL + "close/" + id).contentType(MediaType.APPLICATION_JSON));
        } catch (NestedServletException e) {
            assertCartClosed(e);
        }
    }

    @Test
    @DisplayName("Close with ID Open")
    public void closePresentIdOpen() throws Exception {
        long id = 10L;
        Cart cart = new Cart(id, storedUsers.get(0), Collections.emptyList(), CartStatus.OPEN);
        cartRepository.save(cart);

        MvcResult result = mvc.perform(put(URL + "close/" + id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        Cart cartClosed = objectMapper.readValue(contentAsString, Cart.class);

        assertEquals(cart.getId(), cartClosed.getId());
        assertEquals(CartStatus.CLOSED, cartClosed.getStatus());
    }

    // close - END

    // delete - START

    @Test
    @DisplayName("Delete with no ID")
    public void deleteWithNoId() throws Exception {
        mvc.perform(delete(URL).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Delete with null ID")
    public void deleteNullId() throws Exception {
        Long id = null;

        mvc.perform(delete(URL  + id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Delete with not present ID")
    public void deleteNotPresentId() throws Exception {
        long id = 10L;

        mvc.perform(delete(URL  + id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Delete with ID present")
    public void deleteIdPresent() throws Exception {
        long id = 10L;
        Cart cart = new Cart(id, storedUsers.get(0), Collections.emptyList(), CartStatus.OPEN);
        cartRepository.save(cart);

        mvc.perform(delete(URL + id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
        assertFalse(cartRepository.existsById(id));
    }

    // delete - END

}
