package es.juanjsts.users.repositories;

import es.juanjsts.users.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(String username, String email);

    @Modifying
    @Query("UPDATE User p SET p.isDeleted = TRUE WHERE p.id = :id")
    void updateIsDeletedToTrueById(Long id);

    List<User> findAllByIsDeletedFalse();
}
