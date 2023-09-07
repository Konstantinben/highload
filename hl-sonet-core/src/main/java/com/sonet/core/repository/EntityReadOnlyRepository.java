package com.sonet.core.repository;

import com.sonet.core.configuration.jdbc.ReadOnlyRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
@ReadOnlyRepository
public interface EntityReadOnlyRepository<T, ID> extends Repository<T, ID> {
    Optional<T> findById(ID uuid);
    Optional<T> findByUuid(UUID uuid);
}