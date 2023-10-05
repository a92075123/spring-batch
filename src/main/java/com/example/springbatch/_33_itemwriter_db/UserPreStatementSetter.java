package com.example.springbatch._33_itemwriter_db;

import org.springframework.batch.item.database.ItemPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

//用於設置SQL中佔位符的參數
public class UserPreStatementSetter implements ItemPreparedStatementSetter<User> {
    @Override
    public void setValues(User user, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setLong(1,user.getId());
        preparedStatement.setString(2, user.getName());
        preparedStatement.setInt(3,user.getAge());

    }
}
