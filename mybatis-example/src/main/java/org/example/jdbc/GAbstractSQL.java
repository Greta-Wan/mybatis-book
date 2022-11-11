package org.example.jdbc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class GAbstractSQL {
    private static final String AND = ") \nAND (";
    private static final String OR = ") \nOR (";

    private final SqlStatement sqlStatement = new SqlStatement();

    private static class SqlStatement{
        private enum SqlStatementType{
            DELETE, SELECT, UPDATE, INSERT
        }

        private SqlStatementType statementType;

        List<String> sets = new ArrayList<>();
        List<String> select = new ArrayList<>();
        List<String> tables = new ArrayList<>();
        List<String> join = new ArrayList<>();
        List<String> innerJoin = new ArrayList<>();
        List<String> outerJoin = new ArrayList<>();
        List<String> leftOuterJoin = new ArrayList<>();
        List<String> rightOuterJoin = new ArrayList<>();
        List<String> where = new ArrayList<>();
        List<String> having = new ArrayList<>();
        List<String> groupBy = new ArrayList<>();
        List<String> orderBy = new ArrayList<>();
        List<String> lastList = new ArrayList<>();
        List<String> columns = new ArrayList<>();
        List<String> values = new ArrayList<>();

        boolean distinct;
        public SqlStatement(){

        }

        private void sqlClause(SafeAppendable builder, String keyword, List<String> parts, String open, String close,
                               String conjunction) {
            if (parts.isEmpty()) {
                return;
            }

            if (!builder.isEmpty()) {
                builder.append("\n");
            }
            builder.append(open);

            builder.append(keyword);
            builder.append(" ");
            for(int i = 0; i < parts.size(); i++) {
                String part = parts.get(i);
                if (i > 0 && !part.equals(OR) && !part.equals(AND)) {
                    builder.append(conjunction);
                }

                builder.append(part);
            }

            builder.append(close);
        }


    }

    private static class SafeAppendable{
        private final Appendable a;
        private boolean empty = true;


        private SafeAppendable(Appendable a) {
            this.a = a;
        }

        public SafeAppendable append(CharSequence s) {
            try {
                if (empty && s.length() > 0) {
                    empty = false;
                }

                a.append(s);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        private boolean isEmpty() {
            return empty;
        }
    }
}
