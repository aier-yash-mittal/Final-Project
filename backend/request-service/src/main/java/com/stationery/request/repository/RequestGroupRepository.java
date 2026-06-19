package com.stationery.request.repository;

import com.stationery.request.entity.RequestGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestGroupRepository extends JpaRepository<RequestGroup, Long> {
}
