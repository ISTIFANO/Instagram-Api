package com.example.dashy_platforms.infrastructure.database.repositeries;

import com.example.dashy_platforms.infrastructure.database.entities.InstagramUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InstagramUserRepository extends JpaRepository<InstagramUserEntity,Long> {
    InstagramUserEntity findById(long id);
    @Query("SELECT u FROM InstagramUserEntity u WHERE u.tokenExpiresAt < :expiryDate")
    List<InstagramUserEntity> findTokensExpiringBefore(LocalDateTime expiryDate);
    Optional<InstagramUserEntity> findByInstagramUserId(String instagramUserId);
    boolean existsByInstagramUserId(String instagramUserId);
    @Query("SELECT u FROM InstagramUserEntity u WHERE FUNCTION('MONTH', u.Birthday) = :month AND FUNCTION('DAY', u.Birthday) = :day")
    List<InstagramUserEntity> findByBirthdayMonthAndDay(@Param("month") int month, @Param("day") int day);

}
