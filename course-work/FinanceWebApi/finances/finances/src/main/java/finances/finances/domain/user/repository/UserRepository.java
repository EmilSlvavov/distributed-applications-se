package finances.finances.domain.user.repository;

import finances.finances.domain.user.entity.User;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);

    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.id = :id")
    void deleteByIdDirect(@Param("id") Integer id);
}
