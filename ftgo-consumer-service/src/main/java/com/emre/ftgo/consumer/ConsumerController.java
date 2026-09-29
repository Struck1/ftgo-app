package com.emre.ftgo.consumer;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

@RestController
@RequestMapping("/consumers")
public class ConsumerController {

    private final  ConsumerService consumerService;
    private final  ConsumerRepository consumerRepository;

    public ConsumerController(ConsumerService consumerService, ConsumerRepository consumerRepository) {
        this.consumerService = consumerService;
        this.consumerRepository = consumerRepository;
    }


    @PostMapping
    public ResponseEntity<ConsumerResponse> create(@Valid @RequestBody CreateConsumerRequest request) {
        Consumer consumer = consumerService.createConsumer(request.firstName(), request.lastName());
        return ResponseEntity
                .created(URI.create("/consumers/" + consumer.getId()))
                .body(ConsumerResponse.from(consumer));
    }

    @GetMapping("/{id}")
    public ConsumerResponse get(@PathVariable Long id) {
        return consumerRepository.findById(id)
                .map(ConsumerResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

}
