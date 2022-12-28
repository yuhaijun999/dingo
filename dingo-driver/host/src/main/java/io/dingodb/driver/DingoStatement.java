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

package io.dingodb.driver;

import org.apache.calcite.avatica.AvaticaStatement;
import org.apache.calcite.avatica.Meta;

import java.sql.SQLException;
import javax.annotation.Nullable;

public class DingoStatement extends AvaticaStatement {
    DingoStatement(
        DingoConnection connection,
        Meta.StatementHandle handle,
        int resultSetType,
        int resultSetConcurrency,
        int resultSetHoldability
    ) {
        super(
            connection,
            handle,
            resultSetType,
            resultSetConcurrency,
            resultSetHoldability
        );
    }

    @Override
    protected void setSignature(Meta.Signature signature) {
        super.setSignature(signature);
    }

    @Override
    protected void executeInternal(String sql) throws SQLException {
        this.updateCount = -1;
        try {
            // In JDBC, maxRowCount = 0 means no limit; in prepare it means LIMIT 0
            final long maxRowCount1 = maxRowCount <= 0 ? -1 : maxRowCount;
            getConnection().prepareAndExecuteInternal(this, sql, maxRowCount1);
        } catch (Throwable e) {
            throw ExceptionUtils.toSql(e);
        }
    }

    public DingoConnection getConnection() {
        return (DingoConnection) connection;
    }

    public void clear() {
        if (openResultSet != null) {
            openResultSet.close();
            openResultSet = null;
        }
    }

    public void createResultSet(@Nullable Meta.Frame firstFrame) throws SQLException {
        Meta.Signature signature = getSignature();
        openResultSet = ((DingoConnection) connection).newResultSet(
            this,
            signature,
            firstFrame,
            signature.sql
        );
    }
}