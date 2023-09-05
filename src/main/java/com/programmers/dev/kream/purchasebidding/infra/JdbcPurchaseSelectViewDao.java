package com.programmers.dev.kream.purchasebidding.infra;

import com.programmers.dev.kream.purchasebidding.domain.PurchaseSelectViewDao;
import com.programmers.dev.kream.purchasebidding.ui.dto.BiddingSelectLine;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public class JdbcPurchaseSelectViewDao implements PurchaseSelectViewDao {

    public static final String PURCHASE_SELECT_VIEW_QUERY =
            "select SIZE, SIZED_PRODUCT_ID, MIN(PRICE) as PRICE, STATUS " +
                    "from SELL_BIDDINGS SB " +
                    "right join SIZED_PRODUCTS SP on SB.SIZED_PRODUCT_ID = SP.ID " +
                    "where SP.PRODUCT_ID = :productId and (STATUS = 'LIVE' or STATUS IS NULL)" +
                    "group by SIZED_PRODUCT_ID, SIZE " +
                    "order by SIZE";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcPurchaseSelectViewDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final static RowMapper<BiddingSelectLine> biddingSelectLineRowMapper = (resultSet, i) -> {
        String size = resultSet.getString("SIZE");
        Optional<String> status = Optional.ofNullable(resultSet.getString("STATUS"));

        if (status.isEmpty()) {
            return new BiddingSelectLine(false, size, "-", "-");
        }

        String sizedProductId = resultSet.getString("SIZED_PRODUCT_ID");
        String price = String.valueOf(resultSet.getLong("PRICE"));

        return new BiddingSelectLine(true, size, sizedProductId, price);
    };

    @Override
    public List<BiddingSelectLine> getPurchaseView(Long productId) {
        MapSqlParameterSource source = new MapSqlParameterSource().addValue("productId", productId);
        List<BiddingSelectLine> selectLines = jdbcTemplate.query(PURCHASE_SELECT_VIEW_QUERY, source, biddingSelectLineRowMapper);

        if (selectLines.isEmpty()) {
            throw new EntityNotFoundException("존재하지 않는 상품 번호입니다." + productId);
        }

        return selectLines;
    }
}
