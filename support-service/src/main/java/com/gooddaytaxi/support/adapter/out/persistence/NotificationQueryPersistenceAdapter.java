package com.gooddaytaxi.support.adapter.out.persistence;

import com.gooddaytaxi.support.application.port.out.persistence.NotificationQueryPersistencePort;
import com.gooddaytaxi.support.domain.notification.model.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * JPA 기반 알림 리포지토리 구현체
 */
@RequiredArgsConstructor
@Component
public class NotificationQueryPersistenceAdapter implements NotificationQueryPersistencePort {
    private final NotificationJpaRepository notificationJpaRepository;

    /** 알림 조회
    *
    */
    @Override
    public Notification findById(UUID id) {
        return null;
    }

    @Override
    public Notification findByNotificationOriginId(UUID id) {
        return null;
    }

    @Override
    public List<Notification> findAllByNotificiationType(UUID id) {
        return List.of();
    }

    @Override
    public List<Notification> findByNotifiedAtBetween(LocalDateTime start, LocalDateTime end) {
        return List.of();
    }

    @Override
    public List<Notification> findByNotifiedAtAfter(LocalDateTime start) {
        return List.of();
    }

    @Override
    public List<Notification> findByNotifiedAtBefore(LocalDateTime end) {
        return List.of();
    }

    @Override
    public List<Notification> findByIsReadFalseAndNotifiedAtAfter(LocalDateTime start) {
        return List.of();
    }
}


/*

package io.reflectoring.buckpal.account.adapter.out.persistence;

import javax.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import io.reflectoring.buckpal.account.application.port.out.LoadAccountPort;
import io.reflectoring.buckpal.account.application.port.out.UpdateAccountStatePort;
import io.reflectoring.buckpal.account.domain.Account;
import io.reflectoring.buckpal.account.domain.Account.AccountId;
import io.reflectoring.buckpal.account.domain.Activity;
import io.reflectoring.buckpal.common.PersistenceAdapter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@PersistenceAdapter
class AccountPersistenceAdapter implements
		LoadAccountPort,
		UpdateAccountStatePort {

	private final SpringDataAccountRepository accountRepository;
	private final ActivityRepository activityRepository;
	private final AccountMapper accountMapper;

	@Override
	public Account loadAccount(
					AccountId accountId,
					LocalDateTime baselineDate) {

		AccountJpaEntity account =
				accountRepository.findById(accountId.getValue())
						.orElseThrow(EntityNotFoundException::new);

		List<ActivityJpaEntity> activities =
				activityRepository.findByOwnerSince(
						accountId.getValue(),
						baselineDate);

		Long withdrawalBalance = orZero(activityRepository
				.getWithdrawalBalanceUntil(
						accountId.getValue(),
						baselineDate));

		Long depositBalance = orZero(activityRepository
				.getDepositBalanceUntil(
						accountId.getValue(),
						baselineDate));

		return accountMapper.mapToDomainEntity(
				account,
				activities,
				withdrawalBalance,
				depositBalance);

	}

	private Long orZero(Long value){
		return value == null ? 0L : value;
	}


	@Override
	public void updateActivities(Account account) {
		for (Activity activity : account.getActivityWindow().getActivities()) {
			if (activity.getId() == null) {
				activityRepository.save(accountMapper.mapToJpaEntity(activity));
			}
		}
	}

}

*/