package com.carddemo.repository;

import com.carddemo.entity.TranCatBalance;
import com.carddemo.entity.TranCatBalanceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TranCatBalanceRepository extends JpaRepository<TranCatBalance, TranCatBalanceId> {

    List<TranCatBalance> findByAcctId(Long acctId);

    List<TranCatBalance> findAllByOrderByAcctIdAscTranTypeCdAscTranCatCdAsc();
}
