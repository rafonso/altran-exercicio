package rafael.altran.exercicio.carrinhocomprasbackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rafael.altran.exercicio.carrinhocomprasbackend.models.*;
import rafael.altran.exercicio.carrinhocomprasbackend.repositories.CartItemRepository;
import rafael.altran.exercicio.carrinhocomprasbackend.repositories.CartRepository;
import rafael.altran.exercicio.carrinhocomprasbackend.repositories.ItemRepository;
import rafael.altran.exercicio.carrinhocomprasbackend.repositories.UserRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ItemController.class)
@AutoConfigureDataMongo
@ActiveProfiles("test")
class ItemControllerTest {

    public static final String URL = "/shopping/item/";

    private static final String JSON_FORMAT = "{\"name\":\"%s\",\"value\":\"%s\"}";

    private final Item mobile = new Item(100L, "Mobile", BigDecimal.valueOf(150));
    private final Item ring = new Item(200L, "Ring", BigDecimal.valueOf(250));
    private final Item bluray = new Item(300L, "Bluray", BigDecimal.valueOf(350));
    private List<Item> storedItems = Arrays.asList(mobile, ring, bluray);

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        storedItems.forEach(user -> itemRepository.save(user));
    }

    @AfterEach
    public void cleanData() {
        cartRepository.deleteAll();
        cartItemRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    // getAll() - START

    @Test
    @DisplayName("Find All sorted by Name")
    public void getAllNoResults() throws Exception {
        mvc.perform(get(URL).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(bluray.getId().intValue())))
                .andExpect(jsonPath("$[1].id", is(mobile.getId().intValue())))
                .andExpect(jsonPath("$[2].id", is(ring.getId().intValue())));
    }

    // getAll() - END

    // getById() - START

    @Test
    @DisplayName("Find By Id With ID not Present")
    public void getByIdNotPresent() throws Exception {
        long id = 80L;

        mvc.perform(get(URL + id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Find By Id With ID Present")
    public void getByIdPresent() throws Exception {
        Long id = ring.getId();

        mvc.perform(get(URL + id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(ring.getId().intValue())))
                .andExpect(jsonPath("$.name", is(ring.getName())))
                .andExpect(jsonPath("$.value", is(ring.getValue().intValue())));
    }

    // getById() - END

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
        String name = "";
        BigDecimal value = null;
        String json = String.format(JSON_FORMAT, name, value);

        mvc.perform(post(URL).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Create With Invalid email, filled Name")
    public void createWithFilledNameInvalidValue() throws Exception {
        String name = "PRODUCT";
        BigDecimal value = BigDecimal.valueOf(-10);
        String json = String.format(JSON_FORMAT, name, value);

        mvc.perform(post(URL).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Create With Repeated Name, Valid Value: No problems")
    public void createWithRepeatedNameValidValue() throws Exception {
        String name = bluray.getName();
        BigDecimal value = BigDecimal.valueOf(10);
        String json = String.format(JSON_FORMAT, name, value);

        MvcResult result = mvc.perform(post(URL).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        Item newItem = objectMapper.readValue(contentAsString, Item.class);

        assertTrue(newItem.getId() > 0);
        assertNotEquals(bluray.getId(), newItem.getId());
        assertEquals(bluray.getName(), newItem.getName());
        assertEquals(newItem.getValue(), newItem.getValue());
    }

    @Test
    @DisplayName("Create With Valid Properties")
    public void createWithJsonValidProperties() throws Exception {
        String name = "PRODUCT";
        BigDecimal value = BigDecimal.valueOf(10);
        String json = String.format(JSON_FORMAT, name, value);

        MvcResult result = mvc.perform(post(URL).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        Item newItem = objectMapper.readValue(contentAsString, Item.class);

        assertTrue(newItem.getId() > 0);
        assertEquals(name, newItem.getName());
        assertEquals(newItem.getValue(), newItem.getValue());
    }

    // create() - END


    // update() - START

    @Test
    @DisplayName("Update without Id")
    public void updateWithoutUserId() throws Exception {
        String name = "";
        BigDecimal value = null;
        String json = String.format(JSON_FORMAT, name, value);

        mvc.perform(put(URL).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Update with Id filled, empty Properties")
    public void updateWithFilledIdEmptyProperties() throws Exception {
        long id = 2L;
        String name = "";
        BigDecimal value = null;
        String json = String.format(JSON_FORMAT, name, value);

        mvc.perform(put(URL + id).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Update with Present User Id filled, invalid Value")
    public void updateWithPresentUserIdInvalidValue() throws Exception {
        Long id = ring.getId();
        String name = "Foo";
        BigDecimal value = BigDecimal.valueOf(-50);
        String json = String.format(JSON_FORMAT, name, value);

        mvc.perform(put(URL + id).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Update with valid Id, valid Properties")
    public void updateWithValidIdChangeNameToNotRegisteredNewValue() throws Exception {
        long id = ring.getId();
        String name = "Foo";
        BigDecimal value = BigDecimal.valueOf(50);
        String json = String.format(JSON_FORMAT, name, value);

        MvcResult result = mvc.perform(put(URL + id).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        Item item = objectMapper.readValue(contentAsString, Item.class);

        assertEquals(ring.getId(), item.getId());
        assertEquals(name, item.getName());
        assertEquals(value, item.getValue());
    }

    @Test
    @DisplayName("Update with valid Id, repeating name from another item")
    public void updateWithValidIdChangeNameToRepeated() throws Exception {
        long id = ring.getId();
        String name = mobile.getName();
        BigDecimal value = BigDecimal.valueOf(50);
        String json = String.format(JSON_FORMAT, name, value);

        MvcResult result = mvc.perform(put(URL + id).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        Item item = objectMapper.readValue(contentAsString, Item.class);

        assertEquals(ring.getId(), item.getId());
        assertEquals(name, mobile.getName());
        assertEquals(value, item.getValue());
    }

    // update() - END

    // delete() - START

    @Test
    @DisplayName("Delete without Id")
    public void deleteWithoutUserId() throws Exception {
        mvc.perform(delete(URL)) // .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Delete Item Not Present")
    public void deleteWithNotPresentId() throws Exception {
        mvc.perform(delete(URL + 2))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Delete Item Present")
    public void deleteWithPresentId() throws Exception {
        mvc.perform(delete(URL + mobile.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        mvc.perform(get(URL + mobile.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete Item associate with cart")
    public void deleteUserWithCartAssociate() throws Exception {
        CartItem ci = cartItemRepository.save(new CartItem(2_000L, mobile, 1));
        User u = userRepository.save(new User(1000L, "user@email.com", "User"));
        cartRepository.save(new Cart(10000L, u, Collections.singletonList(ci), CartStatus.OPEN));

        mvc.perform(delete(URL + mobile.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(ItemController.MSG_ITEM_IN_CARTS));
    }

    // delete() - END

}
