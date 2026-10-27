package com.helpflow.support.infrastructure.adapter.out.persistence.repository;

import com.helpflow.support.infrastructure.adapter.out.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataJpaUserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByCorporateEmail(String corporateEmail);

    Optional<UserEntity> findByDocument(String document);

    boolean existsByCorporateEmail(String corporateEmail);

    boolean existsByDocument(String document);
}
