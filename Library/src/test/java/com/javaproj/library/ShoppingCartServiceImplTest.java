package com.javaproj.library;

import com.javaproj.library.dto.ProductDto;
import com.javaproj.library.model.CartItem;
import com.javaproj.library.model.Customer;
import com.javaproj.library.model.ShoppingCart;
import com.javaproj.library.repository.CartItemRepository;
import com.javaproj.library.repository.ShoppingCartRepository;
import com.javaproj.library.service.CustomerService;
import com.javaproj.library.service.impl.ShoppingCartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ShoppingCartServiceImplTest {

    @Mock
    private ShoppingCartRepository cartRepository;

    @Mock
    private CartItemRepository itemRepository;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    void addItemToCart() {
        ProductDto productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setCostPrice(10.0);

        Customer customer = new Customer();
        customer.setId(1L);
        ShoppingCart shoppingCart = new ShoppingCart();
        customer.setCart(shoppingCart);

        when(customerService.findByUsername(anyString())).thenReturn(customer);
        when(cartRepository.save(any(ShoppingCart.class))).thenReturn(shoppingCart);

        ShoppingCart resultCart = shoppingCartService.addItemToCart(productDto, 2, "john.doe");

        assertEquals(shoppingCart, resultCart);
        verify(cartRepository, times(1)).save(any(ShoppingCart.class));
        verify(itemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    void removeItemFromCart() {
        // Arrange
        String username = "john.doe";
        ProductDto productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setCostPrice(20.0);

        Customer customer = new Customer();
        customer.setUsername(username);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setCustomer(customer);

        when(customerService.findByUsername(username)).thenReturn(customer);
        when(cartRepository.save(any(ShoppingCart.class))).thenReturn(shoppingCart);

        ShoppingCart resultCart = shoppingCartService.removeItemFromCart(productDto, username);

        assertNotNull(resultCart);
        assertEquals(0.0, resultCart.getTotalPrice());
        assertEquals(0, resultCart.getTotalItems());
        verify(cartRepository, times(1)).save(any(ShoppingCart.class));
    }

}