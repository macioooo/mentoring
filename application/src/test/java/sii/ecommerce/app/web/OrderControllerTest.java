package sii.ecommerce.app.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import sii.ecommerce.order.api.OrderFacade;
import sii.ecommerce.order.api.dto.CreateOrderCommand;
import sii.ecommerce.order.api.dto.OrderItemDto;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    MockMvc mockMvc;

    @Mock
    OrderFacade orderFacade;

    final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(new OrderController(orderFacade))
                .setValidator(validator)
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void createOrder_delegatesToFacade() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(orderFacade.createOrder(any(CreateOrderCommand.class))).thenReturn(orderId);

        CreateOrderCommand createOrderCommand =
                new CreateOrderCommand(UUID.randomUUID(), List.of(new OrderItemDto(UUID.randomUUID(), 1)));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderCommand)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(orderId.toString()));

        verify(orderFacade).createOrder(any(CreateOrderCommand.class));
    }
}
