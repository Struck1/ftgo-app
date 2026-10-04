package com.emre.ftgo.order;

import com.emre.ftgo.common.outbox.OutboxService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CreateOrderSagaStateRepository sagaStateRepository;
    private final OutboxService outboxService;

    public OrderService(OrderRepository orderRepository,
                        CreateOrderSagaStateRepository sagaStateRepository,
                        OutboxService outboxService) {
        this.orderRepository = orderRepository;
        this.sagaStateRepository = sagaStateRepository;
        this.outboxService = outboxService;
    }

    @Transactional
    public Order createOrder(Long consumerId, Long restaurantId, BigDecimal totalAmount) {
        Order order = orderRepository.save(new Order(consumerId, restaurantId, totalAmount));

        outboxService.publish(
                "order-events",
                "Order",
                String.valueOf(order.getId()),
                "OrderCreated",
                new OrderCreatedEvent(order.getId(), order.getConsumerId(), order.getRestaurantId(), order.getTotalAmount()));

        // Start the Create Order saga: persist its state and send the first command to Kitchen
        CreateOrderSagaState saga = sagaStateRepository.save(new CreateOrderSagaState(order.getId()));

        outboxService.publish(
                "kitchen-commands",
                "CreateOrderSaga",
                saga.getSagaId().toString(),
                "CreateTicket",
                new CreateTicketCommand(saga.getSagaId(), order.getId(), order.getRestaurantId()));

        return order;
    }
}