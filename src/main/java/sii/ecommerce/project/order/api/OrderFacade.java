package sii.ecommerce.project.order.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sii.ecommerce.project.order.internal.service.OrderService;


@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;

}