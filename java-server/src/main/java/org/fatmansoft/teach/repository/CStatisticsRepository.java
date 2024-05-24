package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.CStatistics;
import org.fatmansoft.teach.models.Clocking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CStatisticsRepository extends JpaRepository<CStatistics,Integer> {

}
