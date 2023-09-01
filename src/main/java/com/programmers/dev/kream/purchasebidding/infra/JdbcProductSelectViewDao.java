package com.programmers.dev.kream.purchasebidding.infra;

import com.programmers.dev.kream.purchasebidding.domain.ProductSelectViewDao;
import com.programmers.dev.kream.purchasebidding.ui.dto.PurchaseSelectView;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;


@Repository
public class JdbcProductSelectViewDao implements ProductSelectViewDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcProductSelectViewDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public PurchaseSelectView getPurchaseView(Long ProductId) {
        //미구현

        return null;
    }
}
