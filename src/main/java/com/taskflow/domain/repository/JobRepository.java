package com.taskflow.domain.repository;

import com.taskflow.domain.model.Job;
import com.taskflow.domain.model.JobStatus;
import com.taskflow.domain.model.JobPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {
    
    List<Job> findByStatus(JobStatus status);
    List<Job> findByStatusAndPriority(JobStatus status, JobPriority priority);
    Optional<Job> findByIdempotencyKey(String idempotencyKey);
    long countByStatus(JobStatus status);

}
