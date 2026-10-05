package com.emre.ftgo.accounting;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CardAuthorizationRepository extends JpaRepository<CardAuthorization, Long> {
}
