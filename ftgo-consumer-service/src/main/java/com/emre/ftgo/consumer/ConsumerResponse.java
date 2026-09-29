package com.emre.ftgo.consumer;

public record ConsumerResponse(Long id, String firstName, String lastName) {
    public static ConsumerResponse from(Consumer consumer) {
        return  new ConsumerResponse(consumer.getId(), consumer.getFirstName(), consumer.getLastName());
    }
}
