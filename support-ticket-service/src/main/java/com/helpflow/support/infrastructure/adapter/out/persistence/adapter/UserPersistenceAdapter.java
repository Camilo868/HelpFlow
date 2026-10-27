package com.helpflow.support.infrastructure.adapter.out.persistence.adapter;

import com.helpflow.support.application.port.out.UserRepositoryPort;
import com.helpflow.support.domain.model.User;
import com.helpflow.support.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.helpflow.support.infrastructure.adapter.out.persistence.mapper.UserPersistenceMapper;
import com.helpflow.support.infrastructure.adapter.out.persistence.repository.SpringDataJpaUserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final SpringDataJpaUserRepository userRepository;
    private final UserPersistenceMapper userMapper;

    public UserPersistenceAdapter(SpringDataJpaUserRepository userRepository, UserPersistenceMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public User save(User user) {
        UserEntity entity = userMapper.toEntity(user);
        UserEntity saved = userRepository.save(entity);
        return userMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id).map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByCorporateEmail(String corporateEmail) {
        return userRepository.findByCorporateEmail(corporateEmail).map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByDocument(String document) {
        return userRepository.findByDocument(document).map(userMapper::toDomain);
    }

    @Override
    public boolean existsByCorporateEmail(String corporateEmail) {
        return userRepository.existsByCorporateEmail(corporateEmail);
    }

    @Override
    public boolean existsByDocument(String document) {
        return userRepository.existsByDocument(document);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll().stream().map(userMapper::toDomain).toList();
    }
}
