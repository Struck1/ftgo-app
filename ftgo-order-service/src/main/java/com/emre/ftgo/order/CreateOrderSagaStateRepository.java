package com.emre.ftgo.order;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CreateOrderSagaStateRepository extends JpaRepository<CreateOrderSagaState, UUID> {
}
