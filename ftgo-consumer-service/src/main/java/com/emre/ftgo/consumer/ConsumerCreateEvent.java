package com.emre.ftgo.consumer;

public record ConsumerCreateEvent(Long consumerId, String firstName, String lastName) {
}
