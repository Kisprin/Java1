package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Modification;
import org.fatmansoft.teach.models.Person;
import org.fatmansoft.teach.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ModificationRepository extends JpaRepository<Modification, Integer> {
    @Query(value = "select max(modificationId) from Modification  ")
    Integer getMaxId();
    @Query(value = "from Modification where ?1=''  ")
    List<Modification> findModificationList(String numName);

    List<Modification> findByPersonNum(String num);
}
