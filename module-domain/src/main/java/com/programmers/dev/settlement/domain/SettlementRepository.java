package com.programmers.dev.settlement.domain;

import com.programmers.dev.settlement.query.dto.SettlementView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    List<Settlement> findAllByUserId(Long userId);

    void deleteAllByUserId(Long userId);

    @Query("""
            select new com.programmers.dev.settlement.query.dto.SettlementView(
                s.userId, sum(s.settlementAmount)
            )
            from Settlement s
            group by s.userId
            """)
    List<SettlementView> findSettlementView();
}
