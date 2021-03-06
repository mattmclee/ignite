/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.internal.processors.odbc.odbc;

import org.apache.ignite.binary.BinaryRawWriter;
import org.apache.ignite.internal.binary.BinaryUtils;
import org.apache.ignite.internal.processors.query.GridQueryFieldMetadata;

/**
 * SQL listener column metadata.
 */
public class OdbcColumnMeta {
    /** Cache name. */
    private final String schemaName;

    /** Table name. */
    private final String tableName;

    /** Column name. */
    private final String columnName;

    /** Data type. */
    private final Class<?> dataType;

    /**
     * @param schemaName Cache name.
     * @param tableName Table name.
     * @param columnName Column name.
     * @param dataType Data type.
     */
    public OdbcColumnMeta(String schemaName, String tableName, String columnName, Class<?> dataType) {
        this.schemaName = OdbcUtils.addQuotationMarksIfNeeded(schemaName);
        this.tableName = tableName;
        this.columnName = columnName;
        this.dataType = dataType;
    }

    /**
     * @param info Field metadata.
     */
    public OdbcColumnMeta(GridQueryFieldMetadata info) {
        this.schemaName = OdbcUtils.addQuotationMarksIfNeeded(info.schemaName());
        this.tableName = info.typeName();
        this.columnName = info.fieldName();

        Class<?> type;

        try {
            type = Class.forName(info.fieldTypeName());
        }
        catch (Exception ignored) {
            type = Object.class;
        }

        this.dataType = type;
    }

    /** {@inheritDoc} */
    @Override public int hashCode() {
        int hash = schemaName.hashCode();

        hash = 31 * hash + tableName.hashCode();
        hash = 31 * hash + columnName.hashCode();
        hash = 31 * hash + dataType.hashCode();

        return hash;
    }

    /** {@inheritDoc} */
    @Override public boolean equals(Object o) {
        if (o instanceof OdbcColumnMeta) {
            OdbcColumnMeta other = (OdbcColumnMeta) o;

            return this == other || schemaName.equals(other.schemaName) && tableName.equals(other.tableName) &&
                columnName.equals(other.columnName) && dataType.equals(other.dataType);
        }

        return false;
    }

    /**
     * Write in a binary format.
     *
     * @param writer Binary writer.
     */
    public void write(BinaryRawWriter writer) {
        writer.writeString(schemaName);
        writer.writeString(tableName);
        writer.writeString(columnName);

        byte typeId = BinaryUtils.typeByClass(dataType);

        writer.writeByte(typeId);
    }
}
