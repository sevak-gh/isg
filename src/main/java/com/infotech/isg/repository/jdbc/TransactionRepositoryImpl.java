package com.infotech.isg.repository.jdbc;

import com.infotech.isg.domain.Transaction;
import com.infotech.isg.repository.TransactionRepository;

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
* jdbc implementation of Transaction repository
*
* @author Sevak Gharibian
*/
@Repository("TransactionRepositoryJdbc")
public class TransactionRepositoryImpl implements TransactionRepository {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Transaction findByRefNumBankCodeClientId(String refNum, String bankCode, int clientId) {
        Transaction transaction = null;
        String sql = "select id, provider, token, type, state, resnum, refnum, revnum, "
                     + "clientip, amount, channel, consumer, bankcode, client, customerip, "
                     + "trtime, bankverify, verifytime, status, operator, oprcommand, "
                     + "oprresponse, oprtid, operatortime, stf, stfresult, opreverse, "
                     + "bkreverse from info_topup_transactions where refnum = ? and "
                     + "bankcode = ? and client = ?";
        try {
            transaction = jdbcTemplate.queryForObject(sql, new Object[] {refNum, bankCode, clientId}, new TransactionRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

        return transaction;
    }

    @Override
    public void update(Transaction transaction) {
        String sql = "update info_topup_transactions set provider=?, token=?, type=?, "
                     + "state=?, resnum=?, refnum=?, revnum=?, clientip=?, amount=?, "
                     + "channel=?, consumer=?, bankcode=?, client=?, customerip=?, "
                     + "trtime=?, bankverify=?, verifytime=?, status=?, operator=?,"
                     + "oprcommand=?, oprresponse=?, oprtid=?, operatortime=?, stf=?, "
                     + "stfresult=?, opreverse=?, bkreverse=? where id=?";
        jdbcTemplate.update(sql, new Object[] {transaction.getProvider(), transaction.getToken(), transaction.getAction(),
                                               transaction.getState(), transaction.getResNum(), transaction.getRefNum(),
                                               transaction.getRevNum(), transaction.getRemoteIp(), transaction.getAmount(),
                                               transaction.getChannel(), transaction.getConsumer(), transaction.getBankCode(),
                                               transaction.getClientId(), transaction.getCustomerIp(), transaction.getTrDateTime(),
                                               transaction.getBankVerify(), transaction.getVerifyDateTime(), transaction.getStatus(),
                                               transaction.getOperatorResponseCode(), transaction.getOperatorCommand(), transaction.getOperatorResponse(),
                                               transaction.getOperatorTId(), transaction.getOperatorDateTime(), transaction.getStf(),
                                               transaction.getStfResult(), transaction.getOpReverse(), transaction.getBkReverse(),
                                               transaction.getId()
                                              });
    }

    @Override
    public void create(Transaction transaction) {
        String sql = "insert into info_topup_transactions(provider, token, type, state, resnum, refnum, revnum, "
                     + "clientip, amount, channel, consumer, bankcode, client, customerip, "
                     + "trtime, bankverify, verifytime, status, operator, oprcommand, "
                     + "oprresponse, oprtid, operatortime, stf, stfresult, opreverse, bkreverse) values( "
                     + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, new Object[] {transaction.getProvider(), transaction.getToken(), transaction.getAction(),
                                               transaction.getState(), transaction.getResNum(), transaction.getRefNum(),
                                               transaction.getRevNum(), transaction.getRemoteIp(), transaction.getAmount(),
                                               transaction.getChannel(), transaction.getConsumer(), transaction.getBankCode(),
                                               transaction.getClientId(), transaction.getCustomerIp(), transaction.getTrDateTime(),
                                               transaction.getBankVerify(), transaction.getVerifyDateTime(), transaction.getStatus(),
                                               transaction.getOperatorResponseCode(), transaction.getOperatorCommand(), transaction.getOperatorResponse(),
                                               transaction.getOperatorTId(), transaction.getOperatorDateTime(), transaction.getStf(),
                                               transaction.getStfResult(), transaction.getOpReverse(), transaction.getBkReverse()
                                              });
    }

    private static final class TransactionRowMapper implements RowMapper<Transaction> {
        @Override
        public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
            Transaction transaction = new Transaction();
            transaction.setId(rs.getLong("id"));
            transaction.setProvider(rs.getInt("provider"));
            transaction.setToken(rs.getString("token"));
            transaction.setAction(rs.getInt("type"));
            transaction.setState(rs.getString("state"));
            transaction.setResNum(rs.getString("resnum"));
            transaction.setRefNum(rs.getString("refnum"));
            long revNum = rs.getLong("revnum");
            transaction.setRevNum((rs.wasNull()) ? null : new Long(revNum));
            transaction.setRemoteIp(rs.getString("clientip"));
            transaction.setAmount(rs.getLong("amount"));
            transaction.setChannel(rs.getInt("channel"));
            transaction.setConsumer(rs.getString("consumer"));
            transaction.setBankCode(rs.getString("bankcode"));
            transaction.setClientId(rs.getInt("client"));
            transaction.setCustomerIp(rs.getString("customerip"));
            transaction.setTrDateTime(rs.getTimestamp("trtime"));
            int bankVerify = rs.getInt("bankverify");
            transaction.setBankVerify((rs.wasNull()) ? null : new Integer(bankVerify));
            transaction.setVerifyDateTime(rs.getTimestamp("verifytime"));
            int status = rs.getInt("status");
            transaction.setStatus((rs.wasNull()) ? null : new Integer(status));
            int operatorResponseCode = rs.getInt("operator");
            transaction.setOperatorResponseCode((rs.wasNull()) ? null : new Integer(operatorResponseCode));
            transaction.setOperatorCommand(rs.getString("oprcommand"));
            transaction.setOperatorResponse(rs.getString("oprresponse"));
            transaction.setOperatorTId(rs.getString("oprtid"));
            transaction.setOperatorDateTime(rs.getTimestamp("operatortime"));
            int stf = rs.getInt("stf");
            transaction.setStf((rs.wasNull()) ? null : new Integer(stf));
            int stfResult = rs.getInt("stfresult");
            transaction.setStfResult((rs.wasNull()) ? null : new Integer(stfResult));
            int opReverse = rs.getInt("opreverse");
            transaction.setOpReverse((rs.wasNull()) ? null : new Integer(opReverse));
            int bkReverse = rs.getInt("bkreverse");
            transaction.setBkReverse((rs.wasNull()) ? null : new Integer(bkReverse));
            return transaction;
        }
    }
}
