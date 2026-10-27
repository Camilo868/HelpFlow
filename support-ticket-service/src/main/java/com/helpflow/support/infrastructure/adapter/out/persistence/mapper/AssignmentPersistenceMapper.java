package com.helpflow.support.infrastructure.adapter.out.persistence.mapper;

import com.helpflow.support.domain.model.Assignment;
import com.helpflow.support.infrastructure.adapter.out.persistence.entity.AssignmentEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AssignmentPersistenceMapper {

    Assignment toDomain(AssignmentEntity entity);

    AssignmentEntity toEntity(Assignment domain);
}
