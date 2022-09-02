/*
 * Copyright 2021 DataCanvas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.dingodb.test.jdbc;

import com.google.common.collect.ImmutableList;
import io.dingodb.test.SqlHelper;
import io.dingodb.test.asserts.AssertResultSet;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestSqlParameters {
    private static SqlHelper sqlHelper;

    @BeforeAll
    public static void setupAll() throws Exception {
        sqlHelper = new SqlHelper();
        sqlHelper.execFile("/table-test-create.sql");
        sqlHelper.execFile("/table-test-data.sql");
    }

    @AfterAll
    public static void cleanUpAll() throws Exception {
        sqlHelper.cleanUp();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "select ?",
        "select 1 + ?",
    })
    public void testIllegalUse(String sql) {
        assertThrows(SQLException.class, () -> {
            try (PreparedStatement statement = sqlHelper.getConnection().prepareStatement(sql)) {
                statement.setInt(1, 0);
            }
        });
    }

    @Test
    public void testFilter() throws SQLException {
        String sql = "select * from test where id < ? and amount > ?";
        Connection connection = sqlHelper.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, 8);
            statement.setDouble(2, 5.0);
            try (ResultSet resultSet = statement.executeQuery()) {
                AssertResultSet.of(resultSet).isRecords(ImmutableList.of(
                    new Object[]{5, "Emily", 5.5},
                    new Object[]{6, "Alice", 6.0},
                    new Object[]{7, "Betty", 6.5}
                ));
            }
            statement.setDouble(2, 6.0);
            try (ResultSet resultSet = statement.executeQuery()) {
                AssertResultSet.of(resultSet).isRecords(ImmutableList.of(
                    new Object[]{7, "Betty", 6.5}
                ));
            }
        }
    }
}