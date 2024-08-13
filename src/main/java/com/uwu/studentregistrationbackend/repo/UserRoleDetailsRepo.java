package com.uwu.studentregistrationbackend.repo;
import com.uwu.studentregistrationbackend.entity.User;
import com.uwu.studentregistrationbackend.entity.UserRoleDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRoleDetailsRepo extends JpaRepository<UserRoleDetails,Integer> {
    @Query("SELECT urd FROM UserRoleDetails urd WHERE urd.user = :user AND urd.isActive = :isActive AND urd.type = :type")
    Optional<UserRoleDetails> findByUserAndIsActiveAndType(User user, boolean isActive, String type);

    @Query("SELECT urd FROM UserRoleDetails urd WHERE urd.user = :user AND urd.isActive = :isActive AND (urd.type = :type OR urd.type = :type2)")
    Optional<List<UserRoleDetails>> findByUserAndIsActiveAndTypeExom(User user, boolean isActive, String type, String type2);

    @Query("SELECT urd FROM UserRoleDetails urd WHERE urd.user = :user AND urd.isActive = true AND urd.type = 'EXCOM'")
    List<UserRoleDetails> findByExcomByUser(User user);

}
