package com.programmers.dev.kream.purchasebidding.infra;

import com.programmers.dev.kream.purchasebidding.domain.ProductSelectViewDao;
import com.programmers.dev.kream.purchasebidding.ui.dto.SelectLine;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public class JdbcProductSelectViewDao implements ProductSelectViewDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcProductSelectViewDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static RowMapper<SelectLine> rowMapper = (resultSet, i) -> {
        String size = resultSet.getString("SIZE");
        Optional<String> status = Optional.ofNullable(resultSet.getString("STATUS"));

        if (status.isEmpty()) {
            return new SelectLine(false, size, "-", "-");
        }

        String sizedProductId = resultSet.getString("SIZED_PRODUCT_ID");
        String price = String.valueOf(resultSet.getLong("PRICE"));

        return new SelectLine(true, size, sizedProductId, price);
    };

    @Override
    public List<SelectLine> getPurchaseView(Long ProductId) {
        MapSqlParameterSource source = new MapSqlParameterSource().addValue("productId", ProductId);

        List<SelectLine> selectLines = jdbcTemplate.query(
                "select SIZE, SIZED_PRODUCT_ID, MIN(PRICE) as PRICE, STATUS " +
                        "from SELL_BIDDINGS SB " +
                        "right join SIZED_PRODUCTS SP on SB.SIZED_PRODUCT_ID = SP.ID " +
                        "where SP.PRODUCT_ID = :productId and (STATUS = 'LIVE' or STATUS IS NULL)" +
                        "group by SIZED_PRODUCT_ID, SIZE " +
                        "order by SIZE", source, rowMapper);

        if (selectLines.isEmpty()) {
            return null; //Todo: 존재하지 않는 상품 번호로 검색 예외 구현
        }

        return selectLines;
    }
}
