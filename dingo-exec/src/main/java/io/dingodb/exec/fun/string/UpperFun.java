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

package io.dingodb.exec.fun.string;

import io.dingodb.expr.runtime.RtExpr;
import io.dingodb.expr.runtime.op.RtStringFun;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;

@Slf4j
public class UpperFun extends RtStringFun {
    public static final String NAME = "upper";
    private static final long serialVersionUID = -4349256259193399655L;

    /**
     * Create an RtToUpperCaseOp. RtToUpperCaseOp converts a String to upper case by {@code String::toUpperCase}.
     *
     * @param paras the parameters of the op
     */
    public UpperFun(RtExpr[] paras) {
        super(paras);
    }

    public static String toUpCase(final String str) {
        if (str == null || str.equals("")) {
            return str;
        } else {
            return str.toUpperCase();
        }
    }

    @Override
    protected Object fun(Object @NonNull [] values) {
        return toUpCase((String) values[0]);
    }
}