package com.i5e2.likeawesomevegetable.repository;

import com.i5e2.likeawesomevegetable.domain.point.dto.DepositTotalBalanceDto;
import com.i5e2.likeawesomevegetable.domain.point.entity.UserPointDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserPointDepositJpaRepository extends JpaRepository<UserPointDeposit, Long> {
    @Query(value = "select sum(deposit.depositAmount) as depositTotalAmount " +
            "from UserPointDeposit as deposit " +
            "where deposit.userPoint.user.id = :id")
    DepositTotalBalanceDto getDepositTotalBalance(@Param("id") Long id);

    Optional<UserPointDeposit> findByUserPointId(Long userPointId);

}
