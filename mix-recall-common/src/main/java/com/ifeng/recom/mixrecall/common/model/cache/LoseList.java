package com.ifeng.recom.mixrecall.common.model.cache;

import java.util.ArrayList;
import java.util.Collection;

public class LoseList extends ArrayList {

    public static final LoseList loseList = new LoseList();

    public LoseList(int initialCapacity) {
        super();
    }

    public LoseList() {
        super();
    }

    public LoseList(Collection c) {
        super();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof LoseList;
    }
}
