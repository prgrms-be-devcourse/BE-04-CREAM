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
            "SELECT P.ID, P.SIZE, MIN(SB.PRICE) AS MIN_PRICE, SB.STATUS " +
                    "FROM PRODUCTS P " +
                    "LEFT JOIN SELL_BIDDINGS SB " +
                    "ON P.ID = SB.PRODUCT_ID " +
                    "WHERE P.PRODUCT_NAME = :productName AND P.BRAND_ID = :brandId AND (SB.STATUS = 'LIVE' OR SB.STATUS IS NULL)" +
                    "GROUP BY P.SIZE " +
                    "ORDER BY P.SIZE ";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcPurchaseSelectViewDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final static RowMapper<BiddingSelectLine> biddingSelectLineRowMapper = (resultSet, i) -> {
        String size = resultSet.getString("SIZE");
        String productId = resultSet.getString("ID");

        Optional<String> status = Optional.ofNullable(resultSet.getString("STATUS"));

        if (status.isEmpty()) {
            return new BiddingSelectLine(false, size, productId, "-");
        }

        String minPrice = String.valueOf(resultSet.getLong("MIN_PRICE"));

        return new BiddingSelectLine(true, size, productId, minPrice);
    };

    @Override
    public List<BiddingSelectLine> getPurchaseView(String productName, Long brandId) {
        MapSqlParameterSource source = new MapSqlParameterSource()
                .addValue("productName", productName)
                .addValue("brandId", brandId);
        List<BiddingSelectLine> selectLines = jdbcTemplate.query(PURCHASE_SELECT_VIEW_QUERY, source, biddingSelectLineRowMapper);

        if (selectLines.isEmpty()) {
            throw new EntityNotFoundException("존재하지 않는 상품 이름입니다." + productName);
        }

        return selectLines;
    }
}
