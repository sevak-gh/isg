package com.infotech.isg.repository.jdbc;

import com.infotech.isg.domain.Operator;
import com.infotech.isg.repository.OperatorRepository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.dao.EmptyResultDataAccessException;


/**
* jdbc implementation for Operator repository.
*
* @author Sevak Gharibian
*/
@Repository("JdbcOperatorRepository")
public class JdbcOperatorRepositoryImpl implements OperatorRepository {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Operator findById(int id) {
        Operator operator = null;
        String sql = "select id, name, status from info_topup_operators where id = ?";
        try {
            operator = jdbcTemplate.queryForObject(sql, new Object[] {id}, new OperatorRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

        return operator;
    }

    private static final class OperatorRowMapper implements RowMapper<Operator> {
        @Override
        public Operator mapRow(ResultSet rs, int rowNum) throws SQLException {
            Operator operator = new Operator();
            operator.setId(rs.getInt("id"));
            operator.setName(rs.getString("name"));
            operator.setIsActive(((rs.getString("status").compareToIgnoreCase("active") == 0) ? true : false));
            return operator;
        }
    }
}
