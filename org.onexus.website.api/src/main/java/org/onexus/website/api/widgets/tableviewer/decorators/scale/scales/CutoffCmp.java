/**
 *  Copyright 2012 Universitat Pompeu Fabra.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package org.onexus.website.api.widgets.tableviewer.decorators.scale.scales;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public abstract class CutoffCmp implements Serializable {

    private static final long serialVersionUID = 6256197968707025432L;

    public static final CutoffCmp LT = new CutoffCmp("<", "less than") {
        @Override
        public boolean compare(double value, double cutoff) {
            return value < cutoff;
        }
    };

    public static final CutoffCmp LE = new CutoffCmp("<=", "less or equal") {
        @Override
        public boolean compare(double value, double cutoff) {
            return value <= cutoff;
        }
    };

    public static final CutoffCmp EQ = new CutoffCmp("=", "equal") {
        @Override
        public boolean compare(double value, double cutoff) {
            return value == cutoff;
        }
    };

    public static final CutoffCmp NE = new CutoffCmp("!=", "not equal") {
        @Override
        public boolean compare(double value, double cutoff) {
            return value != cutoff;
        }
    };

    public static final CutoffCmp GT = new CutoffCmp(">", "greater than") {
        @Override
        public boolean compare(double value, double cutoff) {
            return value > cutoff;
        }
    };

    public static final CutoffCmp GE = new CutoffCmp(">=", "greater or equal") {
        @Override
        public boolean compare(double value, double cutoff) {
            return value >= cutoff;
        }
    };

    public static final CutoffCmp ABS_LT = new CutoffCmp("abs <",
            "absolute less than") {
        @Override
        public boolean compare(double value, double cutoff) {
            return value < cutoff;
        }
    };

    public static final CutoffCmp ABS_LE = new CutoffCmp("abs <=",
            "absolute less or equal") {
        @Override
        public boolean compare(double value, double cutoff) {
            return value <= cutoff;
        }
    };

    public static final CutoffCmp ABS_EQ = new CutoffCmp("abs =",
            "absolute equal") {
        @Override
        public boolean compare(double value, double cutoff) {
            return Math.abs(value) == cutoff;
        }
    };

    public static final CutoffCmp ABS_NE = new CutoffCmp("abs !=",
            "absolute not equal") {
        @Override
        public boolean compare(double value, double cutoff) {
            return Math.abs(value) != cutoff;
        }
    };

    public static final CutoffCmp ABS_GT = new CutoffCmp("abs >",
            "absolute greater than") {
        @Override
        public boolean compare(double value, double cutoff) {
            return Math.abs(value) > cutoff;
        }
    };

    public static final CutoffCmp ABS_GE = new CutoffCmp("abs >=",
            "absolute greater or equal") {
        @Override
        public boolean compare(double value, double cutoff) {
            return Math.abs(value) >= cutoff;
        }
    };

    public static final CutoffCmp[] COMPARATORS = new CutoffCmp[]{LT, LE, GT,
            GE, EQ, NE, ABS_LT, ABS_LE, ABS_GT, ABS_GE, ABS_EQ, ABS_NE};

    public static final Map<String, CutoffCmp> ABBREVIATED_NAME_MAP = new HashMap<String, CutoffCmp>();
    public static final Map<String, CutoffCmp> SHORT_NAME_MAP = new HashMap<String, CutoffCmp>();
    public static final Map<String, CutoffCmp> LONG_NAME_MAP = new HashMap<String, CutoffCmp>();
    public static final Map<String, CutoffCmp> NAME_MAP = new HashMap<String, CutoffCmp>();

    static {
        ABBREVIATED_NAME_MAP.put("lt", CutoffCmp.LT);
        ABBREVIATED_NAME_MAP.put("le", CutoffCmp.LE);
        ABBREVIATED_NAME_MAP.put("gt", CutoffCmp.GT);
        ABBREVIATED_NAME_MAP.put("ge", CutoffCmp.GE);
        ABBREVIATED_NAME_MAP.put("eq", CutoffCmp.EQ);
        ABBREVIATED_NAME_MAP.put("ne", CutoffCmp.NE);
        ABBREVIATED_NAME_MAP.put("alt", CutoffCmp.ABS_LT);
        ABBREVIATED_NAME_MAP.put("ale", CutoffCmp.ABS_LE);
        ABBREVIATED_NAME_MAP.put("agt", CutoffCmp.ABS_GT);
        ABBREVIATED_NAME_MAP.put("age", CutoffCmp.ABS_GE);
        ABBREVIATED_NAME_MAP.put("aeq", CutoffCmp.ABS_EQ);
        ABBREVIATED_NAME_MAP.put("ane", CutoffCmp.ABS_NE);

        for (CutoffCmp cmp : COMPARATORS) {
            SHORT_NAME_MAP.put(cmp.getShortName(), cmp);
            LONG_NAME_MAP.put(cmp.getLongName(), cmp);
            NAME_MAP.put(cmp.getShortName(), cmp);
            NAME_MAP.put(cmp.getLongName(), cmp);
        }
    }

    private String shortName;
    private String longName;

    public CutoffCmp(String shortName, String longName) {
        this.shortName = shortName;
        this.longName = longName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    public abstract boolean compare(double value, double cutoff);

    @Override
    public String toString() {
        return shortName;
    }
}
