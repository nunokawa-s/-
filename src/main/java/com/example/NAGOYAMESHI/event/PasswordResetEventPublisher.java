package com.example.NAGOYAMESHI.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.NAGOYAMESHI.entity.User;

@Component
public class PasswordResetEventPublisher {
	private final ApplicationEventPublisher applicationEventPublisher;

	public PasswordResetEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

	public void publishPasswordResetEvent(User user, String requestUrl) {
		applicationEventPublisher.publishEvent(new PasswordResetEvent(this, user, requestUrl));
	}
}
